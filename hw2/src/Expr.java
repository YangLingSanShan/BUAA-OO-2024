import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

public class Expr extends Factor {
    private ArrayList<Term> terms;

    public Expr() {
        this.terms = new ArrayList<>();
        this.setNegative(true);
    }

    public Expr(ArrayList<Term> terms) {
        this.terms = terms;
        this.setNegative(true);
    }

    public void addTerm(Term term, boolean negative) {
        term.setNegative(negative);
        terms.add(term);
    }

    @Override
    public HashMap<Factor, HashMap<BigInteger, BigInteger>> getExpression() {
        Calculator calculator = new Calculator();
        Const c = new Const(new BigInteger("0"));   //<0,<0,0>>
        HashMap<Factor, HashMap<BigInteger, BigInteger>> res = c.getExpression();
        for (Term term : this.terms) {
            res = calculator.Add(res, term.getExpression());
        }
        return res;
    }

    @Override
    public String getType() {
        return "Expr";
    }

    public ArrayList<Term> getTerms() {
        return terms;
    }

    public void renew(HashMap<String, Factor> map) {
        for (Term term : terms) {
            term.renew(map);
        }
    }

    public Expr mergeTerm() {
        ArrayList<Term> termsNew = new ArrayList<>();
        for (Term term : this.terms) {
            Factor factor = term.getFactors().get(0);
            if (!factor.getType().equals("Const")) {
                Const c = new Const(BigInteger.valueOf(1));
                term.getFactors().add(0, c);
            }
        }
        for (Term termThis : this.terms) {
            if (termsNew.isEmpty()) {
                termsNew.add(termThis);
            } else {
                boolean flag = true; //不一致
                for (Term termNew : termsNew) {
                    if (termThis.isEquivalent(termNew)) {
                        flag = false;
                        termNew.merge(termThis);    //吸收进来
                        break;
                    }
                }
                if (flag) {
                    termsNew.add(termThis);
                }
            }
        }
        return new Expr(termsNew);
    }
}