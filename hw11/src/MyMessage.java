import com.oocourse.spec3.main.Message;
import com.oocourse.spec3.main.Person;
import com.oocourse.spec3.main.Tag;

public class MyMessage implements Message {

    private int id;
    private int socialValue;
    private int type;
    private Person person1;
    private Person person2;
    private Tag tag;

    /*@ ensures type == 0;
     @ ensures tag == null;
     @ ensures id == messageId;
     @ ensures socialValue == messageSocialValue;
     @ ensures person1 == messagePerson1;
     @ ensures person2 == messagePerson2;
     @*/
    public MyMessage(int messageId, int messageSocialValue,
                     Person messagePerson1, Person messagePerson2) {
        this.type = 0;
        this.tag = null;
        this.id = messageId;
        this.socialValue = messageSocialValue;
        this.person1 = messagePerson1;
        this.person2 = messagePerson2;
    }

    /*@ ensures type == 1;
     @ ensures person2 == null;
     @ ensures id == messageId;
     @ ensures socialValue == messageSocialValue;
     @ ensures person1 == messagePerson1;
     @ ensures tag == messageTag;
     @*/
    public MyMessage(int messageId, int messageSocialValue, Person messagePerson1, Tag messageTag) {
        this.type = 1;
        this.person2 = null;
        this.id = messageId;
        this.socialValue = messageSocialValue;
        this.person1 = messagePerson1;
        this.tag = messageTag;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getSocialValue() {
        return socialValue;
    }

    @Override
    public Person getPerson1() {
        return person1;
    }

    @Override
    public Person getPerson2() {
        return person2;
    }

    @Override
    public Tag getTag() {
        return tag;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Message) {
            return ((Message) obj).getId() == id;
        } else {
            return false;
        }
    }
}
