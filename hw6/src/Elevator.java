import com.oocourse.elevator2.ResetRequest;
import com.oocourse.elevator2.TimableOutput;

import java.util.ArrayList;

public class Elevator extends Thread {
    private Argument argument;
    private int direction;      //1上 0停 -1下
    private boolean status;     //true开 false关
    private boolean reset;      //true修 false不修
    private ElevatorWaitingQueue elevatorWaitingQueue;  //请求队列  桌子
    private Scheduler scheduler;
    private int position;
    private int end;
    private Controller controller;

    public Elevator(int id, Scheduler s, ElevatorWaitingQueue waitingQueue) {
        argument = new Argument(id);
        direction = 0;
        status = false;
        reset = false;
        elevatorWaitingQueue = waitingQueue;
        scheduler = s;
        position = 1;
        end = 1;
        controller = new Controller(this);
    }

    public int getElevatorID() {
        return argument.getElevatorId();
    }

    @Override
    public void run() {
        while (!elevatorWaitingQueue.isEnd()) {
            try {
                elevatorWaitingQueue.getRequest();
                if (reset) {
                    handleResetRequest(elevatorWaitingQueue.getResetRequest());
                }
                if (controller.isOut()) {
                    open();
                    out();
                }
                if (controller.isIn()) {
                    in();
                }
                shut();
                if (reset) {
                    handleResetRequest(elevatorWaitingQueue.getResetRequest());
                }
                //已经最后时刻了
                end = controller.getEnd();    //寻找目标
                while (position != end) {
                    if (reset) {
                        handleResetRequest(elevatorWaitingQueue.getResetRequest());
                    }
                    if (direction == 1) {
                        up();
                        direction = position == end ? 0 : direction;
                    } else if (direction == -1) {
                        down();
                        direction = position == end ? 0 : direction;
                    }
                    if (controller.isOut()) {
                        open();
                        out();
                    }
                    if (reset) {
                        handleResetRequest(elevatorWaitingQueue.getResetRequest());
                    }
                    if (controller.isIn()) {
                        in();
                    }
                    shut();
                    if (reset) {
                        handleResetRequest(elevatorWaitingQueue.getResetRequest());
                    }
                    if (direction == 0) {
                        end = controller.getEnd();
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public synchronized void handleResetRequest(ResetRequest resetRequest)
            throws InterruptedException {
        ArrayList<Person> toAllocate = new ArrayList<>();
        if (!elevatorWaitingQueue.isEmpty(elevatorWaitingQueue.getPersonOutRequests()) ||
                !elevatorWaitingQueue.isEmpty(elevatorWaitingQueue.getPersonInRequests())) {
            elevatorWaitingQueue.addAll(toAllocate);
        }
        shut();
        direction = 0;
        synchronized (elevatorWaitingQueue) {
            elevatorWaitingQueue.reset(resetRequest);
            if (!toAllocate.isEmpty()) {
                for (Person person : toAllocate) {
                    scheduler.getSchedulerWaitingQueue().addRequest(person);
                }
            }
            elevatorWaitingQueue.notifyAll();
        }
        scheduler.reduceReset();

    }

    private void up() throws InterruptedException {
        if (status) {
            shut();     //门现在开着
        }
        sleep(argument.getMoveTime());
        position++;
        TimableOutput.println("ARRIVE-" + position + "-" + getElevatorID());
    }

    private void down() throws InterruptedException {
        if (status) {
            shut();     //门现在开着
        }
        sleep(argument.getMoveTime());
        position--;
        TimableOutput.println("ARRIVE-" + position + "-" + getElevatorID());
    }

    public void in() throws InterruptedException {
        open();
        if (status) {
            elevatorWaitingQueue.inPerson(position);
        } else {
            System.err.println("Door is closed. Can't In");
        }
    }

    public void out() throws InterruptedException {
        if (status) {
            elevatorWaitingQueue.outPerson(position);
        } else {
            System.err.println("Door is closed. Can't Out");
        }
    }

    public void open() throws InterruptedException {
        if (!status) {
            TimableOutput.println("OPEN-" + position + "-" + getElevatorID());
            sleep(argument.getOpenTime());
            status = true;
        }
    }

    private void shut() throws InterruptedException {
        if (status) {       //如果门开着，那么关上
            sleep(argument.getShutTime());
            TimableOutput.println("CLOSE-" + position + "-" + getElevatorID());
            status = false;
        }
    }

    public synchronized void setReset(boolean flag) {
        reset = flag;
        this.notifyAll();
    }

    public ElevatorWaitingQueue getElevatorWaitingQueue() {
        return elevatorWaitingQueue;
    }

    public int getPosition() {
        return position;
    }

    public void setDirection(int end) {
        direction = Integer.compare(end, position);
    }

    public int getTotal() {
        return controller.getSum();
    }

    public int getMaxCapacity() {
        return argument.getMaxCapacity();
    }

    public int getDirection() {
        return direction;
    }

    public void changeSum(int n) {
        controller.changeSum(n);
    }

    public void changeSum() {   //装满
        controller.changeSum();
    }

    public void clear() {
        controller.clear();
    }

    public void changeMoveTime(double speed) {
        argument.changeMoveTime((int) (speed * 1000));
    }

    public void changeCapacity(int maxCapacity) {
        argument.changeCapacity(maxCapacity);
    }

    public Argument getArgument() {
        return argument;
    }

    public int getEnd() {
        return end;
    }

    public boolean getStatus() {
        return status;
    }

    public synchronized boolean getReset() {
        this.notifyAll();
        return reset;
    }
}
