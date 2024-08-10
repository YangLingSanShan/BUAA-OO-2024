import java.math.BigInteger;
import java.util.HashMap;
import java.util.TreeMap;

public class EXtype extends Factor {
    private Factor in;              //exp(in)^out
    private BigInteger out;

    public EXtype(Pow factor) {
        in = factor.getBase();
        out = factor.getExponent();
        this.setNegative(true);
    }

    @Override
    public HashMap<Factor, TreeMap<BigInteger, BigInteger>> getExpression() {
        HashMap<Factor, TreeMap<BigInteger, BigInteger>> res = new HashMap<>();
        TreeMap<BigInteger, BigInteger> second = new TreeMap<>();
        second.put(new BigInteger("0"), new BigInteger("1"));
        res.put(getExpr(), second);
        return res;
    }

    public Expr getExpr() {
        Term term = new Term();
        Expr expr = new Expr();
        if (out.compareTo(BigInteger.valueOf(1)) == 0) {
            term.addFactor(in, in.getNegative());
            expr.addTerm(term, this.getNegative());
        } else if (out.compareTo(BigInteger.valueOf(0)) == 0) {
            term.addFactor(new Const(out), true);
            expr.addTerm(term, this.getNegative());
        } else {
            term.addFactor(in, in.getNegative());
            term.addFactor(new Const(out), true);
            expr.addTerm(term, this.getNegative());
        }
        return expr;
    }

    @Override
    public String getType() {
        return "EXtype";
    }

    @Override
    public void renew(HashMap<String, Factor> map) {
        in.renew(map);
    }

    @Override
    public boolean isEquivalent(Factor factor) {
        Output output = new Output();
        HashMap<Factor, TreeMap<BigInteger, BigInteger>> res1 = this.getExpression();
        HashMap<Factor, TreeMap<BigInteger, BigInteger>> res2 = factor.getExpression();
        String ans1 = output.getOutPut(res1);
        String ans2 = output.getOutPut(res2);
        return ans1.equals(ans2);
    }

    @Override       //d(exp(x^2+2)^2) = d(exp(2*x^2+4)) =(4*x)*exp(2*x^2+4) = 2*2x*exp(x^2+2)^2
    public Factor differentiate() {
        if (out.compareTo(BigInteger.valueOf(0)) == 0) {
            return null;
        } else {
            Term term = new Term();
            term.addFactor(new Const(out), true);
            term.addFactor(in.differentiate(), in.getNegative());
            term.addFactor(this, this.getNegative());
            Expr expr = new Expr();
            expr.addTerm(term, this.getNegative());
            return expr;
        }
    }

    public Factor getIn() {
        return this.in;
    }

    public void setIn(Factor factor) {
        in = factor;
    }
}