import java.math.BigInteger;
import java.util.HashMap;

public class Pow extends Factor {
    private Factor base;
    private BigInteger exponent;

    public Pow(Factor base, BigInteger exponent) {
        this.base = base;
        this.exponent = exponent;
    }

    @Override
    public HashMap<Factor, HashMap<BigInteger, BigInteger>> getExpression() {
        HashMap<Factor, HashMap<BigInteger, BigInteger>> res = new HashMap<>();
        HashMap<Factor, HashMap<BigInteger, BigInteger>> temp = new HashMap<>();
        if (exponent.compareTo(BigInteger.valueOf(0)) == 0) {
            BigInteger a = new BigInteger("0");
            BigInteger b = new BigInteger("1");
            Const c = new Const(a);
            HashMap<BigInteger,BigInteger> ans = new HashMap<>();
            ans.put(a,b);
            res.put(c,ans);
        } else {
            if (base.getType().equals("Factor")) {
                Const c = new Const(new BigInteger("0"));
                HashMap<BigInteger, BigInteger> second = new HashMap<>();
                second.put(exponent, new BigInteger("1"));    //指数。系数
                res.put(c, second);
            } else {    //expression ^ exponent
                Calculator calculator = new Calculator();
                res = base.getExpression();
                if (!base.getNegative()) {
                    res = calculator.Mul(res, new Const(BigInteger.valueOf(-1)).getExpression());
                }
                temp = base.getExpression();
                int ex = Integer.parseInt(exponent.toString());
                for (int i = 1; i < ex; i++) {
                    res = calculator.Mul(temp, res);
                }
            }
        }
        return res;
    }

    public Factor getBase() {
        return this.base;
    }

    public BigInteger getExponent() {
        return this.exponent;
    }

    @Override
    public String getType() {
        return "Pow";
    }

    @Override
    public void renew(HashMap<String, Factor> map) {
        if (base.getType().equals("Expr")) {
            base.renew(map);
        } else if (base.getType().equals("Factor")) {
            if (map.containsKey(base.getEle())) {
                base = map.get(base.getEle());
            }
        }
    }

    @Override
    public boolean isEquivalent(Factor factor) {
        Pow p = (Pow) factor;
        return this.exponent.compareTo(p.exponent) == 0;
    }
}
