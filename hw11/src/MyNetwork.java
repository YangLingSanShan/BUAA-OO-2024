
import com.oocourse.spec3.exceptions.AcquaintanceNotFoundException;
import com.oocourse.spec3.exceptions.EmojiIdNotFoundException;
import com.oocourse.spec3.exceptions.EqualEmojiIdException;
import com.oocourse.spec3.exceptions.EqualMessageIdException;
import com.oocourse.spec3.exceptions.EqualPersonIdException;
import com.oocourse.spec3.exceptions.EqualRelationException;
import com.oocourse.spec3.exceptions.EqualTagIdException;
import com.oocourse.spec3.exceptions.MessageIdNotFoundException;
import com.oocourse.spec3.exceptions.PathNotFoundException;
import com.oocourse.spec3.exceptions.RelationNotFoundException;
import com.oocourse.spec3.exceptions.TagIdNotFoundException;
import com.oocourse.spec3.exceptions.PersonIdNotFoundException;
import com.oocourse.spec3.main.Network;
import com.oocourse.spec3.main.Message;
import com.oocourse.spec3.main.Tag;
import com.oocourse.spec3.main.Person;
import com.oocourse.spec3.main.EmojiMessage;
import com.oocourse.spec3.main.RedEnvelopeMessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MyNetwork implements Network {

    private HashMap<Integer, Person> people;
    private HashMap<Integer, Message> messages;
    private HashMap<Integer, Integer> emojiID2Heat;
    private HashSet<Tag> tags = new HashSet<>();
    private Block block;
    private int tripleSum;
    private int doubleSum;

    public MyNetwork() {
        people = new HashMap<>();
        //tags = new HashMap<>();
        messages = new HashMap<>();
        block = new Block();
        emojiID2Heat = new HashMap<>();
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
        for (Tag tag : tags) {
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

    //NEW HW11

    @Override
    public Message getMessage(int id) {
        return messages.get(id);
    }

    @Override
    public boolean containsMessage(int id) {
        return messages.containsKey(id);
    }

    @Override
    public void addMessage(Message message)
            throws EqualMessageIdException, EmojiIdNotFoundException, EqualPersonIdException {
        if (containsMessage(message.getId())) {
            throw new MyEqualMessageIdException(message.getId());
        }
        if (message instanceof EmojiMessage &&
                !containsEmojiId(((EmojiMessage) message).getEmojiId())) {
            throw new MyEmojiIdNotFoundException(message.getSocialValue());
        }
        if (message.getType() == 0 &&
                message.getPerson1().equals(message.getPerson2())) {
            throw new MyEqualPersonIdException(message.getPerson1().getId());
        }
        messages.put(message.getId(), message);
    }

    @Override
    public void sendMessage(int id)
            throws RelationNotFoundException,
            MessageIdNotFoundException, TagIdNotFoundException {
        if (!containsMessage(id)) {
            throw new MyMessageIdNotFoundException(id);
        }
        Message message = getMessage(id);
        if (message.getType() == 0 && !(message.getPerson1().isLinked(message.getPerson2()))) {
            throw new MyRelationNotFoundException(message.getPerson1().getId(),
                    message.getPerson2().getId());
        }
        if (message.getType() == 1 && !message.getPerson1().containsTag(message.getTag().getId())) {
            throw new MyTagIdNotFoundException(message.getTag().getId());
        }
        messages.remove(message.getId());
        MyPerson p1 = (MyPerson) message.getPerson1();
        MyPerson p2 = (MyPerson) message.getPerson2();
        MyTag tag = (MyTag) message.getTag();
        int socialValue = message.getSocialValue();
        if (message.getType() == 0) {
            p1.addSocialValue(socialValue);
            p2.addSocialValue(socialValue);
            p2.addMessage(message);
            if (message instanceof RedEnvelopeMessage) {
                int money = ((RedEnvelopeMessage) message).getMoney();
                p1.addMoney(-money);
                p2.addMoney(money);
            }
        } else {
            //type == 1 要求 tag中必须有person1
            p1.addSocialValue(socialValue);
            int money = 0;
            if (message instanceof RedEnvelopeMessage) {
                if (tag.getSize() != 0) {
                    money = ((RedEnvelopeMessage) message).getMoney() / tag.getSize();
                    p1.addMoney(-money * tag.getSize());
                }
            }
            for (Person person : tag.getPeople().values()) {
                person.addMoney(money);
                person.addSocialValue(socialValue);
            }
        }
        if (message instanceof EmojiMessage) {
            int emojiId = ((EmojiMessage) message).getEmojiId();
            emojiID2Heat.replace(emojiId, emojiID2Heat.get(emojiId) + 1);
        }
    }

    @Override
    public int querySocialValue(int id) throws PersonIdNotFoundException {
        if (!containsPerson(id)) {
            throw new MyPersonIdNotFoundException(id);
        }
        return getPerson(id).getSocialValue();
    }

    @Override
    public List<Message> queryReceivedMessages(int id) throws PersonIdNotFoundException {
        if (!containsPerson(id)) {
            throw new MyPersonIdNotFoundException(id);
        }
        return getPerson(id).getReceivedMessages();
    }

    @Override
    public boolean containsEmojiId(int id) {
        return emojiID2Heat.containsKey(id);
    }

    @Override
    public void storeEmojiId(int id) throws EqualEmojiIdException {
        if (containsEmojiId(id)) {
            throw new MyEqualEmojiIdException(id);
        }
        emojiID2Heat.put(id, 0);
    }

    @Override
    public int queryMoney(int id) throws PersonIdNotFoundException {
        if (!containsPerson(id)) {
            throw new MyPersonIdNotFoundException(id);
        }
        return getPerson(id).getMoney();
    }

    @Override
    public int queryPopularity(int id) throws EmojiIdNotFoundException {
        if (!containsEmojiId(id)) {
            throw new MyEmojiIdNotFoundException(id);
        }
        return emojiID2Heat.get(id);
    }

    @Override
    public int deleteColdEmoji(int limit) {
        messages.entrySet().removeIf(m -> m.getValue() instanceof EmojiMessage &&
                emojiID2Heat.get(m.getValue().getSocialValue()) < limit);
        emojiID2Heat.entrySet().removeIf(m -> m.getValue() < limit);
        return emojiID2Heat.size();
    }

    @Override
    public void clearNotices(int personId) throws PersonIdNotFoundException {
        if (!containsPerson(personId)) {
            throw new MyPersonIdNotFoundException(personId);
        }
        ((MyPerson) getPerson(personId)).clearNotices();
    }

    public Message[] getMessages() {
        return messages.values().toArray(new Message[0]);
    }

    public int[] getEmojiIdList() {
        int[] res = new int[emojiID2Heat.keySet().size()];
        int i = 0;
        for (Integer id : emojiID2Heat.keySet()) {
            res[i++] = id;
        }
        return res;
    }

    public int[] getEmojiHeatList() {
        int[] res = new int[emojiID2Heat.values().size()];
        int i = 0;
        for (Integer id : emojiID2Heat.values()) {
            res[i++] = id;
        }
        return res;
    }
}
