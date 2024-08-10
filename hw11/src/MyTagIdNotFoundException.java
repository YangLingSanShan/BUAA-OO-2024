import com.oocourse.spec3.exceptions.TagIdNotFoundException;

public class MyTagIdNotFoundException extends TagIdNotFoundException {
    private int id;

    public MyTagIdNotFoundException(int id) {
        ErrorCount.counter.triggerTagIdNotFoundException(id);
        this.id = id;
    }

    @Override
    public void print() {
        int tinf = ErrorCount.counter.getTinfNum();
        int time = ErrorCount.counter.getTinfIdTriggerTime(id);
        System.out.printf("tinf-%d, %d-%d\n", tinf, id, time);
    }
}
