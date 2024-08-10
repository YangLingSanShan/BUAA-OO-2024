import java.util.ArrayList;

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

    public String getExpression() {
        String expression = this.negative ? "1" : "-1";
        Calculator calculator = new Calculator();
        for (Factor factor : this.factors) {
            expression = calculator.multiple(expression, factor.getExpression());
            if (!factor.getNegative()) {
                expression = calculator.multiple(expression, "-1");
            }
            expression = calculator.simplify(expression);
        }
        //expression = calculator.shorten(expression);
        //s.deleteCharAt(s.length() - 1);
        //return s.toString();
        return expression;
    }
}
