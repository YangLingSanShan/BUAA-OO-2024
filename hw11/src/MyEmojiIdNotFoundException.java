import com.oocourse.spec3.exceptions.EmojiIdNotFoundException;

public class MyEmojiIdNotFoundException extends EmojiIdNotFoundException {
    private int id;

    public MyEmojiIdNotFoundException(int id) {
        ErrorCount.counter.triggerEmojiIdNotFoundException(id);
        this.id = id;
    }

    @Override
    public void print() {
        int einf = ErrorCount.counter.getEinfNum();
        int time = ErrorCount.counter.getEinfIdTriggerTime(id);
        System.out.printf("einf-%d, %d-%d\n", einf, id, time);
    }
}
