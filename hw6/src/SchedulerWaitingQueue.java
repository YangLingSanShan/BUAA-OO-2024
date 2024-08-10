import com.oocourse.elevator2.PersonRequest;
import com.oocourse.elevator2.Request;

import java.util.ArrayList;

public class SchedulerWaitingQueue {
    private int sum = 0;
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

    public synchronized boolean isEmpty() {
        this.notifyAll();
        return sum == 0 && personInRequests.isEmpty();
    }

    public synchronized Request getOneRequestAndRemove() {
        if (sum == 0 && personInRequests.isEmpty() && !isEnd) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (personInRequests.isEmpty()) {
            this.notifyAll();
            return null;
        } else {
            Request request = personInRequests.get(0);
            personInRequests.remove(request);
            this.notifyAll();
            return request;
        }
    }

    public synchronized void reduce() {
        sum--;
        this.notifyAll();
    }
}
