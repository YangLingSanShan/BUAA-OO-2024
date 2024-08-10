import com.oocourse.spec1.exceptions.EqualPersonIdException;
import com.oocourse.spec1.exceptions.EqualRelationException;
import com.oocourse.spec1.exceptions.PersonIdNotFoundException;
import com.oocourse.spec1.exceptions.RelationNotFoundException;
import com.oocourse.spec1.main.Network;
import com.oocourse.spec1.main.Person;

import java.util.HashMap;
import java.util.HashSet;

public class MyNetwork implements Network {

    private HashMap<Integer, Person> people;
    private Block block;
    private int tripleSum;

    public MyNetwork() {
        people = new HashMap<>();
        block = new Block();
        tripleSum = 0;
    }

    @Override
    public boolean containsPerson(int id) {
        return people.containsKey(id);
    }

    @Override
    public Person getPerson(int id) {
        return people.get(id);
    }

    @Override
    public void addPerson(Person person) throws EqualPersonIdException {
        int id = person.getId();
        if (containsPerson(id)) {
            throw new MyEqualPersonIdException(id);
        }
        people.put(id, person);
        block.addBlock();
    }

    @Override
    public void addRelation(int id1, int id2, int value)
            throws PersonIdNotFoundException, EqualRelationException {
        if (!containsPerson(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        }
        if (!containsPerson(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        }
        if (getPerson(id1).isLinked(getPerson(id2))) {
            throw new MyEqualRelationException(id1, id2);
        }
        MyPerson person1 = (MyPerson) getPerson(id1);
        MyPerson person2 = (MyPerson) getPerson(id2);
        person1.addAcquaintance(person2, value);
        person2.addAcquaintance(person1, value);
        block.addPair(person1, person2);
        updateTripleSum(id1, id2, true);
        //triple
    }

    @Override
    public void modifyRelation(int id1, int id2, int value) throws PersonIdNotFoundException,
            EqualPersonIdException, RelationNotFoundException {
        if (!containsPerson(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        }
        if (!containsPerson(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        }
        if (id1 == id2) {
            throw new MyEqualPersonIdException(id1);
        }
        MyPerson person1 = (MyPerson) getPerson(id1);
        MyPerson person2 = (MyPerson) getPerson(id2);
        if (!person1.isLinked(person2)) {
            throw new MyRelationNotFoundException(id1, id2);
        }
        boolean flag1 = person1.changeValue(id2, value);
        boolean flag2 = person2.changeValue(id1, value);
        if (flag1 && flag2) {
            //Triple
            block.removePair(person1, person2);
            updateTripleSum(id1, id2, false);
        }
    }

    @Override
    public int queryValue(int id1, int id2)
            throws PersonIdNotFoundException, RelationNotFoundException {
        if (!containsPerson(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        }
        if (!containsPerson(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        }
        if (!getPerson(id1).isLinked(getPerson(id2))) {
            throw new MyRelationNotFoundException(id1, id2);
        }
        return getPerson(id1).queryValue(getPerson(id2));
    }

    @Override
    public boolean isCircle(int id1, int id2) throws PersonIdNotFoundException {
        if (!containsPerson(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        }
        if (!containsPerson(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        }
        return block.isSameBlock(id1, id2);
    }

    @Override
    public int queryBlockSum() {
        return block.getBlockSum();
    }

    @Override
    public int queryTripleSum() {
        //        ArrayList<Integer> person = new ArrayList<>(people.keySet());
        //        Random random = new Random(6);
        //        MyPerson p1;
        //        MyPerson p2;
        //        do {
        //            int id1 = random.nextInt(person.size());
        //            int id2 = random.nextInt(person.size());
        //            p1 = (MyPerson) getPerson(person.get(id1));
        //            p2 = (MyPerson) getPerson(person.get(id2));
        //        } while (!p1.isLinked(p2));
        //        try {
        //            modifyRelation(p1.getId(), p2.getId(), 114514);
        //        } catch (PersonIdNotFoundException | EqualPersonIdException
        //        | RelationNotFoundException e) {
        //            throw new RuntimeException(e);
        //        }
        return tripleSum;
    }

    public void updateTripleSum(int id1, int id2, boolean type) {
        HashSet<Integer> ac1 = ((MyPerson) getPerson(id1)).getAcquaintanceID();
        HashSet<Integer> ac2 = ((MyPerson) getPerson(id2)).getAcquaintanceID();
        ac1.retainAll(ac2);
        tripleSum += type ? ac1.size() : -ac1.size();
    }

    public Person[] getPersons() {
        Person[] people1 = people.values().toArray(new Person[0]);
        return people1;
    }
}
