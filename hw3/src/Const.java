import java.math.BigInteger;
import java.util.HashMap;
import java.util.TreeMap;

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
    public HashMap<Factor, TreeMap<BigInteger, BigInteger>> getExpression() {
        Const c = new Const(new BigInteger("0"));
        TreeMap<BigInteger, BigInteger> second = new TreeMap<>();

        second.put(new BigInteger("0"), number);

        HashMap<Factor, TreeMap<BigInteger, BigInteger>> res = new HashMap<>();
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

    @Override
    public Factor differentiate() {
        return null;
    }
}
