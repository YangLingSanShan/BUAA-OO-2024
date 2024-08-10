import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;

//<Factor,<BigInteger, >>  <ex,<指数，系数>>
public class Calculator {
    public HashMap<Factor, HashMap<BigInteger, BigInteger>> Mul(HashMap<Factor, HashMap<BigInteger,
            BigInteger>> a, HashMap<Factor, HashMap<BigInteger, BigInteger>> b) {
        HashMap<Factor, HashMap<BigInteger, BigInteger>> res = new HashMap<>();
        for (Factor fa : a.keySet()) {
            for (Factor fb : b.keySet()) {
                Factor newKey = FactorAddFactor(fa, fb);
                HashMap<BigInteger, BigInteger> tree = TreeMulTree(a.get(fa), b.get(fb));
                res.put(newKey, tree);
            }
        }
        this.del(res);
        return res;
    }

    public HashMap<Factor, HashMap<BigInteger, BigInteger>> Add(HashMap<Factor, HashMap<BigInteger,
            BigInteger>> a, HashMap<Factor, HashMap<BigInteger, BigInteger>> b) {
        HashMap<Factor, HashMap<BigInteger, BigInteger>> res = new HashMap<>();
        boolean flag = false;
        Factor temp = null;
        for (Factor factor : a.keySet()) {
            if (factor.getType().equals("Const")) {
                flag = true;
                temp = factor;
            }
        }
        for (Factor fa : a.keySet()) {
            res.put(fa, a.get(fa));
        }
        for (Factor fb : b.keySet()) {
            if (fb.getType().equals("Const") && flag) {
                res.replace(temp, TreeAddTree(res.get(temp), b.get(fb)));
            } else {
                res.put(fb, b.get(fb));
            }
        }
        this.del(res);
        return res;
    }

    public Factor FactorAddFactor(Factor a, Factor b) {
        if (a.getType().equals("Const") && b.getType().equals("Const")) {
            return FactorAddFactor((Const) a, (Const) b);
        } else if (a.getType().equals("Const") && b.getType().equals("Expr")) {
            Expr expr = (Expr) b;
            return new Expr(expr.getTerms());
        } else if (a.getType().equals("Expr") && b.getType().equals("Const")) {
            Expr expr = (Expr) a;
            return new Expr(expr.getTerms());
        } else if (a.getType().equals("Expr") && b.getType().equals("Expr")) {
            return FactorAddFactor((Expr) a, (Expr) b);
        } else {
            return null;
        }
    }

    public Expr FactorAddFactor(Expr a, Expr b) {
        Expr expr = new Expr();
        for (Term term : a.getTerms()) {
            expr.addTerm(term, term.getNegative());
        }
        for (Term term : b.getTerms()) {
            expr.addTerm(term, term.getNegative());
        }
        return expr;
    }

    public Const FactorAddFactor(Const a, Const b) {
        return new Const(a.getNumber().add(b.getNumber()));
    }

    public HashMap<BigInteger, BigInteger> TreeMulTree(HashMap<BigInteger, BigInteger> a,
                                                       HashMap<BigInteger, BigInteger> b) {
        HashMap<BigInteger, BigInteger> res = new HashMap<>();
        for (BigInteger i : a.keySet()) {
            for (BigInteger j : b.keySet()) {
                BigInteger exponent = i.add(j);
                BigInteger argument = a.get(i).multiply(b.get(j));
                if (res.containsKey(exponent)) {
                    argument = argument.add(res.get(exponent));
                    res.replace(exponent, argument);
                } else {
                    res.put(exponent, argument);
                }
            }
        }
        return res;
    }

    public HashMap<BigInteger, BigInteger> TreeAddTree(HashMap<BigInteger, BigInteger> a,
                                                       HashMap<BigInteger, BigInteger> b) {
        HashMap<BigInteger, BigInteger> res = new HashMap<>();
        for (BigInteger i : a.keySet()) {
            res.put(i, a.get(i));
        }
        for (BigInteger j : b.keySet()) {
            if (res.containsKey(j)) {
                BigInteger temp = res.get(j).add(b.get(j));
                res.replace(j, temp);
            } else {
                res.put(j, b.get(j));
            }
        }
        return res;
    }

    public void del(HashMap<Factor, HashMap<BigInteger, BigInteger>> tree) {
        for (Factor factor : tree.keySet()) {
            HashMap<BigInteger, BigInteger> temp = tree.get(factor);
            Iterator<BigInteger> iterator = temp.keySet().iterator();
            while (iterator.hasNext()) {
                if (temp.get(iterator.next()).compareTo(BigInteger.valueOf(0)) == 0) {
                    iterator.remove();
                }
            }
        }
    }
}