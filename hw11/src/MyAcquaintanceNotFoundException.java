import com.oocourse.spec3.exceptions.AcquaintanceNotFoundException;

public class MyAcquaintanceNotFoundException extends AcquaintanceNotFoundException {
    private int id;

    public MyAcquaintanceNotFoundException(int id) {
        ErrorCount.counter.triggerAcquaintanceNotFoundException(id);
        this.id = id;
    }

    @Override
    public void print() {
        int anf = ErrorCount.counter.getAnfNum();
        int time = ErrorCount.counter.getAnfIdTriggerTime(id);
        System.out.printf("anf-%d, %d-%d\n", anf, id, time);
    }
}
