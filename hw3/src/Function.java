import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class Function extends Factor {
    private String[] type;
    private String init;
    private HashMap<String, Factor> map;
    private Expr expr;
    private HashMap<String, String> functions;

    public Function(ArrayList<String> s, ArrayList<Factor> f, HashMap<String, String> funs) {
        type = s.get(0).substring(2, s.get(0).length() - 1).split(","); //x
        init = s.get(1);
        map = new HashMap<>();
        for (int j = 0; j < type.length; j++) {
            map.put(type[j], f.get(j));
        }
        functions = funs;
        expr = getExpr();
    }

    @Override
    public HashMap<Factor, TreeMap<BigInteger, BigInteger>> getExpression() {
        return expr.getExpression();
    }

    @Override
    public String getType() {
        return "Function";
    }

    public Expr getExpr() {
        Parser parser = new Parser(new Lexer(init), functions);
        Expr expr = parser.parserExpr();
        expr.renew(map);
        return expr;
    }

    public Expr get() {
        return this.expr;
    }

}



