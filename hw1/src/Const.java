import java.math.BigInteger;

public class Const extends Factor {
    private BigInteger number;

    public Const(BigInteger number) {
        this.number = number;
    }

    @Override
    public String getExpression() {
        return number.toString();
    }
}
