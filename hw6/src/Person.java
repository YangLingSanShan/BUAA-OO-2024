import com.oocourse.elevator2.PersonRequest;
import com.oocourse.elevator2.Request;

import java.util.ArrayList;

public class Person extends Request {

    private int personID;
    private int fromFloor;
    private int toFloor;
    private int nowElevatorID;
    private int direction;

    public Person(PersonRequest request) {
        personID = request.getPersonId();
        fromFloor = request.getFromFloor();
        toFloor = request.getToFloor();
        nowElevatorID = 1;
        direction = fromFloor < toFloor ? 1 : -1;
    }

    public Person(Person person) {
        personID = person.personID;
        fromFloor = person.fromFloor;
        toFloor = person.toFloor;
        nowElevatorID = person.nowElevatorID;
        direction = person.direction;
    }

    public int getPersonID() {
        return personID;
    }

    public int getFromFloor() {
        return fromFloor;
    }

    public int getToFloor() {
        return toFloor;
    }

    public int getNowElevatorID() {
        return nowElevatorID;
    }

    public int getDirection() {
        return direction;
    }

    public int getIdealElevator(ArrayList<ElevatorWaitingQueue> elevatorWaitingQueues) {
        int time = 0;
        int speed = 0;
        int capacity = 0;
        int nowPerson = 0;
        for (ElevatorWaitingQueue elevatorWaitingQueue : elevatorWaitingQueues) {
            VirtualElevator virtualElevator;
            synchronized (elevatorWaitingQueue.getElevator()) {
                virtualElevator = new VirtualElevator(elevatorWaitingQueue.getElevator());
            }
            virtualElevator.addInRequest(this);
            if (virtualElevator.getElevatorID() == 1) {
                time = virtualElevator.getReset() ? 1200 : 0;
                time += virtualElevator.run(this);
                speed = virtualElevator.getSpeed();
                capacity = virtualElevator.getCapacity();
                nowPerson = virtualElevator.getSum();
            } else {
                int tempTime = virtualElevator.getReset() ? 1200 : 0;
                tempTime += virtualElevator.run(this);
                if (tempTime < time) {
                    nowElevatorID = virtualElevator.getElevatorID();
                    time = tempTime;
                    speed = virtualElevator.getSpeed();
                    capacity = virtualElevator.getCapacity();
                    nowPerson = virtualElevator.getSum();
                } else if (tempTime == time && virtualElevator.getSpeed() < speed) {
                    nowElevatorID = virtualElevator.getElevatorID();
                    time = tempTime;
                    speed = virtualElevator.getSpeed();
                    capacity = virtualElevator.getCapacity();
                    nowPerson = virtualElevator.getSum();
                } else if (tempTime == time && virtualElevator.getSpeed() == speed &&
                        capacity - nowPerson
                                < virtualElevator.getCapacity() - virtualElevator.getSum()) {
                    nowElevatorID = virtualElevator.getElevatorID();
                    time = tempTime;
                    speed = virtualElevator.getSpeed();
                    capacity = virtualElevator.getCapacity();
                    nowPerson = virtualElevator.getSum();
                }
            }
        }
        return nowElevatorID;
    }

    public void changeFromFloor(int position) {
        fromFloor = position;
    }

    public void changeDirection() {
        direction = fromFloor < toFloor ? 1 : -1;
    }
}
