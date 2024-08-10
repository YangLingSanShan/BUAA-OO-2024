public class Argument {
    private final int maxFloor;
    private final int minFloor;
    private final int initPosition;
    private final int moveTime;
    private final int openTime;
    private final int shutTime;
    private final int maxCapacity;

    public Argument(int maxf, int minf, int ip, int mt, int ot, int st, int mc) {
        maxFloor = maxf;
        minFloor = minf;
        initPosition = ip;
        moveTime = mt;
        openTime = ot;
        shutTime = st;
        maxCapacity = mc;
    }

    public int getMaxFloor() {
        return maxFloor;
    }

    public int getMinFloor() {
        return minFloor;
    }

    public int getInitPosition() {
        return initPosition;
    }

    public int getMoveTime() {
        return moveTime;
    }

    public int getOpenTime() {
        return openTime;
    }

    public int getShutTime() {
        return shutTime;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }
}


//用于存储电梯的基本信息，防止在后续迭代中，每台电梯的性能不一致导致问题
//        可到达楼层：1-11层
//        初始位置：1层
//        数量：6部
//        编号：6部电梯，ID分别为1-6
//        移动一层花费的时间：0.4s
//        开门花费的时间：0.2s
//        关门花费的时间：0.2s
//        限乘人数：6人