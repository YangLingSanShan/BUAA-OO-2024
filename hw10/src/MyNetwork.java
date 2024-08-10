
import com.oocourse.spec2.exceptions.EqualPersonIdException;
import com.oocourse.spec2.exceptions.EqualRelationException;
import com.oocourse.spec2.exceptions.PersonIdNotFoundException;
import com.oocourse.spec2.exceptions.RelationNotFoundException;
import com.oocourse.spec2.exceptions.EqualTagIdException;
import com.oocourse.spec2.exceptions.AcquaintanceNotFoundException;
import com.oocourse.spec2.exceptions.TagIdNotFoundException;
import com.oocourse.spec2.exceptions.PathNotFoundException;
import com.oocourse.spec2.main.Network;
import com.oocourse.spec2.main.Person;
import com.oocourse.spec2.main.Tag;

import java.util.HashMap;
import java.util.HashSet;

public class MyNetwork implements Network {

    private HashMap<Integer, Person> people;
    private HashSet<Tag> tags = new HashSet<>();
    private Block block;
    private int tripleSum;
    private int doubleSum;

    public MyNetwork() {
        people = new HashMap<>();
        //tags = new HashMap<>();
        block = new Block();
        tripleSum = 0;
        doubleSum = 0;
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
        updateDoubleSum();
        updateValueSum(id1, id2, value);
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
        int formerValue = queryValue(id1, id2);
        boolean flag1 = person1.changeValue(id2, value);
        boolean flag2 = person2.changeValue(id1, value);
        if (flag1 && flag2) {
            //Triple
            block.removePair(person1, person2);
            updateTripleSum(id1, id2, false);
            updateDoubleSum();
            updateValueSum(id1, id2, -formerValue);
            ((MyPerson) getPerson(id1)).removeOtherFromTag(getPerson(id2));
            ((MyPerson) getPerson(id2)).removeOtherFromTag(getPerson(id1));
        } else {
            updateDoubleSum();
            updateValueSum(id1, id2, value);
        }
    }

    public void updateValueSum(int id1, int id2, int value) {
        for (Tag tag: tags) {
            if (tag.hasPerson(getPerson(id1)) && tag.hasPerson(getPerson(id2))) {
                ((MyTag) tag).changeValueSum(value);
            }
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

    //NEW HOWMWORK 10
    @Override
    public void addTag(int personId, Tag tag)
            throws PersonIdNotFoundException, EqualTagIdException {
        if (!this.containsPerson(personId)) {
            throw new MyPersonIdNotFoundException(personId);
        }
        Person person = getPerson(personId);
        if (person.containsTag(tag.getId())) {
            throw new MyEqualTagIdException(tag.getId());
        }
        person.addTag(tag);
        tags.add(tag);
        //tag.addPerson(person);
    }

    @Override
    public void addPersonToTag(int personId1, int personId2, int tagId) throws
            PersonIdNotFoundException, RelationNotFoundException,
            TagIdNotFoundException, EqualPersonIdException {
        if (!containsPerson(personId1)) {
            throw new MyPersonIdNotFoundException(personId1);
        }
        if (!containsPerson(personId2)) {
            throw new MyPersonIdNotFoundException(personId2);
        }
        if (personId1 == personId2) {
            throw new MyEqualPersonIdException(personId1);
        }
        if (!getPerson(personId1).isLinked(getPerson(personId2))) {
            throw new MyRelationNotFoundException(personId1, personId2);
        }
        if (!getPerson(personId2).containsTag(tagId)) {
            throw new MyTagIdNotFoundException(tagId);
        }
        if (getPerson(personId2).getTag(tagId).hasPerson(getPerson(personId1))) {
            throw new MyEqualPersonIdException(personId1);
        }
        if (getPerson(personId2).getTag(tagId).getSize() > 1111) {
            return;
        }
        Tag tag = getPerson(personId2).getTag(tagId);
        tag.addPerson(getPerson(personId1));
    }

    @Override
    public int queryTagValueSum(int personId, int tagId)
            throws PersonIdNotFoundException, TagIdNotFoundException {
        if (!containsPerson(personId)) {
            throw new MyPersonIdNotFoundException(personId);
        }
        if (!getPerson(personId).containsTag(tagId)) {
            throw new MyTagIdNotFoundException(tagId);
        }
        return getPerson(personId).getTag(tagId).getValueSum();
    }

    @Override
    public int queryTagAgeVar(int personId, int tagId)
            throws PersonIdNotFoundException, TagIdNotFoundException {
        if (!containsPerson(personId)) {
            throw new MyPersonIdNotFoundException(personId);
        }
        if (!getPerson(personId).containsTag(tagId)) {
            throw new MyTagIdNotFoundException(tagId);
        }
        return getPerson(personId).getTag(tagId).getAgeVar();
    }

    @Override
    public void delPersonFromTag(int personId1, int personId2, int tagId) throws
            PersonIdNotFoundException, TagIdNotFoundException {
        if (!containsPerson(personId1)) {
            throw new MyPersonIdNotFoundException(personId1);
        }
        if (!containsPerson(personId2)) {
            throw new MyPersonIdNotFoundException(personId2);
        }
        if (!getPerson(personId2).containsTag(tagId)) {
            throw new MyTagIdNotFoundException(tagId);
        }
        if (!getPerson(personId2).getTag(tagId).hasPerson(getPerson(personId1))) {
            throw new MyPersonIdNotFoundException(personId1);
        }
        Tag tag = getPerson(personId2).getTag(tagId);
        tag.delPerson(getPerson(personId1));
        //getPerson(personId1).delTag(tagId);
    }

    @Override
    public void delTag(int personId, int tagId)
            throws PersonIdNotFoundException, TagIdNotFoundException {
        if (!containsPerson(personId)) {
            throw new MyPersonIdNotFoundException(personId);
        }
        if (!getPerson(personId).containsTag(tagId)) {
            throw new MyTagIdNotFoundException(tagId);
        }
        Person person = getPerson(personId);
        person.delTag(tagId);
    }

    @Override
    public int queryBestAcquaintance(int id)
            throws PersonIdNotFoundException, AcquaintanceNotFoundException {
        if (!containsPerson(id)) {
            throw new MyPersonIdNotFoundException(id);
        }
        if (((MyPerson) getPerson(id)).getAcquaintanceSize() == 0) {
            throw new MyAcquaintanceNotFoundException(id);
        }
        Person person = ((MyPerson) getPerson(id)).getBestAcquaintance();
        return person.getId();
    }

    @Override
    public int queryCoupleSum() {
        return doubleSum / 2;
    }

    @Override
    public int queryShortestPath(int id1, int id2)
            throws PersonIdNotFoundException, PathNotFoundException {
        if (!containsPerson(id1)) {
            throw new MyPersonIdNotFoundException(id1);
        }
        if (!containsPerson(id2)) {
            throw new MyPersonIdNotFoundException(id2);
        }
        if (!isCircle(id1, id2)) {
            throw new MyPathNotFoundException(id1, id2);
        }
        if (id1 == id2) {
            return 0;
        }
        return block.Dijkstra(getPerson(id1), getPerson(id2), this) - 1;
    }

    public void updateDoubleSum() {
        int res = 0;
        for (Person person : people.values()) {
            res += ((MyPerson) person).isHaveDoubleSum() ? 1 : 0;
        }
        this.doubleSum = res;
    }

}
