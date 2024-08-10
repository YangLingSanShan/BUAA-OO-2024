import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

public class Parser {
    private Lexer lexer;
    private HashMap<String, String> functions;

    public Parser(Lexer lexer, HashMap<String, String> functions) {
        this.lexer = lexer;
        this.functions = functions;
    }

    public Expr parserExpr() {
        Expr expression = new Expr();
        if (lexer.getPeek() == '-' || lexer.getPeek() == '+') {
            boolean sign = (lexer.getPeek() == '+');
            lexer.forward();
            expression.addTerm(parserTerm(), sign);
        } else {
            expression.addTerm(parserTerm(), true);
        }
        while (lexer.getPeek() == '+' || lexer.getPeek() == '-') {
            boolean sign = (lexer.getPeek() == '+');
            lexer.forward();
            expression.addTerm(parserTerm(), sign);
        }
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
            return getExpressionFactor();
        } else if (Character.isDigit(lexer.getPeek())) {
            return getConstFactor(true);
        } else if (lexer.getPeek() == '+' || lexer.getPeek() == '-') {
            boolean sign = lexer.getPeek() == '+';
            lexer.forward();
            if (Character.isDigit(lexer.getPeek())) {
                return getConstFactor(sign);
            } else {
                return getPowFactor(sign);
            }
        } else if (lexer.getPeek() == 'x' || lexer.getPeek() == 'y' ||
                lexer.getPeek() == 'z') {
            return getPowFactor(true);
        } else if (lexer.getPeek() == 'e') {
            return getEXtypeFactor();
        } else if (lexer.getPeek() == 'f' || lexer.getPeek() == 'g'
                || lexer.getPeek() == 'h') {
            return getFunctionFactor();
        } else if (lexer.getPeek() == 'd') {
            return getDifferentiation();
        } else {
            lexer.forward();
        }
        return null;
    }

    private EXtype getEXtypeFactor() {
        lexer.forward();
        lexer.forward();
        lexer.forward();
        Factor f = getExpressionFactor();
        if (f.getType().equals("Expr")) {
            return new EXtype(new Pow(f, new BigInteger("1")));
        } else {
            return new EXtype((Pow) f);
        }
    }

    public Const getConstFactor(boolean negative) {
        Const con = new Const(lexer.getConst());
        con.setNegative(negative);
        return con;
    }

    public Pow getPowFactor(boolean sign) {
        Factor factor = new Factor();
        factor.setEle(String.valueOf(lexer.getPeek()));
        factor.setNegative(true);
        Pow pow;
        lexer.forward();
        if (lexer.getPeek() != '^') {
            pow = new Pow(factor, new BigInteger("1"));
            pow.setNegative(sign);
        } else {
            lexer.forward();
            pow = new Pow(factor, lexer.getConst());
            pow.setNegative(sign);
        }
        return pow;
    }

    public Factor getExpressionFactor() {
        lexer.forward();
        Expr expr = parserExpr();
        if (lexer.getPeek() == ')') {
            lexer.forward();
        }
        if (lexer.getPeek() == '^') {   //是幂函数 ： 表达式^常数(?)
            lexer.forward();
            return new Pow(expr, lexer.getConst());
        } else {
            return expr;
        }
    }

    public Expr getFunctionFactor() {
        ArrayList<Factor> factors = new ArrayList<>();
        ArrayList<String> s = new ArrayList<>();
        for (String key : functions.keySet()) {
            if (key.contains(String.valueOf(lexer.getPeek()))) {
                s.add(key);
                s.add(functions.get(key));
                break;
            }
        }
        lexer.forward();
        lexer.forward();
        while (lexer.getPeek() != ',') {
            factors.add(parserFactor());
            if (lexer.getPeek() == ',') {
                lexer.forward();
            } else {
                if (lexer.getPeek() == ')') {
                    lexer.forward();
                }
                break;
            }
        }
        Function function = new Function(s, factors, functions);
        return function.get();
    }

    public Factor getDifferentiation() {
        lexer.forward();
        lexer.forward();
        Factor f = parserFactor();
        return f.differentiate();
    }

}
