import com.oocourse.spec2.main.Person;
import com.oocourse.spec2.main.Tag;

import java.util.HashMap;

public class MyTag implements Tag {

    private HashMap<Integer, Person> people = new HashMap<>();
    private int id;
    private int valueSum;
    private int ageSum;
    private int ageSquareSum;

    public MyTag(int id) {
        this.id = id;
        valueSum = 0;
        ageSum = 0;
        ageSquareSum = 0;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void addPerson(Person person) {
        for (Person p : people.values()) {
            if (person.isLinked(p)) {
                valueSum += 2 * person.queryValue(p);
            }
        }
        ageSum += person.getAge();
        ageSquareSum += person.getAge() * person.getAge();
        people.put(person.getId(), person);
    }

    @Override
    public boolean hasPerson(Person person) {
        return people.containsKey(person.getId());
    }

    @Override
    public int getValueSum() {
        return valueSum;
    }

    @Override
    public int getAgeMean() {
        int n = getSize();
        if (n == 0) {
            return 0;
        } else {
            return ageSum / n;
        }
    }

    @Override
    public int getAgeVar() {
        int n = getSize();
        if (n == 0) {
            return 0;
        } else {
            int mean = getAgeMean();
            return (ageSquareSum - 2 * mean * ageSum + n * mean * mean) / n;
        }
    }

    @Override
    public void delPerson(Person person) {
        for (Person p : people.values()) {
            if (person.isLinked(p)) {
                valueSum -= person.queryValue(p) * 2;
            }
        }
        ageSum -= person.getAge();
        ageSquareSum -= person.getAge() * person.getAge();
        people.remove(person.getId());
    }

    @Override
    public int getSize() {
        return people.size();
    }

    public void changeValueSum(int value) {
        this.valueSum += 2 * value;
    }
}
