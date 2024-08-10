import com.oocourse.spec1.exceptions.EqualRelationException;

public class MyEqualRelationException extends EqualRelationException {
    private int id1;
    private int id2;

    public MyEqualRelationException(int id1, int id2) {
        ErrorCount.counter.triggerEqualRelationException(id1, id2);
        this.id1 = Math.min(id1, id2);
        this.id2 = Math.max(id1, id2);
    }

    @Override
    public void print() {
        int er = ErrorCount.counter.getErNum();
        int id1Time = ErrorCount.counter.getErIdTriggerTime(id1);
        int id2Time = ErrorCount.counter.getErIdTriggerTime(id2);
        System.out.printf("er-%d, %d-%d, %d-%d\n", er, id1, id1Time, id2, id2Time);
    }
}
