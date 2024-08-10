import java.math.BigInteger;

public class Parser {
    private Lexer lexer;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public Expr parserExpr() {
        Expr expression = new Expr();
        Term term;
        if (lexer.getPeek() == '-' || lexer.getPeek() == '+') {
            boolean sign = (lexer.getPeek() == '+');
            lexer.forward();
            expression.addTerm(parserTerm(), sign);
        } else {
            expression.addTerm(parserTerm(), true);
        }
        //lexer.forward();
        while (lexer.getPeek() == '+' || lexer.getPeek() == '-') {
            boolean sign = (lexer.getPeek() == '+');
            lexer.forward();
            expression.addTerm(parserTerm(), sign);
            //lexer.forward();
        }
        //lexer.backward();
        return expression;
    }

    public Term parserTerm() {
        Term term = new Term();
        if (lexer.getPeek() == '-' || lexer.getPeek() == '+') {
            boolean sign = (lexer.getPeek() == '+');
            lexer.forward();
            term.addFactor(parserFactor(), sign);
        } else {
            term.addFactor(parserFactor(), true);
        }
        //lexer.forward();
        while (lexer.getPeek() == '*') {
            lexer.forward();
            if (lexer.getPeek() == '+' || lexer.getPeek() == '-') {
                boolean sign = (lexer.getPeek() == '+');
                lexer.forward();
                term.addFactor(parserFactor(), sign);
            } else {
                term.addFactor(parserFactor(), true);
            }
        }
        return term;
    }

    public Factor parserFactor() {
        if (lexer.getPeek() == '(') {
            lexer.forward();
            Expr expr = parserExpr();
            lexer.forward();
            while (lexer.getPeek() == ')') {
                lexer.forward();
            }
            if (lexer.getPeek() == '^') {   //是幂函数 ： 表达式^常数(?)
                lexer.forward();
                return new Pow(expr.getExpression(), lexer.getConst());
            } else {
                return new Pow(expr.getExpression(), new BigInteger("1")); //')'没考虑怎么处理
            }
        } else if (Character.isDigit(lexer.getPeek())) {
            Const con = new Const(lexer.getConst());
            con.setNegative(true);
            return con;
        } else if (lexer.getPeek() == '+' || lexer.getPeek() == '-') {
            boolean sign = lexer.getPeek() == '+';
            lexer.forward();
            Const con = new Const(lexer.getConst());
            con.setNegative(sign);
            return con;
        } else if (lexer.getPeek() == 'x') {
            lexer.forward();
            Pow pow;
            if (lexer.getPeek() != '^') {
                pow = new Pow("x", new BigInteger("1"));
            } else {
                lexer.forward();
                pow = new Pow("x", lexer.getConst());
            }
            return pow;
        } else {
            lexer.forward();
        }
        return null;
    }
}
