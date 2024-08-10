import com.oocourse.elevator2.ResetRequest;
import com.oocourse.elevator2.TimableOutput;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class ElevatorWaitingQueue {

    private ArrayList<ResetRequest> resetRequests = new ArrayList<>();
    private ArrayList<ArrayList<Person>> personInRequests = new ArrayList<>();
    private ArrayList<ArrayList<Person>> personOutRequests = new ArrayList<>();
    private boolean isEnd = false;
    private Elevator elevator;

    public ElevatorWaitingQueue() {
        personOutRequests.add(null);
        personInRequests.add(null);
        for (int i = 1; i <= 11; i++) {
            personInRequests.add(new ArrayList<>());
            personOutRequests.add(new ArrayList<>());
        }
    }

    public synchronized void setElevator(Elevator elevator) {
        this.elevator = elevator;
        personInRequests.add(null);
        this.notifyAll();
    }

    public synchronized void addRequest(Person person) {
        personInRequests.get(person.getFromFloor()).add(person);
        TimableOutput.println("RECEIVE-" + person.getPersonID() + "-" + elevator.getElevatorID());
        this.notifyAll();
    }

    public synchronized void addRequest(ResetRequest request) {
        elevator.setReset(true);
        resetRequests.add(request);
        this.notifyAll();
    }

    public synchronized ResetRequest getResetRequest() {
        if (hasReset()) {
            ResetRequest resetRequest = resetRequests.get(0);
            resetRequests.remove(resetRequest);
            this.notifyAll();
            return resetRequest;
        } else {
            this.notifyAll();
            return null;
        }
    }

    public ArrayList<ArrayList<Person>> getPersonInRequests() {
        return personInRequests;
    }

    public ArrayList<ArrayList<Person>> getPersonOutRequests() {
        return personOutRequests;
    }

    public synchronized void setEnd() {
        isEnd = true;
        this.notifyAll();
    }

    public synchronized boolean isEnd() {
        this.notifyAll();
        return isEmpty() && isEnd;
    }

    public synchronized boolean hasReset() {
        this.notifyAll();
        return !resetRequests.isEmpty();
    }

    public synchronized boolean isEmpty() {
        this.notifyAll();
        return !hasReset() && isEmpty(personInRequests) && isEmpty(personOutRequests);
    }

    public synchronized boolean isEmpty(ArrayList<ArrayList<Person>> personInRequests) {
        for (int i = 1; i <= 11; i++) {
            if (!personInRequests.get(i).isEmpty()) {
                this.notifyAll();
                return false;
            }
        }
        this.notifyAll();
        return true;
    }

    public ArrayList<Person> getOut(int position) {
        return personOutRequests.get(position);
    }

    public ArrayList<Person> getIn(int position) {
        return personInRequests.get(position);
    }

    public synchronized void inPerson(int position) throws InterruptedException {
        ArrayList<Person> up = new ArrayList<>();
        ArrayList<Person> down = new ArrayList<>();
        for (Person person : personInRequests.get(position)) {
            if (person.getDirection() == 1) {  //上行
                up.add(person);
            } else {
                down.add(person);
            }
        }
        if (elevator.getDirection() == 1) {       //电梯上行，把上行的人都扔进去
            inPersonWhenMoving(up);
        } else if (elevator.getDirection() == -1) {       //电梯下行
            inPersonWhenMoving(down);
        } else {                   //电梯现在静止，up和down必不同时为空
            if (up.size() > down.size()) {
                inPersonWhenMoving(up);
            } else {
                inPersonWhenMoving(down);
            }
        }
        this.notifyAll();
    }

    public synchronized void inPersonWhenMoving(ArrayList<Person> direction)
            throws InterruptedException {
        if (direction.isEmpty()) {     //全反向
            return;
        }
        elevator.open();
        if (elevator.getMaxCapacity() - elevator.getTotal() < direction.size()) {
            for (int i = elevator.getTotal(); i < elevator.getMaxCapacity(); i++) {
                Person person = direction.get(i - elevator.getTotal());
                personOutRequests.get(person.getToFloor()).add(person);     //输入到运载请求队列
                TimableOutput.println(
                        "IN-" + person.getPersonID() + "-" + elevator.getPosition()
                                + "-" + elevator.getElevatorID());
                personInRequests.get(elevator.getPosition()).remove(person); //从请求队列中删除
            }
            elevator.changeSum();
        } else {
            for (Person person : direction) {
                personOutRequests.get(person.getToFloor()).add(person);
                TimableOutput.println(
                        "IN-" + person.getPersonID() + "-" + elevator.getPosition()
                                + "-" + elevator.getElevatorID());
                personInRequests.get(elevator.getPosition()).remove(person);
            }
            elevator.changeSum(direction.size());
        }
        this.notifyAll();
    }

    public synchronized void outPerson(int position) {
        for (Person person : personOutRequests.get(position)) {
            TimableOutput.println(
                    "OUT-" + person.getPersonID() + "-" + position
                            + "-" + elevator.getElevatorID());
        }
        elevator.changeSum(-1 * personOutRequests.get(position).size());
        personOutRequests.get(position).clear();
        this.notifyAll();
    }

    public synchronized void addAll(ArrayList<Person> toAllocate) {
        for (int i = 1; i <= 11; i++) {
            toAllocate.addAll(personOutRequests.get(i));
            personOutRequests.get(i).clear();
        }
        if (!toAllocate.isEmpty()) {
            try {
                elevator.open();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            for (Person person : toAllocate) {
                person.changeFromFloor(elevator.getPosition());
                person.changeDirection();
                TimableOutput.println(
                        "OUT-" + person.getPersonID() + "-" + elevator.getPosition()
                                + "-" + elevator.getElevatorID());
            }
        }
        for (int i = 1; i <= 11; i++) {
            toAllocate.addAll(personInRequests.get(i));
            personInRequests.get(i).clear();
        }
        elevator.clear();
        this.notifyAll();
    }

    public synchronized void reset(ResetRequest resetRequest) {
        TimableOutput.println("RESET_BEGIN-" + elevator.getElevatorID());
        elevator.changeMoveTime(resetRequest.getSpeed());
        elevator.changeCapacity(resetRequest.getCapacity());
        try {
            sleep(1200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        TimableOutput.println("RESET_END-" + elevator.getElevatorID());
        if (resetRequests.isEmpty()) {
            elevator.setReset(false);
        }
        this.notifyAll();
    }

    public Elevator getElevator() {
        return elevator;
    }

    public synchronized void getRequest() {
        if (this.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
