import java.math.BigInteger;
import java.util.HashMap;

public class Output {
    public String getOutPut(HashMap<Factor, HashMap<BigInteger, BigInteger>> res) {
        String result = "";
        for (Factor factor : res.keySet()) {
            HashMap<BigInteger, BigInteger> fun = res.get(factor);
            if (factor.getType().equals("Const")) {
                for (BigInteger i : fun.keySet()) {    //sec*x^fir i是指数 get是系数
                    result = getMonomial(result, fun, i);
                }
            } else {
                for (BigInteger i : fun.keySet()) {    //sec*x^fir   fun.get(i) 系数     i 指数
                    HashMap<Factor, HashMap<BigInteger, BigInteger>> fac = factor.getExpression();
                    String inEX = getOutPut(fac);
                    if (inEX.equals("0")) {
                        result = getMonomial(result, fun, i);
                    } else {
                        result = getExpExpression(result, inEX, fun, i);
                    }
                }
            }

        }
        if (result.isEmpty()) {
            result += "0";
        }
        return shorten(result.length() == 1 ? result : result.substring(0, result.length() - 1));
    }

    private String getMonomial(String res, HashMap<BigInteger, BigInteger> fun, BigInteger i) {
        String result = res;
        if (fun.get(i).compareTo(BigInteger.valueOf(0)) == 0) {
            return result;
        } else if (fun.get(i).compareTo(BigInteger.valueOf(1)) == 0) {
            if (i.compareTo(BigInteger.valueOf(0)) == 0) {
                result += "1";
            } else if (i.compareTo(BigInteger.valueOf(1)) == 0) {
                result += "x";
            } else {
                result += "x^" + i;
            }
        } else if (fun.get(i).compareTo(BigInteger.valueOf(-1)) == 0) {
            if (i.compareTo(BigInteger.valueOf(0)) == 0) {
                result += "-1";
            } else if (i.compareTo(BigInteger.valueOf(1)) == 0) {
                result += "-x";
            } else {
                result += "-x^" + i;
            }
        } else {
            if (i.compareTo(BigInteger.valueOf(0)) == 0) {
                result += fun.get(i);
            } else if (i.compareTo(BigInteger.valueOf(1)) == 0) {
                result += fun.get(i) + "*x";
            } else {
                result += fun.get(i) + "*x^" + i;
            }
        }
        return result + "+";
    }

    private String getExpExpression(String res, String inEX, HashMap<BigInteger,
            BigInteger> fun, BigInteger i) {
        String result = res;
        Lexer lexer = new Lexer(inEX);
        Parser parser = new Parser(lexer, null);
        Expr expr = parser.parserExpr();
        String appendix;
        if (expr.getTerms().size() == 1 &&
                expr.getTerms().get(0).getFactors().size() == 1 &&
                expr.getTerms().get(0).getNegative() &&
                !expr.getTerms().get(0).getFactors().get(0).getType().equals("Expr")) {
            appendix = "exp(" + inEX + ")";
        } else {
            appendix = "exp((" + inEX + "))";
        }
        if (fun.get(i).compareTo(BigInteger.valueOf(1)) == 0) {
            if (i.compareTo(BigInteger.valueOf(0)) == 0) {
                result += appendix + "+";
            } else if (i.compareTo(BigInteger.valueOf(1)) == 0) {
                result += appendix + "*x+";
            } else {
                result += appendix + "*x^" + i + "+";
            }
        } else if (fun.get(i).compareTo(BigInteger.valueOf(-1)) == 0) {
            if (i.compareTo(BigInteger.valueOf(0)) == 0) {
                result += "-" + appendix + "+";
            } else if (i.compareTo(BigInteger.valueOf(1)) == 0) {
                result += "-" + appendix + "*x+";
            } else {
                result += "-" + appendix + "*x^" + i + " + ";
            }
        } else if (fun.get(i).compareTo(BigInteger.valueOf(0)) == 0) {
            result = result;
        } else {
            if (i.compareTo(BigInteger.valueOf(0)) == 0) {
                result += fun.get(i) + "*" + appendix + "+";
            } else if (i.compareTo(BigInteger.valueOf(1)) == 0) {
                result += fun.get(i) + "*" + appendix + "*x+";
            } else {
                result += fun.get(i) + "*" + appendix + "*x^" + i + "+";
            }
        }
        return result;
    }

    public String shorten(String s) {
        String str = s;
        String end = "";
        for (int j = 0; j < 2; j++) {
            int len = str.length();
            end = "";
            for (int i = 0; i < len - 1; i++) {
                if (str.charAt(i) == '+' && str.charAt(i + 1) == '-') {
                    end += "-";
                    i++;
                } else if (str.charAt(i) == '-' && str.charAt(i + 1) == '+') {
                    end += "-";
                    i++;
                } else if (str.charAt(i) == '-' && str.charAt(i + 1) == '-') {
                    end += "+";
                    i++;
                } else if (str.charAt(i) == '+' && str.charAt(i + 1) == '+') {
                    end += "+";
                    i++;
                } else {
                    end += str.charAt(i);
                }
            }
            if (str.charAt(len - 1) == '+' || str.charAt(len - 1) == '-') {
                end = end;
            } else {
                end = end + str.charAt(len - 1);
            }
            str = end;
        }
        return end;
    }

    public String Consolidate(String s) {
        Lexer lexer = new Lexer(shorten(s.replace(" ","")));
        Parser parser = new Parser(lexer, null);
        Expr expr = parser.parserExpr();
        Expr ans = expr.mergeTerm();
        String res = this.getOutPut(ans.getExpression());
        res = res.replace("\t","").replace(" ","");
        return shorten(res);
    }
}
