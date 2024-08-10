import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Calculator calculator = new Calculator();
        Scanner scanner = new Scanner(System.in);
        String expression = scanner.nextLine().replace(" ", "").replace("\t", "");
        expression = calculator.shorten(expression);
        Parser parser = new Parser(new Lexer(expression));
        Expr rawResult = parser.parserExpr();
        String ans = calculator.simplify(rawResult.getExpression());
        System.out.println(calculator.shorten(ans));
    }
}
