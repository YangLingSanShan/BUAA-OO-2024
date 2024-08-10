import com.oocourse.spec3.main.NoticeMessage;
import com.oocourse.spec3.main.Person;
import com.oocourse.spec3.main.Tag;

public class MyNoticeMessage extends MyMessage implements NoticeMessage {
    private String string;

    /*@ ensures type == 0;
          @ ensures tag == null;
          @ ensures id == messageId;
          @ ensures person1 == messagePerson1;
          @ ensures person2 == messagePerson2;
          @ ensures string == noticeString;
          @*/
    public MyNoticeMessage(int messageId, String noticeString,
                           Person messagePerson1, Person messagePerson2) {
        super(messageId, noticeString.length(), messagePerson1, messagePerson2);
        this.string = noticeString;
    }

    /*@ ensures type == 1;
      @ ensures person2 == null;
      @ ensures id == messageId;
      @ ensures person1 == messagePerson1;
      @ ensures tag == messageTag;
      @ ensures string == noticeString;
      @*/
    public MyNoticeMessage(int messageId, String noticeString,
                           Person messagePerson1, Tag messageTag) {
        super(messageId, noticeString.length(), messagePerson1, messageTag);
        this.string = noticeString;
    }

    @Override
    public String getString() {
        return string;
    }
}
