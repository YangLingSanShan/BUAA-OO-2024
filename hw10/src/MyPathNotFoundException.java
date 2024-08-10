import com.oocourse.spec2.exceptions.PathNotFoundException;

public class MyPathNotFoundException extends PathNotFoundException {
    private int id1;
    private int id2;

    public MyPathNotFoundException(int id1, int id2) {
        ErrorCount.counter.triggerPathNotFoundException(id1, id2);
        this.id1 = Math.min(id1, id2);
        this.id2 = Math.max(id1, id2);
    }

    @Override
    public void print() {
        int pnf = ErrorCount.counter.getPnfNum();
        int id1Time = ErrorCount.counter.getPnfIdTriggerTime(id1);
        int id2Time = ErrorCount.counter.getPnfIdTriggerTime(id2);
        System.out.printf("pnf-%d, %d-%d, %d-%d\n", pnf, id1, id1Time, id2, id2Time);
    }
}
