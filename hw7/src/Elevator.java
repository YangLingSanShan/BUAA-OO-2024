
import com.oocourse.elevator3.DoubleCarResetRequest;
import com.oocourse.elevator3.NormalResetRequest;
import com.oocourse.elevator3.ResetRequest;
import com.oocourse.elevator3.TimableOutput;

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
    private boolean isDouble;
    private char sign = ' ';
    private int transfer;

    public Elevator(int id, Scheduler s, ElevatorWaitingQueue waitingQueue) {
        argument = new Argument(id);
        direction = 0;
        status = false;
        reset = false;
        elevatorWaitingQueue = waitingQueue;
        scheduler = s;
        position = 1;
        end = 1;
        isDouble = false;
        controller = new Controller(this);
    }

    public Elevator(Elevator elevator, DoubleCarResetRequest doubleCarResetRequest,
                    ElevatorWaitingQueue elevatorWaitingQueue, char sign) {
        this.argument = new Argument(elevator.argument);
        transfer = doubleCarResetRequest.getTransferFloor();
        direction = 0;
        status = false;
        reset = false;
        this.elevatorWaitingQueue = elevatorWaitingQueue;
        scheduler = elevator.scheduler;
        position = sign == 'A' ? transfer - 1 : transfer + 1;
        end = position;
        isDouble = true;
        this.sign = sign;
        if (sign == 'A') {
            argument.changeMaxFloor(doubleCarResetRequest.getTransferFloor());
        } else {
            argument.changeMinFloor(doubleCarResetRequest.getTransferFloor());
        }
        controller = new CarController(this);
    }

    public int getElevatorID() {
        return argument.getElevatorId();
    }

    @Override
    public void run() {
        while (elevatorWaitingQueue.isEnd(argument.getMinFloor(), argument.getMaxFloor())) {
            try {
                elevatorWaitingQueue.getRequest(argument.getMinFloor(), argument.getMaxFloor());
                if (handleResetRequest()) {
                    break;
                }
                if (controller.isOut()) {
                    out();
                }
                if (controller.isIn()) {
                    in();
                }
                if (handleResetRequest()) {
                    break;
                }
                //已经最后时刻了
                end = controller.getEnd();    //寻找目标
                while (position != end) {
                    if (handleResetRequest()) {
                        break;
                    }
                    if (direction == 1) {
                        up();
                    } else if (direction == -1) {
                        down();
                    }
                    if (controller.isOut()) {
                        out();
                    }
                    if (handleResetRequest()) {
                        break;
                    }
                    if (controller.isIn()) {
                        in();
                    }
                    if (handleResetRequest()) {
                        break;
                    }
                    end = direction == 0 ? controller.getEnd() : end;
                }
                if (isDouble) {
                    break;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        //System.out.println(getElevatorID());
    }

    public synchronized boolean handleResetRequest() throws InterruptedException {
        if (reset) {
            ResetRequest request = elevatorWaitingQueue.getResetRequest();
            if (request instanceof NormalResetRequest) {
                return handleResetRequest((NormalResetRequest) request);
            } else if (request instanceof DoubleCarResetRequest) {
                return handleResetRequest((DoubleCarResetRequest) request);
            }
        }
        this.notifyAll();
        return false;
    }

    //改双箱
    private synchronized boolean handleResetRequest(DoubleCarResetRequest request)
            throws InterruptedException {
        isDouble = true;
        elevatorWaitingQueue.setDouble();
        ArrayList<Person> toAllocate = new ArrayList<>();
        synchronized (elevatorWaitingQueue) {
            if (!elevatorWaitingQueue.isEmpty(elevatorWaitingQueue.getPersonOutRequests(),
                    argument.getMinFloor(), argument.getMaxFloor()) ||
                    !elevatorWaitingQueue.isEmpty(elevatorWaitingQueue.getPersonInRequests(),
                            argument.getMinFloor(), argument.getMaxFloor())) {
                elevatorWaitingQueue.addAll(toAllocate, this);
            }
            shut();
            direction = 0;
            elevatorWaitingQueue.reset(request);
            if (!toAllocate.isEmpty()) {
                for (Person person : toAllocate) {
                    scheduler.getSchedulerWaitingQueue().addRequest(person);
                }
            }
            elevatorWaitingQueue.notifyAll();
        }
        scheduler.reduceReset();
        this.notifyAll();
        return true;
    }

    //正常Reset
    public synchronized boolean handleResetRequest(NormalResetRequest resetRequest)
            throws InterruptedException {
        ArrayList<Person> toAllocate = new ArrayList<>();
        synchronized (elevatorWaitingQueue) {
            if (!elevatorWaitingQueue.isEmpty(elevatorWaitingQueue.getPersonOutRequests(),
                    argument.getMinFloor(), argument.getMaxFloor()) ||
                    !elevatorWaitingQueue.isEmpty(elevatorWaitingQueue.getPersonInRequests(),
                            argument.getMinFloor(), argument.getMaxFloor())) {
                elevatorWaitingQueue.addAll(toAllocate, this);
            }
            shut();
            direction = 0;
            elevatorWaitingQueue.reset(resetRequest);
            if (!toAllocate.isEmpty()) {
                for (Person person : toAllocate) {
                    scheduler.getSchedulerWaitingQueue().addRequest(person);
                }
            }
            elevatorWaitingQueue.notifyAll();
        }
        scheduler.reduceReset();
        this.notifyAll();
        return false;
    }

    public void up() throws InterruptedException {
        if (status) {
            shut();     //门现在开着
        }
        sleep(argument.getMoveTime());
        position++;
        if (isDouble && sign != ' ') {
            TimableOutput.println("ARRIVE-" + position + "-" + getElevatorID() + "-" + sign);
        } else {
            TimableOutput.println("ARRIVE-" + position + "-" + getElevatorID());
        }
        changeDirection();
    }

    public void down() throws InterruptedException {
        if (status) {
            shut();     //门现在开着
        }
        sleep(argument.getMoveTime());
        position--;
        if (isDouble && sign != ' ') {
            TimableOutput.println("ARRIVE-" + position + "-" + getElevatorID() + "-" + sign);
        } else {
            TimableOutput.println("ARRIVE-" + position + "-" + getElevatorID());
        }
        changeDirection();
    }

    public void in() throws InterruptedException {
        open();
        if (status) {
            elevatorWaitingQueue.inPerson(position,
                    argument.getMinFloor(), argument.getMaxFloor(), this);
        } else {
            System.err.println("Door is closed. Can't In");
        }
        shut();
    }

    public void out() throws InterruptedException {
        open();
        if (status) {
            synchronized (elevatorWaitingQueue) {
                ArrayList<Person> people = elevatorWaitingQueue.outPerson(position, this);
                if (!people.isEmpty()) {
                    for (Person person : people) {
                        person.changeFromFloor(position);
                        scheduler.getSchedulerWaitingQueue().addRequest(person);
                    }
                    scheduler.reduceTrans(people.size());           //?
                }
                elevatorWaitingQueue.notifyAll();
            }
        } else {
            System.err.println("Door is closed. Can't Out");
        }
        shut();
    }

    public void open() throws InterruptedException {
        if (!status) {
            if (isDouble && sign != ' ') {
                TimableOutput.println("OPEN-" + position + "-" + getElevatorID() + "-" + sign);
            } else {
                TimableOutput.println("OPEN-" + position + "-" + getElevatorID());
            }
            sleep(argument.getOpenTime());
            status = true;
        }
    }

    public void shut() throws InterruptedException {
        if (status) {       //如果门开着，那么关上
            sleep(argument.getShutTime());
            if (isDouble && sign != ' ') {
                TimableOutput.println("CLOSE-" + position + "-" + getElevatorID() + "-" + sign);
            } else {
                TimableOutput.println("CLOSE-" + position + "-" + getElevatorID());
            }
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

    public void changeDirection() {
        direction = position == end ? 0 : direction;
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

    //    public synchronized boolean getReset() {
    //        this.notifyAll();
    //        return reset;
    //    }

    public boolean getReset() {
        return reset;
    }

    public boolean isDouble() {
        return isDouble;
    }

    public void changePosition(int position) {
        this.position += position;
    }

    public void changeEnd(int end) {
        this.end = end;
    }

    public char getSign() {
        return sign;
    }

    public int getTransfer() {
        return transfer;
    }

    public void setTransfer(int transferFloor) {
        transfer = transferFloor;
    }

    public Controller getController() {
        return controller;
    }

    public int getMinFloor() {
        return argument.getMinFloor();
    }

    public int getMaxFloor() {
        return argument.getMaxFloor();
    }
}
