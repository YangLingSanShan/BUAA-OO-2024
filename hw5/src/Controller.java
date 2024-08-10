public class Controller {

    private Argument argument;
    private Elevator elevator;
    private int total;

    public Controller(Argument a, Elevator e) {
        argument = a;
        elevator = e;
        total = 0;
    }

    public boolean isIn() {
        if (total >= argument.getMaxCapacity()) {
            return false;
        }
        synchronized (elevator.getInRequests()) {
            if (!elevator.getInRequests().get(elevator.getPosition()).isEmpty()) {
                elevator.getInRequests().notifyAll();
                return true;
            } else {
                elevator.getInRequests().notifyAll();
                return false;
            }
        }
    }

    public boolean isOut() {
        synchronized (elevator.getOutRequests()) {
            if (!elevator.getOutRequests().get(elevator.getPosition()).isEmpty()) {
                elevator.getOutRequests().notifyAll();
                return true;
            } else {
                elevator.getOutRequests().notifyAll();
                return false;
            }
        }
    }

    public int getDirection() {
        //找目的地
        //如果电梯静止：找最远的一个目标 (?)
        //如果电梯上行,从上行方向上找一个最远的，如果没有，则下行
        //下行同上
        synchronized (elevator.getInRequests()) {
            int end = elevator.getPosition();           // end = position 找离end最远的i
            if (elevator.getDirection() == 0) {
                for (int i = 1; i <= argument.getMaxFloor(); i++) {
                    if (!elevator.getInRequests().get(i).isEmpty()
                            || !elevator.getOutRequests().get(i).isEmpty()) {
                        if (Math.abs(i - elevator.getPosition()) >
                                Math.abs(end - elevator.getPosition())) {
                            end = i;
                        }
                    }
                }
            } else {
                if (elevator.getDirection() == 1) {
                    for (int i = argument.getMaxFloor(); i >= 1; i--) {
                        if (!elevator.getInRequests().get(i).isEmpty()
                                || !elevator.getOutRequests().get(i).isEmpty()) {
                            end = i;
                            break;
                        }
                    }
                } else if (elevator.getDirection() == -1) {
                    for (int i = 1; i <= argument.getMaxFloor(); i++) {
                        if (!elevator.getInRequests().get(i).isEmpty()
                                || !elevator.getOutRequests().get(i).isEmpty()) {
                            end = i;
                            break;
                        }
                    }
                }
            }
            elevator.changeDirection(end);
            elevator.getInRequests().notifyAll();
            return end;
        }
    }

    public void changeTotal(int n) {
        total += n;
    }

    public int getTotal() {
        return total;
    }

    public boolean isEnd() {
        synchronized (elevator.getInRequests()) {
            for (int i = 1; i <= argument.getMaxFloor(); i++) {
                if (!elevator.getInRequests().get(i).isEmpty()) {
                    return false;
                }
            }
        }
        synchronized (elevator.getOutRequests()) {
            for (int i = 1; i <= argument.getMaxFloor(); i++) {
                if (!elevator.getOutRequests().get(i).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
}
