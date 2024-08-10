import java.math.BigInteger;
import java.util.HashMap;
import java.util.TreeMap;

public class Pow extends Factor {
    private Factor base;
    private BigInteger exponent;

    public Pow(Factor base, BigInteger exponent) {
        this.base = base;
        this.exponent = exponent;
        this.setNegative(true);
    }

    @Override
    public HashMap<Factor, TreeMap<BigInteger, BigInteger>> getExpression() {
        HashMap<Factor, TreeMap<BigInteger, BigInteger>> res = new HashMap<>();
        HashMap<Factor, TreeMap<BigInteger, BigInteger>> temp = new HashMap<>();
        if (exponent.compareTo(BigInteger.valueOf(0)) == 0) {
            BigInteger a = new BigInteger("0");
            BigInteger b = new BigInteger("1");
            Const c = new Const(a);
            TreeMap<BigInteger, BigInteger> ans = new TreeMap<>();
            ans.put(a, b);
            res.put(c, ans);
        } else {
            if (base.getType().equals("Factor")) {      //x^7  (-1)^7
                Const c = new Const(new BigInteger("0"));
                TreeMap<BigInteger, BigInteger> second = new TreeMap<>();
                second.put(exponent, new BigInteger("1"));    //指数。系数
                res.put(c, second);
            } else {    //expression ^ exponent
                Calculator calculator = new Calculator();
                res = base.getExpression();
                temp = base.getExpression();// base.getExpression();
                if (!base.getNegative()) {
                    res = calculator.Mul(res, new Const(BigInteger.valueOf(-1)).getExpression());
                    temp = calculator.Mul(temp, new Const(BigInteger.valueOf(-1)).getExpression());
                }
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
        if (base.getType().equals("Expr") ||
                base.getType().equals("Const") ||
                base.getType().equals("EXtype")) {
            //base.getType().equals("Pow")) {
            base.renew(map);
        } else { //if (base.getType().equals("Factor"))
           if (map.containsKey(base.getEle())) {
                base = map.get(base.getEle());
           }
        }
    }

    @Override
    public String getEle() {
        return base.getEle();
    }

    @Override
    public boolean isEquivalent(Factor factor) {
        Pow p = (Pow) factor;
        return this.exponent.compareTo(p.exponent) == 0;
    }

    @Override       //x^2 x^1 x^0 (x^2+1)^3-> 3*2x*(x^2+1)^2
    public Factor differentiate() {
        if (base.getType().equals("Const")) {
            return null;
        }
        if (exponent.compareTo(BigInteger.valueOf(0)) == 0) {
            return null;
        } else if (exponent.compareTo(BigInteger.valueOf(1)) == 0) {
            return base.differentiate();
        } else {
            Term term = new Term();
            BigInteger newExponent = this.exponent;     //3
            term.addFactor(new Const(newExponent), true);
            Pow pow = new Pow(this.base, this.exponent.add(BigInteger.valueOf(-1)));
            if (base.getType().equals("Factor")) {      //d(x^3) 3 * x^2
                term.addFactor(pow, this.getNegative());
            } else { //dx((x^2+1)^3)-> 3*2x*(x^2+1)^2
                term.addFactor(base.differentiate(), base.getNegative());
                term.addFactor(pow, this.getNegative());
            }
            Expr expr = new Expr();
            expr.addTerm(term, this.getNegative());
            return expr;
        }
    }
}
