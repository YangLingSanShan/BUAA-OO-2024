import com.oocourse.elevator1.ElevatorInput;
import com.oocourse.elevator1.PersonRequest;
import java.util.ArrayList;

public class Distributor extends Thread {
    private int elevatorSum;
    private int minFloor;
    private int maxFloor;
    private static final int initPosition = 1;
    private static final int moveTime = 400;
    private static final int openTime = 200;
    private static final int shutTime = 200;
    private static final int maxCapacity = 6;
    private Argument argument;
    private ArrayList<Elevator> elevators = new ArrayList<>();  //电梯序列
    private ElevatorInput elevatorInput;                        //输入

    public Distributor(int sum, int max, int min) {
        elevatorSum = sum;
        maxFloor = max;
        minFloor = min;
        argument = new Argument(maxFloor, minFloor, initPosition, moveTime,
                openTime, shutTime, maxCapacity);
        elevators.add(0, null);
        for (int i = 1; i <= elevatorSum; i++) {
            Elevator elevator = new Elevator(i, argument);
            elevators.add(i, elevator);
            elevator.start();
        }
    }

    @Override
    public void run() {
        elevatorInput = new ElevatorInput(System.in);
        while (!interrupted()) {
            PersonRequest request = elevatorInput.nextPersonRequest();
            if (request != null) {      //有新的请求
                Person person = new Person(request);
                try {
                    distributeRequest(person, person.getStartElevator());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                setEnd();
                break;
            }
        }
        //System.out.println("distri out");
    }

    public void distributeRequest(Person person, int id) throws InterruptedException {
        elevators.get(id).addRequest(person);
    }

    public void setEnd() {
        for (int i = 1; i <= elevatorSum; i++) {
            elevators.get(i).setEnd();
        }
    }
}
