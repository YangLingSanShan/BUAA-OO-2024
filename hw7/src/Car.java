import com.oocourse.elevator3.DoubleCarResetRequest;
import com.oocourse.elevator3.TimableOutput;

import java.util.concurrent.Semaphore;

public class Car extends Elevator {

    private Semaphore semaphore;

    public Car(Elevator elevator, DoubleCarResetRequest doubleCarResetRequest,
               ElevatorWaitingQueue elevatorWaitingQueue, char sign, Semaphore semaphore) {
        super(elevator, doubleCarResetRequest, elevatorWaitingQueue, sign);
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        ElevatorWaitingQueue elevatorWaitingQueue = getElevatorWaitingQueue();
        Argument argument = getArgument();
        CarController controller = (CarController) getController();
        while (elevatorWaitingQueue.isEnd(argument.getMinFloor(), argument.getMaxFloor())) {
            try {
                elevatorWaitingQueue.getRequest(argument.getMinFloor(), argument.getMaxFloor());
                if (controller.isOut()) {
                    out();
                }
                if (controller.isIn()) {
                    in();
                }
                //已经最后时刻了
                changeEnd(controller.getEnd());    //寻找目标
                while (getPosition() != getEnd()) {
                    if (getDirection() == 1) {
                        up();
                    } else if (getDirection() == -1) {
                        down();
                    }
                    if (controller.isOut()) {
                        out();
                    }
                    if (getPosition() == getTransfer()) {
                        if (getSign() == 'A') {     //A接下楼的
                            inCertainDirection(-1);
                            changeEnd(controller.getEnd());
                            if (getDirection() == 0) {
                                changeEnd(getTransfer() - 1);
                                changeDirection();
                            }
                            down();
                            semaphore.release();
                        } else {                    //B接上楼的
                            inCertainDirection(1);
                            changeEnd(controller.getEnd());
                            if (getDirection() == 0) {
                                changeEnd(getTransfer() + 1);
                                changeDirection();
                            }
                            up();
                            semaphore.release();
                        }
                    } else {
                        if (controller.isIn()) {
                            in();
                        }
                    }
                    //2号 在transfer out 然后 又从transfer进来
                    changeEnd(getDirection() == 0 ? controller.getEnd() : getEnd());
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void inCertainDirection(int direction) throws InterruptedException {
        CarController controller = (CarController) getController();
        ElevatorWaitingQueue elevatorWaitingQueue = getElevatorWaitingQueue();
        if (controller.isCertain(getPosition(), direction)) {
            open();
            if (getStatus()) {
                elevatorWaitingQueue.inCertainDirection(direction, getPosition()
                        , this.getMinFloor(), this.getMaxFloor(), this);
            } else {
                System.err.println("Door is closed. Can't In");
            }
            shut();
        }
    }

    @Override
    public void up() throws InterruptedException {
        Argument argument = getArgument();
        if (getStatus()) {
            shut();     //门现在开着
        }
        if (getPosition() + 1 == getTransfer()) {
            try {
                semaphore.acquire(); // 获取许可
                sleep(argument.getMoveTime());
                changePosition(1);
                TimableOutput.println("ARRIVE-" + getPosition() + "-" +
                        getElevatorID() + "-" + getSign());
                changeDirection();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            changePosition(1);
            sleep(argument.getMoveTime());
            TimableOutput.println("ARRIVE-" + getPosition() + "-" +
                    getElevatorID() + "-" + getSign());
            changeDirection();
        }
    }

    @Override
    public void down() throws InterruptedException {
        Argument argument = getArgument();
        if (getStatus()) {
            shut();     //门现在开着
        }
        if (getPosition() - 1 == getTransfer()) {   //马上要下到transfer
            try {
                semaphore.acquire(); // 获取许可
                sleep(argument.getMoveTime());
                changePosition(-1);                 //下到transfer
                TimableOutput.println("ARRIVE-" + getPosition() + "-" +
                        getElevatorID() + "-" + getSign());
                changeDirection();                  //此时direction必然是0？
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            changePosition(-1);
            sleep(argument.getMoveTime());
            TimableOutput.println("ARRIVE-" + getPosition() + "-" +
                    getElevatorID() + "-" + getSign());
            changeDirection();
        }
    }
}
