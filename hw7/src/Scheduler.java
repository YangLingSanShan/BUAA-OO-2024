import com.oocourse.elevator3.Request;

import java.util.ArrayList;
import java.util.Random;

public class Scheduler extends Thread {
    private SchedulerWaitingQueue schedulerWaitingQueue;
    private ArrayList<ElevatorWaitingQueue> elevatorWaitingQueues;
    private Random numList = new Random(6);

    public Scheduler(SchedulerWaitingQueue wq, ArrayList<ElevatorWaitingQueue> ewqs) {
        this.schedulerWaitingQueue = wq;
        this.elevatorWaitingQueues = ewqs;
    }

    @Override
    public void run() {
        while (!schedulerWaitingQueue.isEnd() || !schedulerWaitingQueue.isEmpty()
                || schedulerWaitingQueue.hasTrans()) {
            if (schedulerWaitingQueue.isEmpty() && schedulerWaitingQueue.hasTrans()) {
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
                int elevatorId = numList.nextInt(6);
                ElevatorWaitingQueue queue = this.elevatorWaitingQueues.get(elevatorId);
                synchronized (queue) {
                    queue.addRequest(person);
                    queue.notifyAll();
                }
                if (queue.isDouble() && person.needTransfer(queue.getTransfer())) {
                    schedulerWaitingQueue.addTrans();
                }

            } else {
                synchronized (schedulerWaitingQueue) {
                    if (getSchedulerWaitingQueue().hasTrans()) {
                        try {
                            schedulerWaitingQueue.wait();
                            schedulerWaitingQueue.notifyAll();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
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

    public synchronized void reduceTrans(int sum) {
        schedulerWaitingQueue.subTrans(sum);
        this.notifyAll();
    }
}
