import com.oocourse.spec3.exceptions.EqualTagIdException;

public class MyEqualTagIdException extends EqualTagIdException {
    private int id;

    public MyEqualTagIdException(int id) {
        ErrorCount.counter.triggerEqualTagIdException(id);
        this.id = id;
    }

    @Override
    public void print() {
        int eti = ErrorCount.counter.getEtiNum();
        int time = ErrorCount.counter.getEtiIdTriggerTime(id);
        System.out.printf("eti-%d, %d-%d\n", eti, id, time);
    }
}
