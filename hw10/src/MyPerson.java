import com.oocourse.spec2.main.Person;
import com.oocourse.spec2.main.Tag;

import java.util.HashMap;
import java.util.HashSet;

public class MyPerson implements Person {

    private int id;
    private String name;
    private int age;
    private boolean visited;
    private Person bestAcquaintance;
    private HashMap<Integer, Person> acquaintance = new HashMap<>();
    private HashMap<Integer, Integer> values = new HashMap<>();
    private HashMap<Integer, Tag> tags = new HashMap<>();

    public MyPerson(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.bestAcquaintance = null;
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
            for (Person person: acquaintance.values()) {
                this.updateBestAaquaintance(values.get(person.getId()), person);
            }
            return false;   //没有断联
        } else {
            values.remove(id);
            acquaintance.remove(id);
            if (bestAcquaintance.getId() == id) {
                bestAcquaintance = null;
                for (Person person: acquaintance.values()) {
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
        for (Tag tag: tags.values()) {
            if (tag.hasPerson(person)) {
                tag.delPerson(person);
            }
        }
    }
}
