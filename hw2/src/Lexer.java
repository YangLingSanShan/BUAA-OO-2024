import java.math.BigInteger;

public class Lexer {
    private String pre;
    private int length;
    private int position;

    public Lexer(String pre) {
        this.pre = pre;
        this.position = 0;
        this.length = pre.length();
    }

    public void forward() {
        if (position < pre.length()) {
            position++;
        }
    }

    public void backward() {
        if (position > 0) {
            position--;
        }
    }

    public BigInteger getConst() {
        StringBuilder constNumber = new StringBuilder();
        while (pre.charAt(position) == '+') {
            forward();
        }
        while (position < pre.length() && Character.isDigit(pre.charAt(position))) {
            constNumber.append(pre.charAt(position));
            forward();
        }
        //前导0需要考虑
        int notZeroPosition = 0;
        boolean flag = false;
        String num = "";
        for (int i = 0; i < constNumber.length(); i++) {
            if (constNumber.charAt(i) != '0') {
                notZeroPosition = i;
                flag = true;
                break;
            }
        }
        if (flag) {
            num = constNumber.substring(notZeroPosition);
        } else {
            num = "0";
        }
        return new BigInteger(num);
    }

    public char getPeek() {
        if (position < length) {
            return pre.charAt(position);
        } else {
            return '\0';
        }
    }

    public int getPosition() {
        return this.position;
    }

    public int getLength() {
        return this.length;
    }
}
