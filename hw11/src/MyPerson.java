import com.oocourse.spec3.main.Message;
import com.oocourse.spec3.main.NoticeMessage;
import com.oocourse.spec3.main.Person;
import com.oocourse.spec3.main.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MyPerson implements Person {

    private int id;
    private String name;
    private int age;
    private boolean visited;
    private Person bestAcquaintance;
    private HashMap<Integer, Person> acquaintance = new HashMap<>();
    private HashMap<Integer, Integer> values = new HashMap<>();
    private HashMap<Integer, Tag> tags = new HashMap<>();
    private int money;
    private int socialValue;
    private ArrayList<Message> messages = new ArrayList<>();

    public MyPerson(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.bestAcquaintance = null;
        this.money = 0;
        this.socialValue = 0;
    }

    public void setVisited(boolean flag) {
        visited = flag;
    }

    public boolean getVisited() {
        return visited;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getAge() {
        return age;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Person) {
            return ((Person) obj).getId() == id;
        } else {
            return false;
        }
    }

    @Override
    public boolean isLinked(Person person) {
        return person.getId() == id || acquaintance.containsKey(person.getId());
    }

    @Override
    public int queryValue(Person person) {
        if (acquaintance.containsKey(person.getId())) {
            return values.get(person.getId());
        } else {
            return 0;
        }
    }

    public void addAcquaintance(MyPerson person, int valueOf) {
        int id = person.getId();
        acquaintance.put(id, person);
        values.put(id, valueOf);
        this.updateBestAaquaintance(valueOf, person);
    }

    public boolean changeValue(int id, int valueOf) {
        int oldValue = values.get(id);
        if (oldValue + valueOf > 0) {
            values.replace(id, oldValue + valueOf);
            for (Person person : acquaintance.values()) {
                this.updateBestAaquaintance(values.get(person.getId()), person);
            }
            return false;   //没有断联
        } else {
            values.remove(id);
            acquaintance.remove(id);
            if (bestAcquaintance.getId() == id) {
                bestAcquaintance = null;
                for (Person person : acquaintance.values()) {
                    this.updateBestAaquaintance(values.get(person.getId()), person);
                }
            }
            return true;    //断联
        }
    }

    public HashMap<Integer, Person> getAcquaintanceHashMap() {
        return this.acquaintance;
    }

    public HashSet<Person> getAcquaintance() {
        return new HashSet<>(getAcquaintanceHashMap().values());
    }

    public HashSet<Integer> getAcquaintanceID() {
        return new HashSet<>(acquaintance.keySet());
    }

    public boolean strictEquals(Person person) {
        return true;
    }

    //NEW HOMEWORK 10
    @Override
    public boolean containsTag(int id) {
        return tags.containsKey(id);
    }

    @Override
    public Tag getTag(int id) {
        return tags.get(id);
    }

    @Override
    public void addTag(Tag tag) {
        tags.put(tag.getId(), tag);
    }

    @Override
    public void delTag(int id) {
        tags.remove(id);
    }

    public Person getBestAcquaintance() {
        return bestAcquaintance;
    }

    public void updateBestAaquaintance(int value, Person newPerson) {
        if (bestAcquaintance == null) {
            bestAcquaintance = newPerson;
        } else {
            if ((value > values.get(bestAcquaintance.getId())) ||
                    (value == values.get(bestAcquaintance.getId())
                            && (newPerson.getId() < bestAcquaintance.getId()))) {
                bestAcquaintance = newPerson;
            }
        }
    }

    public boolean isHaveDoubleSum() {
        MyPerson best = (MyPerson) bestAcquaintance;
        return best != null && best.getBestAcquaintance().equals(this);
    }

    public int getAcquaintanceSize() {
        return acquaintance.size();
    }

    public void removeOtherFromTag(Person person) {
        for (Tag tag : tags.values()) {
            if (tag.hasPerson(person)) {
                tag.delPerson(person);
            }
        }
    }

    //NEW HW11

    @Override
    public void addSocialValue(int num) {
        this.socialValue += num;
    }

    @Override
    public int getSocialValue() {
        return socialValue;
    }

    public void addMessage(Message message) {
        messages.add(0, message);
    }

    @Override
    public List<Message> getMessages() {
        return messages;
    }

    @Override
    public List<Message> getReceivedMessages() {
        ArrayList<Message> res = new ArrayList<>();
        int len = Math.min(5, messages.size());
        for (int i = 0; i < len; i++) {
            res.add(messages.get(i));
        }
        return res;
    }

    @Override
    public void addMoney(int num) {
        this.money += num;
    }

    @Override
    public int getMoney() {
        return money;
    }

    public void clearNotices() {
        messages.removeIf(m -> m instanceof NoticeMessage);
    }
}
