import java.util.ArrayList;
import java.util.Iterator;

public class VirtualElevator {

    private int id;
    private int time = 0;
    private Argument argument;
    private int position;
    private int end;
    private boolean status;
    private boolean reset;
    private int direction;
    private VirtualController virtualController;
    private ArrayList<ArrayList<Person>> inRequests = new ArrayList<>();
    private ArrayList<ArrayList<Person>> outRequests = new ArrayList<>();

    public VirtualElevator(Elevator elevator) {
        id = elevator.getElevatorID();
        argument = new Argument(elevator.getArgument());
        position = elevator.getPosition();
        end = elevator.getEnd();
        status = elevator.getStatus();
        reset = elevator.getReset();
        direction = elevator.getDirection();
        inRequests = cloneArrayList(elevator.getElevatorWaitingQueue().getPersonInRequests());
        outRequests = cloneArrayList(elevator.getElevatorWaitingQueue().getPersonOutRequests());
        virtualController = new VirtualController(this);
    }

    public synchronized ArrayList<ArrayList<Person>>
        cloneArrayList(ArrayList<ArrayList<Person>> origin) {
        ArrayList<ArrayList<Person>> clone = new ArrayList<>();
        clone.add(null);
        for (int i = 1; i <= 11; i++) {
            ArrayList<Person> clonedSublist = new ArrayList<>();
            Iterator<Person> iterator = origin.get(i).iterator();
            while (iterator.hasNext()) {
                Person person = iterator.next();
                clonedSublist.add(new Person(person)); // Assuming Person has a copy constructor
            }
            clone.add(clonedSublist);
        }
        return clone;
    }

    public int run(Person person) {
        while (true) {
            if (virtualController.isOut()) {
                open();
                boolean flag = out(person);
                if (flag) {
                    return time;
                }
            }
            if (virtualController.isIn()) {
                in();
            }
            shut();
            end = virtualController.changeEnd();
            while (position != end) {
                //System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                if (direction == 1) {
                    up();
                    direction = position == end ? 0 : direction;
                } else if (direction == -1) {
                    down();
                    direction = position == end ? 0 : direction;
                }
                if (virtualController.isOut()) {
                    open();
                    boolean flag = out(person);
                    if (flag) {
                        return time;
                    }
                }
                if (virtualController.isIn()) {
                    in();
                }
                shut();
                if (direction == 0) {
                    end = virtualController.changeEnd();
                }
            }
        }
    }

    public void open() {
        if (!status) {  //开门
            time += argument.getOpenTime();
            status = false;
        }
    }

    public void shut() {
        if (status) {       //如果门开着，那么关上
            time += argument.getShutTime();
            status = true;
        }
    }

    public void in() {
        ArrayList<Person> up = new ArrayList<>();
        ArrayList<Person> down = new ArrayList<>();
        for (Person person : inRequests.get(position)) {
            if (person.getDirection() == 1) {  //上行
                up.add(person);
            } else {
                down.add(person);
            }
        }
        if (direction == 1) {       //电梯上行，把上行的人都扔进去
            inPersonWhenMoving(up);
        } else if (direction == -1) {       //电梯下行
            inPersonWhenMoving(down);
        } else {                   //电梯现在静止，up和down必不同时为空
            if (up.size() > down.size()) {
                inPersonWhenMoving(up);
            } else {
                inPersonWhenMoving(down);
            }
        }
    }

    public void inPersonWhenMoving(ArrayList<Person> direction) {
        if (direction.isEmpty()) {     //全反向
            return;
        }
        open();
        if (argument.getMaxCapacity() - virtualController.getTotal() < direction.size()) {
            for (int i = virtualController.getTotal(); i < argument.getMaxCapacity(); i++) {
                Person person = direction.get(i - virtualController.getTotal());
                outRequests.get(person.getToFloor()).add(person);     //输入到运载请求队列
                inRequests.get(position).remove(person);              //从请求队列中删除
            }
            virtualController.changeTotal();
        } else {
            for (Person person : direction) {
                outRequests.get(person.getToFloor()).add(person);
                inRequests.get(position).remove(person);
            }
            virtualController.changeTotal(direction.size());
        }
    }

    public boolean out(Person person) {
        boolean res = false;
        virtualController.changeTotal(-1 * outRequests.get(position).size());
        for (Person person1 : outRequests.get(position)) {
            if (person1.getPersonID() == person.getPersonID()) {
                res = true;
            }
        }
        outRequests.get(position).clear();
        return res;
    }

    public int getCapacity() {
        return argument.getMaxCapacity();
    }

    public void changeDirection(int end) {
        if (end == position) {
            direction = 0;
        } else if (end > position) {
            direction = 1;
        } else {
            direction = -1;
        }
    }

    public void down() {
        if (status) {
            shut();
        }
        time += argument.getMoveTime();
        position -= 1;
    }

    public void up() {
        if (status) {
            shut();     //门现在开着
        }
        time += argument.getMoveTime();
        position += 1;
    }

    public ArrayList<ArrayList<Person>> getInRequests() {
        return inRequests;
    }

    public ArrayList<ArrayList<Person>> getOutRequests() {
        return outRequests;
    }

    public int getPosition() {
        return position;
    }

    public int getEnd() {
        return end;
    }

    public boolean getReset() {
        return reset;
    }

    public int getElevatorID() {
        return id;
    }

    public int getSpeed() {
        return argument.getMoveTime();
    }

    public void addInRequest(Person person) {
        inRequests.get(person.getFromFloor()).add(person);
    }

    public int getSum() {
        return virtualController.getTotal();
    }
}
