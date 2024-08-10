import com.oocourse.elevator2.Request;

import java.util.ArrayList;

public class Scheduler extends Thread {
    private SchedulerWaitingQueue schedulerWaitingQueue;
    private ArrayList<ElevatorWaitingQueue> elevatorWaitingQueues;

    public Scheduler(SchedulerWaitingQueue wq, ArrayList<ElevatorWaitingQueue> ewqs) {
        this.schedulerWaitingQueue = wq;
        this.elevatorWaitingQueues = ewqs;
    }

    @Override
    public void run() {
        while (!schedulerWaitingQueue.isEnd() || !schedulerWaitingQueue.isEmpty()) {
            if (schedulerWaitingQueue.isEmpty()) {
                synchronized (schedulerWaitingQueue) {
                    try {
                        schedulerWaitingQueue.wait();
                        schedulerWaitingQueue.notifyAll();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            Request request = this.schedulerWaitingQueue.getOneRequestAndRemove();
            if (request != null) {
                Person person = (Person) request;
                int elevatorId = person.getIdealElevator(elevatorWaitingQueues);
                this.elevatorWaitingQueues.get(elevatorId - 1).addRequest(person);
            } else  {
                synchronized (schedulerWaitingQueue) {
                    try {
                        schedulerWaitingQueue.wait();
                        schedulerWaitingQueue.notifyAll();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        for (ElevatorWaitingQueue elevatorWaitingQueue : elevatorWaitingQueues) {
            elevatorWaitingQueue.setEnd();
        }
    }

    public SchedulerWaitingQueue getSchedulerWaitingQueue() {
        return schedulerWaitingQueue;
    }

    public synchronized void reduceReset() {
        schedulerWaitingQueue.reduce();
        this.notifyAll();
    }
}
