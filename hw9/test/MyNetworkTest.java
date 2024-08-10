import com.oocourse.spec1.exceptions.EqualPersonIdException;
import com.oocourse.spec1.exceptions.EqualRelationException;
import com.oocourse.spec1.exceptions.PersonIdNotFoundException;
import com.oocourse.spec1.exceptions.RelationNotFoundException;
import com.oocourse.spec1.main.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.security.SecureRandom;
import java.util.*;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.Constants.CHARACTERS;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class MyNetworkTest {


    /*@ ensures \result ==
     @         (\sum int i; 0 <= i && i < persons.length;
     @             (\sum int j; i < j && j < persons.length;
     @                 (\sum int k; j < k && k < persons.length
     @                     && getPerson(persons[i].getId()).isLinked(getPerson(persons[j].getId()))
     @                     && getPerson(persons[j].getId()).isLinked(getPerson(persons[k].getId()))
     @                     && getPerson(persons[k].getId()).isLinked(getPerson(persons[i].getId()));
     @                     1)));
     @*/
    private MyNetwork network;
    private List<Person> people;
    private int personSum;

    public MyNetworkTest(MyNetwork n, List<Person> p, int s) {
        this.network = n;
        this.people = p;
        this.personSum = s;
    }

    @Test
    public void queryTripleSum() {
        //正确性
        /*@ ensures \result ==
     @         (\sum int i; 0 <= i && i < persons.length;
     @             (\sum int j; i < j && j < persons.length;
     @                 (\sum int k; j < k && k < persons.length
     @                     && getPerson(persons[i].getId()).isLinked(getPerson(persons[j].getId()))
     @                     && getPerson(persons[j].getId()).isLinked(getPerson(persons[k].getId()))
     @                     && getPerson(persons[k].getId()).isLinked(getPerson(persons[i].getId()));
     @                     1)));
     @*/

        List<Person> oldPeople = new ArrayList<>();
        for (Person person : people) {
            MyPerson myPerson = new MyPerson(person.getId(), person.getName(), person.getAge());
            oldPeople.add(myPerson);
        }
        int oldPersonSum = personSum;
        int[][] oldMap = new int[oldPersonSum][oldPersonSum];
        for (int i = 0; i < oldPersonSum; i++) {
            for (int j = i; j < oldPersonSum; j++) {
                if (people.get(i).isLinked(people.get(j))) {
                    try {
                        oldMap[i][j] = network.queryValue(people.get(i).getId(), people.get(j).getId());
                        oldMap[j][i] = network.queryValue(people.get(i).getId(), people.get(j).getId());
                    } catch (PersonIdNotFoundException | RelationNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    oldMap[i][j] = -1;
                    oldMap[i][j] = -1;
                }
            }
        }
        int oldResult = network.queryTripleSum();

        //\result
        int newReseult = 0;
        int len = personSum;
        //\sum int i; 0 <= i && i < persons.length;
        for (int i = 0; i < len; i++) {
            //\sum int j; i < j && j < persons.length;
            for (int j = i + 1; j < len; j++) {
                //\sum int k; j < k && k < persons.length
                for (int k = j + 1; k < len; k++) {
                    Person myPerson1 = people.get(i);
                    Person myPerson2 = people.get(j);
                    Person myPerson3 = people.get(k);
                    if (myPerson1.isLinked(myPerson2) && myPerson2.isLinked(myPerson3) && myPerson3.isLinked(myPerson1)) {
                        newReseult++;
                    }
                }
            }
        }
        assertEquals(newReseult, oldResult);
        //pure检验: network should not be changed
        List<Person> newPeople = Arrays.asList(network.getPersons());
        int newPeopleSum = newPeople.size();
        assertEquals(oldPersonSum, newPeopleSum);
        for (Person oldPerson : oldPeople) {
            MyPerson newPerson = (MyPerson) network.getPerson(oldPerson.getId());
            assertNotNull(newPerson);
            assertEquals(oldPerson.getName(), newPerson.getName());
            assertEquals(oldPerson.getAge(), newPerson.getAge());
            assertEquals(oldPerson.getId(), newPerson.getId());
        }
        int[][] newMap = new int[newPeopleSum][newPeopleSum];
        for (int i = 0; i < newPeopleSum; i++) {
            for (int j = i; j < newPeopleSum; j++) {
                if (newPeople.get(i).isLinked(newPeople.get(j))) {
                    try {
                        newMap[i][j] = network.queryValue(newPeople.get(i).getId(), newPeople.get(j).getId());
                        newMap[j][i] = network.queryValue(newPeople.get(i).getId(), newPeople.get(j).getId());
                    } catch (PersonIdNotFoundException | RelationNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    newMap[i][j] = -1;
                    newMap[i][j] = -1;
                }
            }
        }
        for (int i = 0; i < newPeopleSum; i++) {
            for (int j = 0; j < newPeopleSum; j++) {
                assertEquals(oldMap[i][j],newMap[i][j]);
            }
        }
    }

    @Parameterized.Parameters
    public static Collection prepareData() {
        int testNum = 60;//测试次数,可根据需求调整

        //该二维数组的类型必须是Object类型的
        //该二维数组中的第一维代表有多少组测试数据,有多少次测试就会创造多少个PathTest对象
        //该二维数组的第二维代表PathTest构造方法中的参数，位置一一对应
        Object[][] object = new Object[testNum][];
        for (int i = 0; i < testNum; i++) {
            List<Person> myPeople = generatePersonList();
            MyNetwork myNetwork = new MyNetwork();
            addAllPerson(myPeople, myNetwork);
            int len = myPeople.size();
            if (i % 3 == 0) {
                fullyConnect(myPeople, myNetwork);
            } else if (i % 3 == 1) {
                sectionConnect(len, myPeople, myNetwork);
            } else{
                nonConnect();
            }
            object[i] = new Object[]{myNetwork, myPeople, len};
        }
        return Arrays.asList(object);
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

    public static void nonConnect() {

    }

    //全连接
    public static void fullyConnect(List<Person> people, MyNetwork network) {
        int len = people.size();
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < len; i++) {
            for (int j = i + 1; j < len; j++) {
                try {
                    network.addRelation(people.get(i).getId(), people.get(j).getId(), random.nextInt(5));
                } catch (PersonIdNotFoundException | EqualRelationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    //部分
    public static void sectionConnect(int personSum, List<Person> people, MyNetwork network) {
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
            if (p1.isLinked(p2)) {
                i--;
            } else {
                try {
                    network.addRelation(p1.getId(), p2.getId(), random.nextInt(5));
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