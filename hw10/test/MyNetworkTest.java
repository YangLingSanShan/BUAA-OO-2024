import com.oocourse.spec2.exceptions.*;
import com.oocourse.spec2.main.Person;
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
    private List<Person> people;
    private List<Person> oldPeople;

    public MyNetworkTest(MyNetwork n, MyNetwork o, List<Person> p, List<Person> op) {
        this.network = n;
        this.oldNetwork = o;
        this.people = p;
        this.oldPeople = op;

    }

    @Test
    public void queryCoupleSum() {
        Person[] oldPersons = oldNetwork.getPersons();
        int oldPersonSum = oldPersons.length;
        int oldResult = oldNetwork.queryCoupleSum();


            /*@ ensures \result ==
      @         (\sum int i, j; 0 <= i && i < j && j < persons.length
      @                         && persons[i].acquaintance.length > 0 && queryBestAcquaintance(persons[i].getId()) == persons[j].getId()
      @                         && persons[j].acquaintance.length > 0 && queryBestAcquaintance(persons[j].getId()) == persons[i].getId();
      @                         1);
      @*/
        //\result
        int result = 0;
        int len = people.size();
        for (int i = 0; i < len; i++) {
            for (int j = i + 1; j < len; j++) {
                try {
                    if (network.queryBestAcquaintance(people.get(i).getId()) == people.get(j).getId() &&
                            network.queryBestAcquaintance(people.get(j).getId()) == people.get(i).getId()) {
                        result++;
                    }
                } catch (PersonIdNotFoundException | AcquaintanceNotFoundException ignored) {
                }
            }
        }
        assertEquals(result, oldResult);
        //pure检验: network should not be changed
        Person[] newPersons = network.getPersons();
        int newPeopleSum = newPersons.length;
        assertEquals(oldPersonSum, newPeopleSum);
        for (int i = 0; i < newPeopleSum; i++) {
            assertTrue(((MyPerson) oldPersons[i]).strictEquals(newPersons[i]));
        }
    }

    @Parameterized.Parameters
    public static Collection prepareData() {
        int testNum = 250;//测试次数,可根据需求调整

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
            } else if (i % 7 == 1) {
                sectionConnect(len, myPeople, myNetwork, oldPeople, oldNetwork);
            } else if (i % 7 == 2) {
                lineConnect(myPeople, myNetwork, oldPeople, oldNetwork);
            } else if (i % 7 == 3) {
                sameConnect(myPeople, myNetwork, oldPeople, oldNetwork);
            } else if (i % 7 == 4) {
                noneConnect();
            } else if (i % 7 == 5) {
                sameRandConnect(myPeople, myNetwork, oldPeople, oldNetwork);
            } else {
                partialSameConnect(myPeople, myNetwork, oldPeople, oldNetwork);
            }
            object[i] = new Object[]{myNetwork, oldNetwork, myPeople, oldPeople};
        }
        return Arrays.asList(object);
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