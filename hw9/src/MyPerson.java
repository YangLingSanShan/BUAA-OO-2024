import com.oocourse.spec1.main.Person;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class MyPerson implements Person {

    private int id;
    private String name;
    private int age;
    private boolean visited;
    private HashMap<Integer, Person> acquaintance = new HashMap<>();
    private HashMap<Integer, Integer> value = new HashMap<>();

    public MyPerson(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
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
            return value.get(person.getId());
        } else {
            return 0;
        }
    }

    public void addAcquaintance(MyPerson person, int valueOf) {
        int id = person.getId();
        acquaintance.put(id, person);
        value.put(id, valueOf);
    }

    public boolean changeValue(int id, int valueOf) {
        int oldValue = value.get(id);
        if (oldValue + valueOf > 0) {
            value.replace(id, oldValue + valueOf);
            return false;   //没有断联
        } else {
            value.remove(id);
            acquaintance.remove(id);
            return true;    //断联
        }
    }

    public HashSet<Person> getAcquaintance() {
        return new HashSet<>(acquaintance.values());
    }

    public HashSet<Integer> getAcquaintanceID() {
        return new HashSet<>(acquaintance.keySet());
    }

    public boolean strictEquals(Person person) {
        if (this.id != person.getId()) {
            return false;
        }
        if (this.age != person.getAge()) {
            return false;
        }
        if (!Objects.equals(this.name, person.getName())) {
            return false;
        }
        return true;
    }

    public void changeAge(int y) {
        age = y;
    }

    public void changeName(String name) {
        this.name = name;
    }
}
