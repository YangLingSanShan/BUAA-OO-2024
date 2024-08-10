import com.oocourse.elevator1.TimableOutput;

import java.util.ArrayList;

public class Elevator extends Thread {

    private int elevatorID;
    private Controller controller;
    private boolean status;     //true 开 false 关
    private int position;
    private int end;
    private int direction;      //1上 0停 -1下
    private boolean exit;           //退出
    private Argument argument;
    private ArrayList<ArrayList<Person>> inRequests = new ArrayList<>();
    private ArrayList<ArrayList<Person>> outRequests = new ArrayList<>();

    public Elevator(int id, Argument a) {
        elevatorID = id;
        argument = a;
        controller = new Controller(a, this);
        status = false;     //开始关
        direction = 0;      //开始停
        end = 1;            //开始不知道去哪
        position = a.getInitPosition();
        inRequests.add(0, null);
        outRequests.add(0, null);
        exit = false;
        for (int i = 1; i <= a.getMaxFloor(); i++) {
            inRequests.add(i, new ArrayList<>());
            outRequests.add(i, new ArrayList<>());
        }
    }

    public ArrayList<ArrayList<Person>> getInRequests() {
        return inRequests;
    }

    public ArrayList<ArrayList<Person>> getOutRequests() {
        return outRequests;
    }

    public boolean getStatus() {
        return status;
    }

    public int getPosition() {
        return position;
    }

    public int getDirection() {
        return direction;
    }

    public int getEnd() {
        return end;
    }

    public void changeDirection(int end) {
        if (end == position) {
            direction = 0;
        } else if (end > position) {
            direction = 1;
        } else {
            direction = -1;
        }
    }

    public void shut() throws InterruptedException {
        if (status) {       //如果门开着，那么关上
            sleep(argument.getShutTime());
            TimableOutput.println("CLOSE-" + position + "-" + elevatorID);
            status = false;
        }
    }

    public void open() throws InterruptedException {
        if (!status) {  //开门
            TimableOutput.println("OPEN-" + position + "-" + elevatorID);
            sleep(argument.getOpenTime());
            status = true;
        }
    }

    public void down() throws InterruptedException {
        if (status) {
            shut();
        }
        sleep(argument.getMoveTime());
        position--;
        TimableOutput.println("ARRIVE-" + position + "-" + elevatorID);
    }

    public void up() throws InterruptedException {
        if (status) {
            shut();     //门现在开着
        }
        sleep(argument.getMoveTime());
        position++;
        TimableOutput.println("ARRIVE-" + position + "-" + elevatorID);
    }

    @Override
    public void run() {
        boolean flag = false;
        while (!flag) {
            try {
                getRequest();
                if (controller.isOut()) {
                    open();
                    out();
                }
                if (controller.isIn()) {
                    in();
                }
                shut();
                if (exit) {
                    flag = setEnd();
                }
                //已经最后时刻了
                end = controller.getDirection();    //寻找目标

                while (position != end) {
                    if (direction == 1) {
                        up();
                        direction = position == end ? 0 : direction;
                    } else if (direction == -1) {
                        down();
                        direction = position == end ? 0 : direction;
                    }
                    if (controller.isOut()) {
                        open();
                        out();
                    }
                    if (controller.isIn()) {
                        in();
                    }
                    shut();
                    if (exit) {
                        flag = setEnd();
                    }
                    if (direction == 0) {
                        end = controller.getDirection();
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        //System.out.println("elevator out" + elevatorID);
    }

    public synchronized void addRequest(Person person) {
        ArrayList<Person> floorRequests = inRequests.get(person.getFromFloor());
        floorRequests.add(person);
        this.notifyAll();
    }

    public void out() {
        for (Person person : outRequests.get(position)) {
            TimableOutput.println(
                    "OUT-" + person.getPersonId() + "-" + position + "-" + elevatorID);
        }
        controller.changeTotal(-1 * outRequests.get(position).size());
        outRequests.get(position).clear();

    }

    public synchronized void in() throws InterruptedException {
        ArrayList<Person> up = new ArrayList<>();
        ArrayList<Person> down = new ArrayList<>();
        for (Person person : inRequests.get(position)) {
            if (person.getFromFloor() < person.getToFloor()) {  //上行
                up.add(person);
            } else {
                down.add(person);
            }
        }
        if (direction == 1) {       //电梯上行，把上行的人都扔进去
            inPersonWhenMoving(up);
        } else if (direction == -1) {       //电梯下行
            inPersonWhenMoving(down);
        } else {                   //电梯现在静止，up和down必不同时为空
            if (up.size() > down.size()) {
                inPersonWhenMoving(up);
            } else {
                inPersonWhenMoving(down);
            }
        }
    }

    private void inPersonWhenMoving(ArrayList<Person> direction) throws InterruptedException {
        if (direction.isEmpty()) {     //全反向
            return;
        }
        open();
        if (argument.getMaxCapacity() - controller.getTotal() < direction.size()) {
            for (int i = controller.getTotal(); i < argument.getMaxCapacity(); i++) {
                Person person = direction.get(i - controller.getTotal());
                outRequests.get(person.getToFloor()).add(person);     //输入到运载请求队列
                TimableOutput.println(
                        "IN-" + person.getPersonId() + "-" + position + "-" + elevatorID);
                inRequests.get(position).remove(person);              //从请求队列中删除
            }
            controller.changeTotal(argument.getMaxCapacity() - controller.getTotal());
        } else {
            for (Person person : direction) {
                outRequests.get(person.getToFloor()).add(person);
                TimableOutput.println(
                        "IN-" + person.getPersonId() + "-" + position + "-" + elevatorID);
                inRequests.get(position).remove(person);
            }
            controller.changeTotal(direction.size());
        }
    }
    //    public synchronized void in() throws InterruptedException {
    //        //进人，分配到目的地，改电梯现在人数
    //        //如果这层要进来的人太多了  -> 按照先后顺序加入  max = 6   now:4  add:2
    //        ArrayList<Person> permitted = new ArrayList<>();
    //        for (Person person : inRequests.get(position)) {
    //            if (isSameDirection(person)) {
    //                permitted.add(person);
    //            }
    //        }
    //        if (permitted.isEmpty()) {          //没有人应该加入（所有这层请求队列都是反向）
    //            return;
    //        }
    //        open();                             //这时候才开门
    //        //permitted是当前状态能进来的人的队列        //total = 3  3 4 5 6
    //        if (argument.getMaxCapacity() - controller.getTotal() < permitted.size()) {
    //            for (int i = controller.getTotal(); i < argument.getMaxCapacity(); i++) {
    //                Person person = permitted.get(i - controller.getTotal());
    //                outRequests.get(person.getToFloor()).add(person);     //输入到运载请求队列
    //                TimableOutput.println(
    //                        "IN-" + person.getPersonId() + "-" + position + "-" + elevatorID);
    //                inRequests.get(position).remove(person);         //从请求队列中删除
    //            }
    //            controller.changeTotal(argument.getMaxCapacity() - controller.getTotal());
    //        } else {        //如果这层要进来的人都能容纳
    //            for (Person person : permitted) {
    //                outRequests.get(person.getToFloor()).add(person);
    //                TimableOutput.println(
    //                        "IN-" + person.getPersonId() + "-" + position + "-" + elevatorID);
    //                inRequests.get(position).remove(person);
    //            }
    //            controller.changeTotal(permitted.size());
    //        }
    //    }

    public synchronized boolean setEnd() {
        this.notifyAll();
        exit = true;
        if (controller.isEnd()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isEmpty() {
        for (int i = 1; i <= argument.getMaxFloor(); i++) {
            if (!inRequests.get(i).isEmpty() || !outRequests.get(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public synchronized void getRequest() {
        if (isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}