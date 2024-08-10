import com.oocourse.elevator2.ElevatorInput;
import com.oocourse.elevator2.PersonRequest;
import com.oocourse.elevator2.Request;
import com.oocourse.elevator2.ResetRequest;

import java.util.ArrayList;

public class InputThread extends Thread {

    private SchedulerWaitingQueue schedulerWaitingQueue;
    private ArrayList<ElevatorWaitingQueue> elevatorWaitingQueues;

    public InputThread(SchedulerWaitingQueue queue, ArrayList<ElevatorWaitingQueue> ewqs) {
        schedulerWaitingQueue = queue;
        elevatorWaitingQueues = ewqs;
    }

    @Override
    public void run() {
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        while (true) {
            Request request = elevatorInput.nextRequest();
            if (request == null) {
                schedulerWaitingQueue.setEnd();
                break;
            } else {
                if (request instanceof PersonRequest) { //人
                    synchronized (schedulerWaitingQueue) {
                        schedulerWaitingQueue.addRequest((PersonRequest) request);
                        schedulerWaitingQueue.notifyAll();
                    }
                } else {                                //重置
                    ResetRequest resetRequest = (ResetRequest) request;
                    int id = resetRequest.getElevatorId();
                    synchronized (elevatorWaitingQueues.get(id - 1)) {
                        elevatorWaitingQueues.get(id - 1).addRequest(resetRequest);
                        synchronized (schedulerWaitingQueue) {
                            schedulerWaitingQueue.addRequest(1);
                            schedulerWaitingQueue.notifyAll();
                        }
                    }
                }
            }

        }

    }
}
