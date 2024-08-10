import com.oocourse.elevator3.DoubleCarResetRequest;
import com.oocourse.elevator3.NormalResetRequest;
import com.oocourse.elevator3.ResetRequest;
import com.oocourse.elevator3.TimableOutput;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import static java.lang.Thread.sleep;

public class ElevatorWaitingQueue {

    private ArrayList<ResetRequest> resetRequests = new ArrayList<>();
    private ArrayList<ArrayList<Person>> personInRequests = new ArrayList<>();
    private ArrayList<ArrayList<Person>> personOutRequests = new ArrayList<>();
    private boolean isEnd = false;
    private boolean isDouble = false;
    private Elevator elevator;  //单电梯
    private int transfer = 11;
    private ArrayList<Car> cars = new ArrayList<>();
    private ArrayList<ElevatorWaitingQueue> subQueue = new ArrayList<>();
    private Semaphore semaphore = new Semaphore(1);

    public ElevatorWaitingQueue() {
        personOutRequests.add(null);
        personInRequests.add(null);
    }

    public synchronized void setElevator(Elevator elevator) {
        this.elevator = elevator;
        for (int i = 1; i <= 11; i++) {
            personInRequests.add(new ArrayList<>());
            personOutRequests.add(new ArrayList<>());
        }
        this.notifyAll();
    }

    public synchronized void addRequest(Person person) {

        if (isDouble) {
            if ((person.getFromFloor() < transfer && person.getDirection() == 1) ||
                    (person.getFromFloor() <= transfer && person.getDirection() == -1)) {
                synchronized (subQueue.get(0)) {
                    subQueue.get(0).getPersonInRequests().get(person.getFromFloor()).add(person);
                    TimableOutput.println("RECEIVE-" + person.getPersonID() +
                            "-" + elevator.getElevatorID() + "-A");
                    subQueue.get(0).notifyAll();
                }
            } else {
                synchronized (subQueue.get(1)) {
                    subQueue.get(1).getPersonInRequests().get(person.getFromFloor()).add(person);
                    TimableOutput.println("RECEIVE-" + person.getPersonID() +
                            "-" + elevator.getElevatorID() + "-B");
                    subQueue.get(1).notifyAll();
                }
            }
        } else {
            synchronized (personInRequests) {
                personInRequests.get(person.getFromFloor()).add(person);
                TimableOutput.println("RECEIVE-" + person.getPersonID() +
                        "-" + elevator.getElevatorID());
                personInRequests.notifyAll();
            }
        }
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
        if (!subQueue.isEmpty()) {
            for (ElevatorWaitingQueue queue : subQueue) {
                queue.setEnd();
            }
        }
        this.notifyAll();
    }

    public synchronized boolean isEnd(int min, int max) {
        this.notifyAll();
        return !isEmpty(min, max) || !isEnd;
    }

    public synchronized boolean hasReset() {
        this.notifyAll();
        return !resetRequests.isEmpty();
    }

    public synchronized boolean isEmpty(int min, int max) {
        this.notifyAll();
        return !hasReset() && isEmpty(personInRequests, min, max)
                && isEmpty(personOutRequests, min, max);
    }

    public synchronized boolean isEmpty(ArrayList<ArrayList<Person>> personInRequests,
                                        int min, int max) {
        for (int i = min; i <= max; i++) {
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

    public synchronized void inPerson(int position, int min, int max, Elevator e)
            throws InterruptedException {
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
            inPersonWhenMoving(up, min, max, 1, e);
        } else if (elevator.getDirection() == -1) {       //电梯下行
            inPersonWhenMoving(down, min, max, -1, e);
        } else {                   //电梯现在静止，up和down必不同时为空
            if (up.size() > down.size()) {
                inPersonWhenMoving(up, min, max, 1, e);
            } else {
                inPersonWhenMoving(down, min, max, -1, e);
            }
        }
        this.notifyAll();
    }

    public synchronized void inCertainDirection(int dir, int position, int min, int max, Elevator e)
            throws InterruptedException {
        ArrayList<Person> certain = new ArrayList<>();
        for (Person person : personInRequests.get(position)) {
            if (person.getDirection() == dir) {
                certain.add(person);
            }
        }
        inPersonWhenMoving(certain, min, max, dir, e);
    }

    public synchronized void inPersonWhenMoving(ArrayList<Person> direction,
                                                int min, int max, int dir, Elevator e)
            throws InterruptedException {
        if (direction.isEmpty()) {     //全反向
            return;
        }
        e.open();
        if (e.getMaxCapacity() - e.getTotal() < direction.size()) {
            for (int i = e.getTotal(); i < e.getMaxCapacity(); i++) {
                Person person = direction.get(i - e.getTotal());
                int toFloor = (dir == 1) ? Math.min(person.getToFloor(), max) :
                        Math.max(person.getToFloor(), min);
                personOutRequests.get(toFloor).add(person);     //输入到运载请求队列
                if (e.isDouble() && e.getSign() != ' ') {
                    TimableOutput.println(
                            "IN-" + person.getPersonID() + "-" + e.getPosition()
                                    + "-" + e.getElevatorID() + "-" + e.getSign());
                } else {
                    TimableOutput.println(
                            "IN-" + person.getPersonID() + "-" + e.getPosition()
                                    + "-" + e.getElevatorID());
                }
                personInRequests.get(e.getPosition()).remove(person); //从请求队列中删除
            }
            e.changeSum();
        } else {
            for (Person person : direction) {
                int toFloor = (dir == 1) ? Math.min(person.getToFloor(), max) :
                        Math.max(person.getToFloor(), min);
                personOutRequests.get(toFloor).add(person);     //输入到运载请求队列
                if (e.isDouble() && e.getSign() != ' ') {
                    TimableOutput.println(
                            "IN-" + person.getPersonID() + "-" + e.getPosition()
                                    + "-" + e.getElevatorID() + "-" + e.getSign());
                } else {
                    TimableOutput.println(
                            "IN-" + person.getPersonID() + "-" + e.getPosition()
                                    + "-" + e.getElevatorID());
                }
                personInRequests.get(e.getPosition()).remove(person);
            }
            e.changeSum(direction.size());
        }
        this.notifyAll();
    }

    public synchronized ArrayList<Person> outPerson(int position, Elevator elevator) {
        ArrayList<Person> people = new ArrayList<>();
        for (Person person : personOutRequests.get(position)) {
            if (person.getToFloor() != position) {
                people.add(person);
            }
            if (elevator.isDouble() && elevator.getSign() != ' ') {
                TimableOutput.println(
                        "OUT-" + person.getPersonID() + "-" + elevator.getPosition()
                                + "-" + elevator.getElevatorID() + "-" + elevator.getSign());
            } else {
                TimableOutput.println(
                        "OUT-" + person.getPersonID() + "-" + elevator.getPosition()
                                + "-" + elevator.getElevatorID());
            }
        }
        elevator.changeSum(-1 * personOutRequests.get(position).size());
        personOutRequests.get(position).clear();
        this.notifyAll();
        return people;
    }

    public synchronized void addAll(ArrayList<Person> toAllocate, Elevator elevator) {
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
                if (elevator.isDouble() && elevator.getSign() != ' ') {
                    TimableOutput.println(
                            "OUT-" + person.getPersonID() + "-" + elevator.getPosition()
                                    + "-" + elevator.getElevatorID() + "-" + elevator.getSign());
                } else {
                    TimableOutput.println(
                            "OUT-" + person.getPersonID() + "-" + elevator.getPosition()
                                    + "-" + elevator.getElevatorID());
                }
            }
        }
        for (int i = 1; i <= 11; i++) {
            toAllocate.addAll(personInRequests.get(i));
            personInRequests.get(i).clear();
        }
        elevator.clear();
        this.notifyAll();
    }

    public synchronized void reset(NormalResetRequest resetRequest) {
        TimableOutput.println("RESET_BEGIN-" + elevator.getElevatorID());
        elevator.changeMoveTime(resetRequest.getSpeed());
        elevator.changeCapacity(resetRequest.getCapacity());
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TimableOutput.println("RESET_END-" + elevator.getElevatorID());
        if (resetRequests.isEmpty()) {
            elevator.setReset(false);
        }
        this.notifyAll();
    }

    //轿厢
    public synchronized void reset(DoubleCarResetRequest resetRequest) {
        this.transfer = resetRequest.getTransferFloor();
        isDouble = true;
        elevator.setTransfer(resetRequest.getTransferFloor());

        TimableOutput.println("RESET_BEGIN-" + elevator.getElevatorID());
        ElevatorWaitingQueue queueA = new ElevatorWaitingQueue();
        ElevatorWaitingQueue queueB = new ElevatorWaitingQueue();
        elevator.changeMoveTime(resetRequest.getSpeed());
        elevator.changeCapacity(resetRequest.getCapacity());
        Car a = new Car(elevator, resetRequest, queueA, 'A', semaphore);
        Car b = new Car(elevator, resetRequest, queueB, 'B', semaphore);
        queueA.setElevator(a);
        queueB.setElevator(b);
        cars.add(a);
        cars.add(b);
        subQueue.add(queueA);
        subQueue.add(queueB);
        try {
            sleep(1200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        a.start();
        b.start();

        TimableOutput.println("RESET_END-" + elevator.getElevatorID());
        if (resetRequests.isEmpty()) {
            elevator.setReset(false);
        }
        this.notifyAll();
    }

    public synchronized void getRequest(int min, int max) {
        if (this.isEmpty(min, max)) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isDouble() {
        return isDouble;
    }

    public int getTransfer() {
        return transfer;
    }

    public synchronized void setDouble() {
        isDouble = true;
        this.notifyAll();
    }
}
