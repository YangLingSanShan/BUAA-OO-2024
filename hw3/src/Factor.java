import java.math.BigInteger;
import java.util.HashMap;
import java.util.TreeMap;

public class Factor {
    private boolean negative;
    private String ele;

    public void setNegative(boolean negative) {
        this.negative = negative;
    }

    public boolean getNegative() {
        return this.negative;
    }

    //x <0,<1,1>>
    public HashMap<Factor, TreeMap<BigInteger, BigInteger>> getExpression() {
        Const c = new Const(new BigInteger("0"));
        TreeMap<BigInteger, BigInteger> second = new TreeMap<>();
        if (getNegative()) {
            second.put(new BigInteger("1"), new BigInteger("1"));
        } else {
            second.put(new BigInteger("1"), new BigInteger("-1"));
        }
        HashMap<Factor, TreeMap<BigInteger, BigInteger>> res = new HashMap<>();
        res.put(c, second);
        return res;
    }

    public Factor differentiate() {
        return new Const(BigInteger.valueOf(1));
    }

    public void setEle(String s) {
        ele = s;
    }

    public String getType() {
        return "Factor";
    }

    public String getEle() {
        return ele;
    }

    public void renew(HashMap<String, Factor> map) {

    }

    public boolean isEquivalent(Factor factor) {
        return true;
    }

    public void add(Const c, boolean flag) {
    }
}
