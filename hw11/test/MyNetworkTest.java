import com.oocourse.spec3.exceptions.*;
import com.oocourse.spec3.main.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.security.SecureRandom;
import java.util.*;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.Constants.CHARACTERS;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class MyNetworkTest {

    private MyNetwork network;
    private MyNetwork oldNetwork;

    public MyNetworkTest(MyNetwork n, MyNetwork o) {
        this.network = n;
        this.oldNetwork = o;

    }

    @Test
    public void deleteColdEmoji() {
        Random random = new Random(System.currentTimeMillis());

        int limit = random.nextInt(7);
        int oldResult = oldNetwork.deleteColdEmoji(limit);

    /*@ public normal_behavior
      @ assignable emojiIdList, emojiHeatList, messages;
     1 @ ensures (\forall int i; 0 <= i && i < \old(emojiIdList.length);
      @          (\old(emojiHeatList[i] >= limit) ==>
      @          (\exists int j; 0 <= j && j < emojiIdList.length; emojiIdList[j] == \old(emojiIdList[i]))));
     2 @ ensures (\forall int i; 0 <= i && i < emojiIdList.length;
      @          (\exists int j; 0 <= j && j < \old(emojiIdList.length);
      @          emojiIdList[i] == \old(emojiIdList[j]) && emojiHeatList[i] == \old(emojiHeatList[j])));
     3 @ ensures emojiIdList.length ==
      @          (\num_of int i; 0 <= i && i < \old(emojiIdList.length); \old(emojiHeatList[i] >= limit));
     4 @ ensures emojiIdList.length == emojiHeatList.length;
     5 @ ensures (\forall int i; 0 <= i && i < \old(messages.length);
      @          (\old(messages[i]) instanceof EmojiMessage &&
      @           containsEmojiId(\old(((EmojiMessage)messages[i]).getEmojiId()))  ==> \not_assigned(\old(messages[i])) &&
      @           (\exists int j; 0 <= j && j < messages.length; messages[j].equals(\old(messages[i])))));
     6 @ ensures (\forall int i; 0 <= i && i < \old(messages.length);
      @          (!(\old(messages[i]) instanceof EmojiMessage) ==> \not_assigned(\old(messages[i])) &&
      @           (\exists int j; 0 <= j && j < messages.length; messages[j].equals(\old(messages[i])))));
     7 @ ensures messages.length == (\num_of int i; 0 <= i && i <= \old(messages.length);
      @          (\old(messages[i]) instanceof EmojiMessage) ==>
      @           (containsEmojiId(\old(((EmojiMessage)messages[i]).getEmojiId()))));
     8 @ ensures \result == emojiIdList.length;
      @*/

        Message[] oldMessages = oldNetwork.getMessages();
        int[] oldNetworkEmojiIdList = oldNetwork.getEmojiIdList();
        int[] oldNetworkEmojiHeat = oldNetwork.getEmojiHeatList();

        Message[] newMessages = network.getMessages();
        int[] newNetworkEmojiIdList = network.getEmojiIdList();
        int[] newNetworkEmojiHeat = network.getEmojiHeatList();

        List<Message> remainingMessages = new ArrayList<>();
        List<Integer> remainingEmojiIdList = new ArrayList<>();
        List<Integer> remainingEmojiHeatList = new ArrayList<>();

        for (Message message : newMessages) {
            if (message instanceof EmojiMessage) {
                for (int i = 0; i < newNetworkEmojiIdList.length; i++) {
                    if (newNetworkEmojiIdList[i] == ((EmojiMessage) message).getEmojiId()) {
                        if (newNetworkEmojiHeat[i] >= limit) {
                            remainingMessages.add(message);
                            remainingEmojiIdList.add(newNetworkEmojiIdList[i]);
                            remainingEmojiHeatList.add(newNetworkEmojiHeat[i]);
                            break;
                        }
                    }
                }
            } else {
                remainingMessages.add(message);
            }
        }

        //\result
        int result = remainingEmojiIdList.size();
        assertEquals(result, oldResult);
        //message检验:
        assertEquals(oldMessages.length, remainingMessages.size());
        for (int i = 0; i < oldMessages.length; i++) {
            Message oldMessage = oldMessages[i];
            Message newMessage = remainingMessages.get(i);
            assertEquals(oldMessage.getId(), newMessage.getId());
            assertEquals(oldMessage.getSocialValue(), newMessage.getSocialValue());
            assertEquals(oldMessage.getType(), newMessage.getType());

            MyPerson oldPerson = (MyPerson) oldMessage.getPerson1();
            MyPerson newPerson = (MyPerson) newMessage.getPerson1();
            assertTrue(oldPerson.equals(newPerson));

            if (oldMessage.getType() == 0) {
                assertNull(oldMessage.getTag());
                assertNull(newMessage.getTag());
                MyPerson oldPerson2 = (MyPerson) oldMessage.getPerson2();
                MyPerson newPerson2 = (MyPerson) newMessage.getPerson2();
                assertTrue(oldPerson2.equals(newPerson2));
            } else {
                assertNull(oldMessage.getPerson2());
                assertNull(newMessage.getPerson2());
                MyTag oldTag = (MyTag) oldMessage.getTag();
                MyTag newTag = (MyTag) newMessage.getTag();
                assertTrue(oldTag.equals(newTag));
            }



            if (oldMessage instanceof MyNoticeMessage && newMessage instanceof MyNoticeMessage) {
                MyNoticeMessage oldNotice = (MyNoticeMessage) oldMessage;
                MyNoticeMessage newNotice = (MyNoticeMessage) newMessage;
                assertEquals(oldNotice.getString(), newNotice.getString());
            } else if (oldMessage instanceof MyEmojiMessage && newMessage instanceof MyEmojiMessage) {
                MyEmojiMessage oldEmoji = (MyEmojiMessage) oldMessage;
                MyEmojiMessage newEmoji = (MyEmojiMessage) newMessage;
                assertEquals(oldEmoji.getEmojiId(), newEmoji.getEmojiId());
            } else if (oldMessage instanceof MyRedEnvelopeMessage && newMessage instanceof MyRedEnvelopeMessage) {
                MyRedEnvelopeMessage oldRed = (MyRedEnvelopeMessage) oldMessage;
                MyRedEnvelopeMessage newRed = (MyRedEnvelopeMessage) newMessage;
                assertEquals(oldRed.getMoney(), newRed.getMoney());
            } else if (oldMessage instanceof MyMessage && newMessage instanceof  MyMessage) {
                assertTrue(true);
            } else {
                assertTrue(false);
            }
        }
        boolean flag;
        for (int i = 0; i < result; i++) {
            flag = true;
            for (int j = 0; j < result; j++) {
                if (oldNetworkEmojiIdList[i] == remainingEmojiIdList.get(j)) {
                    assertEquals(oldNetworkEmojiHeat[i], (int) remainingEmojiHeatList.get(j));
                    assertTrue(remainingEmojiHeatList.get(i) >= limit);
                    flag = false;
                    break;
                }
            }
            assertFalse(flag);
        }
    }

    @Parameterized.Parameters
    public static Collection prepareData() {
        int testNum = 70;//测试次数,可根据需求调整

        //该二维数组的类型必须是Object类型的
        //该二维数组中的第一维代表有多少组测试数据,有多少次测试就会创造多少个PathTest对象
        //该二维数组的第二维代表PathTest构造方法中的参数，位置一一对应
        Object[][] object = new Object[testNum][];
        for (int i = 0; i < testNum; i++) {
            List<Person> myPeople = generatePersonList();
            List<Person> oldPeople = clonePeople(myPeople);
            MyNetwork myNetwork = new MyNetwork();
            MyNetwork oldNetwork = new MyNetwork();
            addAllPerson(myPeople, myNetwork);
            addAllPerson(oldPeople, oldNetwork);
            int len = myPeople.size();
            if (i % 7 == 0) {
                fullyConnect(myPeople, myNetwork, oldPeople, oldNetwork);
                addMessage(myNetwork, oldNetwork, oldPeople, myPeople);
            } else if (i % 7 == 1) {
                sectionConnect(len, myPeople, myNetwork, oldPeople, oldNetwork);
                addMessage(myNetwork, oldNetwork, oldPeople, myPeople);
            } else if (i % 7 == 2) {
                lineConnect(myPeople, myNetwork, oldPeople, oldNetwork);
                addMessage(myNetwork, oldNetwork, oldPeople, myPeople);
            } else if (i % 7 == 3) {
                sameConnect(myPeople, myNetwork, oldPeople, oldNetwork);
                addMessage(myNetwork, oldNetwork, oldPeople, myPeople);
            } else if (i % 7 == 4) {
                noneConnect();
            } else if (i % 7 == 5) {
                sameRandConnect(myPeople, myNetwork, oldPeople, oldNetwork);
                addMessage(myNetwork, oldNetwork, oldPeople, myPeople);
            } else {
                partialSameConnect(myPeople, myNetwork, oldPeople, oldNetwork);
                addMessage(myNetwork, oldNetwork, oldPeople, myPeople);
            }
            object[i] = new Object[]{myNetwork, oldNetwork};
        }
        return Arrays.asList(object);
    }

    private static void addMessage(Network myNetwork, Network oldNetwork, List<Person> oldPeople, List<Person> people) {
        int messageSum = 300;
        for (int j = 1; j <= 4; j++) {
            for (int i = 1; i <= 191; i++) {
                if (i % 4 == 1) {
                    addNormalMessage(i, myNetwork, oldNetwork, oldPeople, people);
                } else if (i % 4 == 2) {
                    try {
                        myNetwork.storeEmojiId(i + 1000);
                        oldNetwork.storeEmojiId(i + 1000);
                    } catch (EqualEmojiIdException ignored) {
                    }
                    addEmojiMessage(i, myNetwork, oldNetwork, oldPeople, people);
                } else if (i % 4 == 3) {
                    addRedPalMessage(i, myNetwork, oldNetwork, oldPeople, people);
                } else {
                    addNoticeMessage(i, myNetwork, oldNetwork, oldPeople, people);
                }
                try {
                    myNetwork.sendMessage(i);
                    oldNetwork.sendMessage(i);
                } catch (RelationNotFoundException | MessageIdNotFoundException | TagIdNotFoundException ignored) {
                }
            }
        }
        for (int j = 1; j <= 3; j++) {
            for (int i = 1; i <= 103; i++) {
                if (i % 4 == 1) {
                    addNormalMessage(i, myNetwork, oldNetwork, oldPeople, people);
                } else if (i % 4 == 2) {
                    addEmojiMessage(i, myNetwork, oldNetwork, oldPeople, people);
                } else if (i % 4 == 3) {
                    addRedPalMessage(i, myNetwork, oldNetwork, oldPeople, people);
                } else {
                    addNoticeMessage(i, myNetwork, oldNetwork, oldPeople, people);
                }
                try {
                    myNetwork.sendMessage(i);
                    oldNetwork.sendMessage(i);
                } catch (RelationNotFoundException | MessageIdNotFoundException | TagIdNotFoundException ignored) {
                }
            }
        }
        for (int i = 1; i <= messageSum; i++) {
            if (i % 4 == 1) {
                addNormalMessage(i, myNetwork, oldNetwork, oldPeople, people);
            } else if (i % 4 == 2) {
                try {
                    myNetwork.storeEmojiId(i + 1000);
                    oldNetwork.storeEmojiId(i + 1000);
                } catch (EqualEmojiIdException ignored) {
                }
                addEmojiMessage(i, myNetwork, oldNetwork, oldPeople, people);
            } else if (i % 4 == 3) {
                addRedPalMessage(i, myNetwork, oldNetwork, oldPeople, people);
            } else {
                addNoticeMessage(i, myNetwork, oldNetwork, oldPeople, people);
            }
        }
    }

    private static void addNoticeMessage(int id, Network myNetwork, Network oldNetwork, List<Person> oldPeople, List<Person> people) {
        Random random = new Random(System.currentTimeMillis());
        int messageID = id;
        String messageText = generateName(10);
        int p1index = random.nextInt(oldPeople.size());
        int p2index = random.nextInt(oldPeople.size());
        while (p1index == p2index) {
            p2index = random.nextInt(oldPeople.size());
        }
        try {
            myNetwork.addMessage(new MyNoticeMessage(messageID, messageText, oldPeople.get(p1index), oldPeople.get(p2index)));
            oldNetwork.addMessage(new MyNoticeMessage(messageID, messageText, oldPeople.get(p1index), oldPeople.get(p2index)));
        } catch (EqualMessageIdException | EmojiIdNotFoundException | EqualPersonIdException ignored) {
        }
    }

    private static void addRedPalMessage(int id, Network myNetwork, Network oldNetwork, List<Person> oldPeople, List<Person> people) {
        Random random = new Random(System.currentTimeMillis());
        int messageID = id;
        int luckMoney = random.nextInt(500);
        int p1index = random.nextInt(oldPeople.size());
        int p2index = random.nextInt(oldPeople.size());
        while (p1index == p2index) {
            p2index = random.nextInt(oldPeople.size());
        }
        try {
            myNetwork.addMessage(new MyRedEnvelopeMessage(messageID, luckMoney, oldPeople.get(p1index), oldPeople.get(p2index)));
            oldNetwork.addMessage(new MyRedEnvelopeMessage(messageID, luckMoney, oldPeople.get(p1index), oldPeople.get(p2index)));
        } catch (EqualMessageIdException | EmojiIdNotFoundException | EqualPersonIdException ignored) {
        }
    }

    private static void addEmojiMessage(int id, Network myNetwork, Network oldNetwork, List<Person> oldPeople, List<Person> people) {
        Random random = new Random(System.currentTimeMillis());
        int messageID = id;
        int p1index = random.nextInt(oldPeople.size());
        int p2index = random.nextInt(oldPeople.size());
        while (p1index == p2index) {
            p2index = random.nextInt(oldPeople.size());
        }
        try {
            myNetwork.addMessage(new MyEmojiMessage(messageID, messageID + 1000, oldPeople.get(p1index), oldPeople.get(p2index)));
            oldNetwork.addMessage(new MyEmojiMessage(messageID, messageID + 1000, oldPeople.get(p1index), oldPeople.get(p2index)));
        } catch (EqualMessageIdException | EmojiIdNotFoundException | EqualPersonIdException ignored) {
        }
    }

    private static void addNormalMessage(int id, Network myNetwork, Network oldNetwork, List<Person> oldPeople, List<Person> people) {
        Random random = new Random(System.currentTimeMillis());
        int messageID = id;
        int messageValue = random.nextInt(500);
        int p1index = random.nextInt(oldPeople.size());
        int p2index = random.nextInt(oldPeople.size());
        while (p1index == p2index) {
            p2index = random.nextInt(oldPeople.size());
        }
        try {
            myNetwork.addMessage(new MyMessage(messageID, messageValue, oldPeople.get(p1index), oldPeople.get(p2index)));
            oldNetwork.addMessage(new MyMessage(messageID, messageValue, oldPeople.get(p1index), oldPeople.get(p2index)));
        } catch (EqualMessageIdException | EmojiIdNotFoundException | EqualPersonIdException ignored) {
        }

    }

    private static List<Person> clonePeople(List<Person> myPeople) {
        List<Person> res = new ArrayList<>();
        for (Person person : myPeople) {
            res.add(new MyPerson(person.getId(), person.getName(), person.getAge()));
        }
        return res;
    }

    private static void sameRandConnect(List<Person> people, MyNetwork network, List<Person> oldPeople, MyNetwork oldNetwork) {
        int personSum = people.size();
        Random random = new Random(System.currentTimeMillis());
        int edge = random.nextInt(personSum * (personSum - 1) / 2) / 2;
        for (int i = 0; i < edge; i++) {
            int id1 = random.nextInt(personSum - 1);
            int id2 = random.nextInt(personSum - 1);
            while (id2 == id1) {
                id2 = random.nextInt(personSum - 1);
            }
            Person p1 = people.get(id1);
            Person p2 = people.get(id2);
            Person op1 = oldPeople.get(id1);
            Person op2 = oldPeople.get(id2);
            if (p1.isLinked(p2)) {
                i--;
            } else {
                try {
                    network.addRelation(p1.getId(), p2.getId(), 10);
                    oldNetwork.addRelation(op1.getId(), op2.getId(), 10);
                } catch (PersonIdNotFoundException | EqualRelationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void partialSameConnect(List<Person> people, MyNetwork network, List<Person> oldPeople, MyNetwork oldNetwork) {
        int len = people.size();
        for (int i = 0; i < len - 2; i += 2) {
            try {
                network.addRelation(people.get(i).getId(), people.get(i + 2).getId(), 100);
                oldNetwork.addRelation(oldPeople.get(i).getId(), oldPeople.get(i + 2).getId(), 100);
            } catch (PersonIdNotFoundException | EqualRelationException e) {
                throw new RuntimeException(e);
            }
        }
        sectionConnect(len, people, network, oldPeople, oldNetwork);
    }

    public static void noneConnect() {
    }

    public static void sameConnect(List<Person> people, MyNetwork network, List<Person> oldPeople, MyNetwork oldNetwork) {
        int len = people.size();
        for (int i = 0; i < len; i++) {
            for (int j = i + 1; j < len; j++) {
                try {
                    network.addRelation(people.get(i).getId(), people.get(j).getId(), 100);
                    oldNetwork.addRelation(oldPeople.get(i).getId(), oldPeople.get(j).getId(), 100);
                } catch (PersonIdNotFoundException | EqualRelationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void lineConnect(List<Person> people, MyNetwork network, List<Person> oldPeople, MyNetwork oldNetwork) {
        int len = people.size();
        for (int i = 0; i < len - 1; i++) {
            try {
                network.addRelation(people.get(i).getId(), people.get(i + 1).getId(), i + 10);
                oldNetwork.addRelation(oldPeople.get(i).getId(), oldPeople.get(i + 1).getId(), i + 10);
            } catch (PersonIdNotFoundException | EqualRelationException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            network.addRelation(people.get(0).getId(), people.get(len - 1).getId(), len - 1 + 10);
            oldNetwork.addRelation(oldPeople.get(0).getId(), oldPeople.get(len - 1).getId(), len - 1 + 10);
        } catch (PersonIdNotFoundException | EqualRelationException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addAllPerson(List<Person> people, MyNetwork network) {
        for (Person person : people) {
            try {
                network.addPerson(person);
            } catch (EqualPersonIdException e) {
                throw new RuntimeException(e);
            }
        }
    }


    //全连接
    public static void fullyConnect(List<Person> people, MyNetwork network, List<Person> oldPeople, MyNetwork oldNetwork) {
        int len = people.size();
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < len; i++) {
            for (int j = i + 1; j < len; j++) {
                try {
                    int value = random.nextInt(20);
                    network.addRelation(people.get(i).getId(), people.get(j).getId(), value);
                    oldNetwork.addRelation(oldPeople.get(i).getId(), oldPeople.get(j).getId(), value);
                } catch (PersonIdNotFoundException | EqualRelationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    //部分
    public static void sectionConnect(int personSum, List<Person> people, MyNetwork network, List<Person> oldPeople, MyNetwork oldNetwork) {
        Random random = new Random(System.currentTimeMillis());
        int edge = random.nextInt(personSum * (personSum - 1) / 2) / 2;
        for (int i = 0; i < edge; i++) {
            int id1 = random.nextInt(personSum - 1);
            int id2 = random.nextInt(personSum - 1);
            while (id2 == id1) {
                id2 = random.nextInt(personSum - 1);
            }
            Person p1 = people.get(id1);
            Person p2 = people.get(id2);
            Person op1 = oldPeople.get(id1);
            Person op2 = oldPeople.get(id2);
            if (p1.isLinked(p2)) {
                i--;
            } else {
                try {
                    int value = random.nextInt(10);
                    network.addRelation(p1.getId(), p2.getId(), value);
                    oldNetwork.addRelation(op1.getId(), op2.getId(), value);
                } catch (PersonIdNotFoundException | EqualRelationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    //随机生成人
    public static List<Person> generatePersonList() {
        List<Person> res = new ArrayList<>();
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        HashSet<Integer> id = generatePersonID();
        for (Integer i : id) {
            Person person = new MyPerson(i, generateName(5), random.nextInt(200));
            res.add(person);
        }
        return res;
    }

    public static HashSet<Integer> generatePersonID() {
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        int minPersonSum = 80;
        int maxPersonSum = 100;
        int minID = -900;
        int maxID = 900;
        int personSum = random.nextInt(maxPersonSum - minPersonSum + 1) + minPersonSum;
        HashSet<Integer> personID = new HashSet<>();
        while (personID.size() < personSum) {
            int randomNumber = random.nextInt(maxID - minID + 1) + minID;
            personID.add(randomNumber);
        }
        return personID;
    }

    public static String generateName(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }
}