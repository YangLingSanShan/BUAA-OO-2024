import java.util.ArrayList;

public class VirtualController {
    private VirtualElevator virtualElevator;
    private int sum = 0;

    public VirtualController(VirtualElevator ve) {
        virtualElevator = ve;
    }

    public int getTotal() {
        return sum;
    }

    public void changeTotal(int size) {
        sum += size;
    }

    public void changeTotal() {
        sum = virtualElevator.getCapacity();
    }

    public boolean isOut() {
        ArrayList<Person> now = virtualElevator.getOutRequests().get(virtualElevator.getPosition());
        return !now.isEmpty();
    }

    public boolean isIn() {
        if (sum >= virtualElevator.getCapacity()) {
            return false;
        }
        return !virtualElevator.getInRequests().get(virtualElevator.getPosition()).isEmpty();
    }

    public int changeEnd() {
        int end = virtualElevator.getPosition();
        for (int i = 11; i >= end; i--) {
            if (!virtualElevator.getInRequests().get(i).isEmpty() ||
                    !virtualElevator.getOutRequests().get(i).isEmpty()) {
                end = i;
                break;
            }
        }
        if (end > virtualElevator.getPosition()) {
            virtualElevator.changeDirection(end);
            return end;
        } else {
            for (int i = 1; i < end; i++) {
                if (!virtualElevator.getInRequests().get(i).isEmpty() ||
                        !virtualElevator.getOutRequests().get(i).isEmpty()) {
                    end = i;
                    break;
                }
            }
            virtualElevator.changeDirection(end);
            return end;
        }
    }
}
