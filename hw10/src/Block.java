import com.oocourse.spec2.main.Network;
import com.oocourse.spec2.main.Person;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Map;

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

    }        // 从起点到终点的最短路径算法

    public int Dijkstra(Person startPerson, Person endPerson, Network network) {
        // 优先队列中的元素为 {Person id, 起点到该点的距离}
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]);
        pq.offer(new int[]{startPerson.getId(), 0});
        HashSet<Integer> visited = new HashSet<>();
        HashMap<Integer, Person> map = ((MyPerson) startPerson).getAcquaintanceHashMap();
        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int curId = cur[0];
            int curDist = cur[1];
            if (curId == endPerson.getId()) {
                return curDist;
            }
            if (visited.contains(curId)) {
                continue;
            }
            visited.add(curId);
            Person curPerson = network.getPerson(curId);
            if (curPerson == null) {
                continue; // 如果当前节点为null，直接跳过
            }
            map = ((MyPerson) curPerson).getAcquaintanceHashMap();
            if (map == null) {
                continue; // 如果当前节点的熟人列表为null，直接跳过
            }
            for (Map.Entry<Integer, Person> entry : map.entrySet()) {
                int nextId = entry.getKey();
                if (nextId != curId && !visited.contains(nextId)) { // 排除自己和已访问过的人
                    pq.offer(new int[]{nextId, curDist + 1}); // 距离直接设为1
                }
            }
        }

        return -1; // 如果找不到路径，返回-1
    }
}