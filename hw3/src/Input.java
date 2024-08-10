import java.util.HashMap;
import java.util.Scanner;

public class Input {
    private Scanner scanner;
    private int num;

    public Input() {
        scanner = new Scanner(System.in);
        num = Integer.parseInt(scanner.nextLine().replace(" ", "").replace("\t", ""));
    }

    public HashMap<String, String> getFunction() {
        HashMap<String, String> functions = new HashMap<>();
        for (int i = 0; i < num; i++) {
            String expression = scanner.nextLine().replace(" ", "").replace("\t", "");
            String[] split = expression.split("=");
            functions.put(split[0], shorten(split[1]));
        }
        return functions;
    }

    public String getExpression() {
        String expression = scanner.nextLine();
        expression = expression.replace(" ", "").replace("\t", "");
        return shorten(expression);
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

}
