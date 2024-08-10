import java.util.ArrayList;

public class Expr extends Factor {
    private ArrayList<Term> terms;

    public Expr() {
        this.terms = new ArrayList<>();
    }

    public void addTerm(Term term, boolean negative) {
        term.setNegative(negative);
        terms.add(term);
    }

    @Override
    public String getExpression() {
        StringBuilder s = new StringBuilder();
        for (Term term : this.terms) {
            s.append(term.getExpression()).append("+");
        }
        s.deleteCharAt(s.length() - 1);
        return s.toString();
    }

}
