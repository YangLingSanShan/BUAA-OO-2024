import com.oocourse.elevator1.PersonRequest;

public class Person {
    private final PersonRequest personRequest;
    private int nowElevator;

    public Person(PersonRequest request) {
        personRequest = request;
    }

    public int getFromFloor() {
        return personRequest.getFromFloor();
    }

    public int getToFloor() {
        return personRequest.getToFloor();
    }

    public int getPersonId() {
        return personRequest.getPersonId();
    }

    public int getStartElevator() {
        return personRequest.getElevatorId();
    }
}
