import com.oocourse.spec3.exceptions.EqualEmojiIdException;

public class MyEqualEmojiIdException extends EqualEmojiIdException {
    private int id;

    public MyEqualEmojiIdException(int id) {
        ErrorCount.counter.triggerEqualEmojiIdException(id);
        this.id = id;
    }

    @Override
    public void print() {
        int eei = ErrorCount.counter.getEeiNum();
        int time = ErrorCount.counter.getEeiIdTriggerTime(id);
        System.out.printf("eei-%d, %d-%d\n", eei, id, time);
    }
}
