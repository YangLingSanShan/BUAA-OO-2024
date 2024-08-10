import com.oocourse.spec3.exceptions.EqualMessageIdException;

public class MyEqualMessageIdException extends EqualMessageIdException {
    private int id;

    public MyEqualMessageIdException(int id) {
        ErrorCount.counter.triggerEqualMessageIdException(id);
        this.id = id;
    }

    @Override
    public void print() {
        int emi = ErrorCount.counter.getEmiNum();
        int time = ErrorCount.counter.getEmiIdTriggerTime(id);
        System.out.printf("emi-%d, %d-%d\n", emi, id, time);
    }

}
