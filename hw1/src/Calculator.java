import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

public class Calculator {
    public String simplify(String str) {
        String[] elements = str.split("\\+");
        ArrayList<String> constant = new ArrayList<>();
        ArrayList<String> variable = new ArrayList<>();
        HashMap<String, String> vars = new HashMap<>();
        for (String s : elements) {
            if (!s.isEmpty()) {
                if (s.contains("x")) {
                    variable.add(s);
                } else {
                    constant.add(s);
                }
            }
        }
        BigInteger num = new BigInteger("0");
        for (String s : constant) {
            num = num.add(new BigInteger(s));//(BigInteger.valueOf(Long.parseLong(s)));
        } //x^2+x^2+x^3+2*2*2*x*x
        for (String s : variable) {
            String[] temp = getDivided(s);
            merge(temp, vars);
        }
        return getAddExpression(getoutput(vars).toString(), num.toString());
    }

    public String[] getDivided(String str) { //(x+3)^2      100 * x^2 * 10000 * x ^ 3
        BigInteger ex = new BigInteger("0");
        BigInteger parameter = new BigInteger("1");
        String[] set = str.split("\\*");
        for (String s : set) {
            if (s.contains("x")) {
                if (s.contains("^")) {
                    String[] splits = s.split("\\^");
                    ex = ex.add(new BigInteger(splits[1]));
                    //ex += Integer.parseInt(splits[1]);
                } else {
                    ex = ex.add(new BigInteger("1"));
                }
                if (s.contains("-")) {
                    //parameter *= -1;
                    parameter = parameter.multiply(new BigInteger("-1"));
                }
            } else {
                parameter = parameter.multiply(new BigInteger(s)); //*= Integer.parseInt(s);
            }
        }
        return new String[]{String.valueOf(ex), String.valueOf(parameter)};
        //返回指数,系数
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

    // (x + 1) * (x + 2 + 0)  <1,1>,<0,1>   *   <1,1>,<0,2>,<0,0> -> <2,1> <1,2> <1,1>,<0,2>
    //第一项相加，第二项相乘
    public String multiple(String fir, String sec) {
        String[] first = fir.split("\\+");
        String[] second = sec.split("\\+");
        String[] temp1;
        String[] temp2;
        HashMap<String, String> varsF = new HashMap<>();
        HashMap<String, String> varsS = new HashMap<>();
        HashMap<String, String> result = new HashMap<>();
        for (String f : first) {
            if (!f.isEmpty()) {
                temp1 = getDivided(f);
                merge(temp1, varsF);
            }
        }
        for (String s : second) {
            if (!s.isEmpty()) {
                temp2 = getDivided(s);
                merge(temp2, varsS);
            }
        }
        for (String f : varsF.keySet()) { //用f去乘以s
            temp1 = new String[]{f, varsF.get(f)};
            for (String s : varsS.keySet()) {
                temp2 = new String[]{s, varsS.get(s)};
                merge(pairMul(temp1, temp2), result);
            }
        }
        return getoutput(result).toString();
    }

    private StringBuilder getoutput(HashMap<String, String> result) {
        StringBuilder end = new StringBuilder();
        for (String r : result.keySet()) {
            if (!r.equals("0") && !r.equals("1")) {
                if (result.get(r).equals("0")) {
                    continue;
                } else if (result.get(r).equals("1")) {
                    end.append("x^").append(r).append("+");
                } else if (result.get(r).equals("-1")) {
                    end.append("-x^").append(r).append("+");
                } else {
                    end.append(result.get(r)).append("*x^").append(r).append("+");
                }
            } else if (r.equals("1")) {
                if (result.get(r).equals("0")) {
                    continue;
                } else if (result.get(r).equals("1")) {
                    end.append("x").append("+");
                } else if (result.get(r).equals("-1")) {
                    end.append("-x").append("+");
                } else {
                    end.append(result.get(r)).append("*x").append("+");
                }
            } else {
                if (!result.get(r).equals("0")) {
                    end.append(result.get(r)).append("+");
                }
            }
        }
        return end.length() == 0 ? new StringBuilder("0") : end;
    }

    private void merge(String[] temp, HashMap<String, String> vars) {
        if (vars.containsKey(temp[0])) {
            BigInteger big = new BigInteger(vars.get(temp[0]));
            big = big.add(new BigInteger(temp[1]));//(BigInteger.valueOf(Long.parseLong(temp[1])));
            vars.replace(temp[0], String.valueOf(big));
        } else {
            vars.put(temp[0], temp[1]);
        }
    }

    private String[] pairMul(String[] a, String[] b) {
        //String expo = String.valueOf(Integer.parseInt(a[0]) + Integer.parseInt(b[0]));
        BigInteger paraA = new BigInteger(a[1]);
        BigInteger paraB = new BigInteger(b[1]);
        BigInteger expoA = new BigInteger(a[0]);
        BigInteger expoB = new BigInteger(b[0]);

        String expo = String.valueOf(expoA.add(expoB));
        String para = String.valueOf(paraA.multiply(paraB));
        return new String[]{expo, para};
    }

    private String getAddExpression(String variations, String constants) {
        if (variations.equals("0") && constants.equals("0")) {
            return "0";
        } else if (variations.equals("0") && !constants.equals("0")) {
            return constants;
        } else if (!variations.equals("0") && constants.equals("0")) {
            return variations;
        } else {
            return variations + constants;
        }
    }
}

//(x+2) * (2*x+3)