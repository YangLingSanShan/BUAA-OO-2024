import com.oocourse.elevator3.ElevatorInput;
import com.oocourse.elevator3.NormalResetRequest;
import com.oocourse.elevator3.Request;
import com.oocourse.elevator3.ResetRequest;
import com.oocourse.elevator3.DoubleCarResetRequest;
import com.oocourse.elevator3.PersonRequest;
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
                    int id;
                    ResetRequest resetRequest = (ResetRequest) request;
                    if (resetRequest instanceof NormalResetRequest) {
                        NormalResetRequest normalResetRequest = (NormalResetRequest) request;
                        id = normalResetRequest.getElevatorId();
                    } else {
                        DoubleCarResetRequest doubleCarResetRequest
                                = (DoubleCarResetRequest) request;
                        id = doubleCarResetRequest.getElevatorId();
                    }
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
