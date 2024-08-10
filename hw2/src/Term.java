import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

public class Term {
    private ArrayList<Factor> factors;
    private boolean negative;

    public Term() {
        factors = new ArrayList<>();
    }

    public void setNegative(boolean negative) {
        this.negative = negative;
    }

    public void addFactor(Factor factor, boolean negative) {
        factor.setNegative(negative);
        factors.add(factor);
    }

    public boolean getNegative() {
        return this.negative;
    }

    public HashMap<Factor, HashMap<BigInteger, BigInteger>> getExpression() {
        BigInteger base = new BigInteger(negative ? "1" : "-1");
        HashMap<Factor, HashMap<BigInteger, BigInteger>> res = (new Const(base)).getExpression();
        Calculator calculator = new Calculator();
        for (Factor f : factors) {
            res = calculator.Mul(res, f.getExpression());
            if (!f.getNegative()) {
                BigInteger temp = new BigInteger("-1");
                res = calculator.Mul((new Const(temp)).getExpression(), res);
            }
        }
        return res;
    }

    public ArrayList<Factor> getFactors() {
        return this.factors;
    }

    public void renew(HashMap<String, Factor> map) {
        for (Factor factor : factors) {
            factor.renew(map);
        }
    }

    public boolean isEquivalent(Term termNew) {
        if (this.factors.size() != termNew.factors.size()) {
            return false;
        }
        for (int i = 0; i < this.factors.size(); i++) {
            if (!this.factors.get(i).getType().equals(termNew.factors.get(i).getType())) {
                return false;
            }
            // 相同类型
            if (!this.factors.get(i).isEquivalent(termNew.factors.get(i))) {
                return false;
            }
        }
        return true;
    }

    public void merge(Term term) {
        if (this.negative == term.negative) {
            Const c = (Const) term.factors.get(0);
            this.factors.get(0).add(c, true);
        } else {
            Const ofThis = (Const) this.factors.get(0);
            Const ofnew = (Const) term.factors.get(0);
            if (ofThis.getNumber().compareTo(ofnew.getNumber()) >= 0) {
                this.factors.get(0).add(ofnew, false);
            } else {
                this.factors.get(0).add(ofnew, false);
                this.negative ^= true;
            }
        }
    }
}
