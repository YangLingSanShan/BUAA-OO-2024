import com.oocourse.spec1.main.Person;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class Block {
    private int blockSum;
    private int index;
    private HashMap<Integer, HashSet<Person>> blocks;
    private HashMap<Integer, Integer> id2Block;

    public Block() {
        blockSum = 0;
        index = 0;
        blocks = new HashMap<>();
        id2Block = new HashMap<>();
    }

    public void addBlock() {
        blockSum++;
    }

    public void addPair(Person person1, Person person2) {
        int blockIDofPerson1 = id2Block.getOrDefault(person1.getId(), -1);
        int blockIDofPerson2 = id2Block.getOrDefault(person2.getId(), -1);
        if (blockIDofPerson1 == -1 && blockIDofPerson2 == -1) {
            addPair(person1, person2, index);
            index++;
            blockSum--;
            return;
        }   //均为孤立节点
        if (blockIDofPerson1 == -1) {
            addPair(person1, blockIDofPerson2);
            blockSum--;
            return;
        }   //Person1 孤立
        if (blockIDofPerson2 == -1) {
            addPair(person2, blockIDofPerson1);
            blockSum--;
            return;
        }   //Person2 孤立
        if (blockIDofPerson1 != blockIDofPerson2) {     //不在一个块中
            HashSet<Person> des = blocks.get(blockIDofPerson1);
            HashSet<Person> res = blocks.get(blockIDofPerson2);
            for (Person person : res) {
                id2Block.replace(person.getId(), blockIDofPerson1);
            }
            des.addAll(res);
            blocks.remove(blockIDofPerson2);
            blockSum--;
        }

    }

    public void addPair(Person person1, Person person2, int id) {
        id2Block.put(person1.getId(), id);
        id2Block.put(person2.getId(), id);
        blocks.put(id, new HashSet<>());
        blocks.get(id).add(person1);
        blocks.get(id).add(person2);
    }

    public void addPair(Person person, int id) {
        id2Block.put(person.getId(), id);
        blocks.get(id).add(person);
    }

    public void removePair(Person person1, Person person2) {
        //假设为 person1 -> person2
        HashSet<Person> subBlock = blocks.get(id2Block.get(person1.getId()));
        for (Person person : subBlock) {
            ((MyPerson) person).setVisited(false);
        }
        dfs(person1);
        if (!((MyPerson) person2).getVisited()) {   //需要分裂:person1 带着他的人滚蛋
            HashSet<Person> newSubBlock = new HashSet<>();
            Iterator<Person> iterator = subBlock.iterator();
            while (iterator.hasNext()) {
                MyPerson person = (MyPerson) iterator.next();
                if (person.getVisited()) {
                    newSubBlock.add(person);
                    id2Block.replace(person.getId(), index);
                    iterator.remove(); // 从 block 中移除该元素
                }
            }
            blocks.put(index, newSubBlock);
            index++;
            blockSum++;
        }

    }

    public void dfs(Person person) {
        ((MyPerson) person).setVisited(true);
        HashSet<Person> acquaintaince = ((MyPerson) person).getAcquaintance();
        for (Person p : acquaintaince) {
            if (!((MyPerson) p).getVisited()) {
                dfs(p);
            }
        }
    }

    public int getBlockSum() {
        return blockSum;
    }

    public boolean isSameBlock(int id1, int id2) {
        return id1 == id2 ||
                id2Block.getOrDefault(id1, -1).equals(id2Block.getOrDefault(id2, -2));
    }
}
