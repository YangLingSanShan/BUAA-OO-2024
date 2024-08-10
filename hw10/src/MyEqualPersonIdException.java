import com.oocourse.spec2.exceptions.EqualPersonIdException;

public class MyEqualPersonIdException extends EqualPersonIdException {
    private int id;

    public MyEqualPersonIdException(int id) {
        ErrorCount.counter.triggerEqualPersonIdException(id);
        this.id = id;
    }

    @Override
    public void print() {
        int epi = ErrorCount.counter.getEpiNum();
        int time = ErrorCount.counter.getEpiIdTriggerTime(id);
        System.out.printf("epi-%d, %d-%d\n", epi, id, time);
    }
}
