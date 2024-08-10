import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.Request;

import java.util.ArrayList;

public class SchedulerWaitingQueue {
    private int sum = 0;
    private int trans = 0;
    private ArrayList<Person> personInRequests = new ArrayList<>();
    private boolean isEnd = false;

    public synchronized void addRequest(int n) {
        sum += n;
        this.notifyAll();
    }

    public synchronized void addRequest(PersonRequest request) {
        synchronized (personInRequests) {
            personInRequests.add(new Person(request));
            personInRequests.notifyAll();
        }
        this.notifyAll();
    }

    public synchronized void addRequest(Person person) {
        synchronized (personInRequests) {
            personInRequests.add(person);
            personInRequests.notifyAll();
        }
        this.notifyAll();
    }

    public synchronized void setEnd() {
        isEnd = true;
        this.notifyAll();
    }

    public synchronized boolean isEnd() {
        this.notifyAll();
        return isEnd;
    }

    public synchronized boolean hasTrans() {
        this.notifyAll();
        return trans != 0;
    }

    public synchronized boolean isEmpty() {
        this.notifyAll();
        return sum == 0 && personInRequests.isEmpty();
    }

    public synchronized Request getOneRequestAndRemove() {
        if (personInRequests.isEmpty() && !isEnd && trans == 0) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (!personInRequests.isEmpty()) {
            Request request = personInRequests.get(0);
            personInRequests.remove(request);
            this.notifyAll();
            return request;
        } else if (trans != 0) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return null;
        } else {
            this.notifyAll();
            return null;
        }
    }

    public synchronized void reduce() {
        sum--;
        this.notifyAll();
    }

    public synchronized void add() {
        sum++;
        this.notifyAll();
    }

    public synchronized void addTrans() {
        trans++;
        //System.out.println(trans);
        this.notifyAll();
    }

    public synchronized void subTrans(int sum) {
        trans -= sum;
        //System.out.println(trans);
        this.notifyAll();
    }
}
