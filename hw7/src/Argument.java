public class Argument {

    private int elevatorId;
    private int moveTime = 400;
    private int maxCapacity = 6;
    private int maxFloor = 11;
    private int minFloor = 1;
    private final int openTime = 200;
    private final int shutTime = 200;
    private final int resetTime = 1200;

    public Argument(int id) {
        elevatorId = id;
    }

    public Argument(Argument argument) {
        elevatorId = argument.elevatorId;
        moveTime = argument.moveTime;
        maxCapacity = argument.maxCapacity;
        minFloor = argument.minFloor;
        maxFloor = argument.maxFloor;
    }

    public void changeMoveTime(int speed) {
        moveTime = speed;
    }

    public void changeCapacity(int capacity) {
        maxCapacity = capacity;
    }

    public int getMoveTime() {
        return moveTime;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public int getMaxFloor() {
        return maxFloor;
    }

    public int getMinFloor() {
        return minFloor;
    }

    public int getOpenTime() {
        return openTime;
    }

    public int getShutTime() {
        return shutTime;
    }

    public int getElevatorId() {
        return elevatorId;
    }

    public void changeMaxFloor(int transfer) {
        maxFloor = transfer;
    }

    public void changeMinFloor(int transfer) {
        minFloor = transfer;
    }
}
