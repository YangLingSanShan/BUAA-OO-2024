import com.oocourse.spec3.exceptions.MessageIdNotFoundException;

public class MyMessageIdNotFoundException extends MessageIdNotFoundException {
    private int id;

    public MyMessageIdNotFoundException(int id) {
        ErrorCount.counter.triggerMessageIdNotFoundException(id);
        this.id = id;
    }

    @Override
    public void print() {
        int minf = ErrorCount.counter.getMinfNum();
        int time = ErrorCount.counter.getMinfIdTriggerTime(id);
        System.out.printf("minf-%d, %d-%d\n", minf, id, time);
    }
}
