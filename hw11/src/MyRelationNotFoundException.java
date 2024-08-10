import com.oocourse.spec3.exceptions.RelationNotFoundException;

public class MyRelationNotFoundException extends RelationNotFoundException {
    private int id1;
    private int id2;

    public MyRelationNotFoundException(int id1, int id2) {
        ErrorCount.counter.triggerRelationNotFoundException(id1, id2);
        this.id1 = Math.min(id1, id2);
        this.id2 = Math.max(id1, id2);
    }

    @Override
    public void print() {
        int rnf = ErrorCount.counter.getRnfNum();
        int id1Time = ErrorCount.counter.getRnfIdTriggerTime(id1);
        int id2Time = ErrorCount.counter.getRnfIdTriggerTime(id2);
        System.out.printf("rnf-%d, %d-%d, %d-%d\n", rnf, id1, id1Time, id2, id2Time);
    }
}
