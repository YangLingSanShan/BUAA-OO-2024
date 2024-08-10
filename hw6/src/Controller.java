import java.util.ArrayList;

public class Controller {
    private Elevator elevator;
    private int sum = 0;
    private Argument argument;

    public Controller(Elevator e) {
        elevator = e;
        argument = e.getArgument();
    }

    public boolean isOut() {
        ArrayList<Person> out = elevator.getElevatorWaitingQueue().getOut(elevator.getPosition());
        return !out.isEmpty();
    }

    public boolean isIn() {
        if (sum >= argument.getMaxCapacity()) {
            return false;
        }
        ArrayList<Person> in = elevator.getElevatorWaitingQueue().getIn(elevator.getPosition());
        return !in.isEmpty();
    }

    public int getEnd() {
        int end = elevator.getPosition();
        for (int i = argument.getMaxFloor(); i >= end; i--) {
            if (!elevator.getElevatorWaitingQueue().getIn(i).isEmpty() ||
                    !elevator.getElevatorWaitingQueue().getOut(i).isEmpty()) {
                end = i;
                break;
            }
        }
        if (end > elevator.getPosition()) {
            elevator.setDirection(end);
            return end;
        } else {
            for (int i = argument.getMinFloor(); i < end; i++) {
                if (!elevator.getElevatorWaitingQueue().getIn(i).isEmpty() ||
                        !elevator.getElevatorWaitingQueue().getOut(i).isEmpty()) {
                    end = i;
                    break;
                }
            }
            elevator.setDirection(end);
            return end;
        }
    }

    public int getSum() {
        return sum;
    }

    public void changeSum(int n) {
        sum += n;
    }

    public void changeSum() {
        sum = argument.getMaxCapacity();
    }

    public void clear() {
        sum = 0;
    }
}
