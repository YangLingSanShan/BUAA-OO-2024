import com.oocourse.elevator1.TimableOutput;

public class MainClass {
    public static void main(String[] args) {
        TimableOutput.initStartTimestamp();
        Distributor distributor = new Distributor(6, 11, 1);
        distributor.start();
    }
}
