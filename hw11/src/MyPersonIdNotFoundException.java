import com.oocourse.spec3.exceptions.PersonIdNotFoundException;

public class MyPersonIdNotFoundException extends PersonIdNotFoundException {
    private int id;

    public MyPersonIdNotFoundException(int id) {
        ErrorCount.counter.triggerPersonIdNotFoundException(id);
        this.id = id;
    }

    @Override
    public void print() {
        int pinf = ErrorCount.counter.getPinfNum();
        int time = ErrorCount.counter.getPinfIdTriggerTime(id);
        System.out.printf("pinf-%d, %d-%d\n", pinf, id, time);
    }
}