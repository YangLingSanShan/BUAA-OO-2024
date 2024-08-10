import java.math.BigInteger;
import java.util.HashMap;

public class Const extends Factor {
    private BigInteger number;

    public Const(BigInteger number) {
        this.number = number;
        this.setNegative(true);
    }

    @Override
    public String getType() {
        return "Const";
    }

    public BigInteger getNumber() {
        return this.number;
    }

    @Override
    public HashMap<Factor, HashMap<BigInteger, BigInteger>> getExpression() {
        Const c = new Const(new BigInteger("0"));
        HashMap<BigInteger, BigInteger> second = new HashMap<>();
        second.put(new BigInteger("0"), number);
        HashMap<Factor, HashMap<BigInteger, BigInteger>> res = new HashMap<>();
        res.put(c, second);
        return res;
    }

    @Override
    public void add(Const c, boolean flag) {
        if (flag) {
            this.number = this.number.add(c.number);
        } else {
            this.number = this.number.add(c.number.multiply(BigInteger.valueOf(-1)));
            this.number = this.number.abs();
        }
    }

}
