import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.Request;

public class Person extends Request {

    private int personID;
    private int fromFloor;
    private int toFloor;
    private int direction;

    public Person(PersonRequest request) {
        personID = request.getPersonId();
        fromFloor = request.getFromFloor();
        toFloor = request.getToFloor();
        direction = fromFloor < toFloor ? 1 : -1;
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

    public int getDirection() {
        return direction;
    }

    public void changeFromFloor(int position) {
        fromFloor = position;
    }

    public void changeDirection() {
        direction = fromFloor < toFloor ? 1 : -1;
    }

    public boolean needTransfer(int transfer) {
        return (fromFloor < transfer && transfer < toFloor) ||
                (toFloor < transfer && transfer < fromFloor);
    }
}
