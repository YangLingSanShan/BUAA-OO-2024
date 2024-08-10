import java.math.BigInteger;

public class Pow extends Factor {
    private String base;
    private int exponent;

    public Pow(String base, BigInteger exponent) {
        this.base = base;
        this.exponent = exponent.intValue();
    }

    @Override
    public String getExpression() {
        Calculator calculator = new Calculator();
        if (exponent == 0) {
            return "1";
        } else if (exponent == 1) {
            return this.getNegative() ? base : "-" + base;
        } else {
            String result = base;
            for (int i = exponent; i > 1; i--) {
                result = calculator.multiple(result, base);
            }
            result = calculator.simplify(result);
            return result;
        }
    }
}
