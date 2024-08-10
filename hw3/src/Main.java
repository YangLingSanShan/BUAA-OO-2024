import java.math.BigInteger;
import java.util.HashMap;
import java.util.TreeMap;

public class Main {
    public static void main(String[] args) {
        Input input = new Input();
        Output output = new Output();
        HashMap<String, String> functions = input.getFunction();
        Parser parser = new Parser(new Lexer(input.getExpression()), functions);
        Expr rawResult = parser.parserExpr();
        HashMap<Factor, TreeMap<BigInteger, BigInteger>> res = rawResult.getExpression();
        String result = output.Consolidate(output.getOutPut(res));
        System.out.println(result);
    }
}

