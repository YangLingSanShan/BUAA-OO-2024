import com.oocourse.library2.LibraryCloseCmd;
import com.oocourse.library2.LibraryCommand;
import com.oocourse.library2.LibraryBookId;
import com.oocourse.library2.LibraryOpenCmd;
import java.time.LocalDate;
import java.util.HashMap;

import static com.oocourse.library2.LibrarySystem.SCANNER;

public class Main {
    public static void main(String[] args) {
        Library library = new Library();
        library.initLibrary((HashMap<LibraryBookId, Integer>) SCANNER.getInventory());
        while (true) {
            LibraryCommand command = SCANNER.nextCommand();
            if (command == null) {
                break;
            }
            LocalDate today = command.getDate(); // 今天的日期
            if (command instanceof LibraryOpenCmd) {
                // 在开馆时做点什么
                library.openLibrary(today);
            } else if (command instanceof LibraryCloseCmd) {
                // 在闭馆时做点什么
                library.closeLibrary(today);
            } else {
                // 对指令进行处理
                library.dealLibraryRequest(command, today);
            }

        }
    }

}
