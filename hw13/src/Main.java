import com.oocourse.library1.LibraryBookId;
import com.oocourse.library1.LibraryCommand;
import com.oocourse.library1.LibraryRequest;

import java.time.LocalDate;
import java.util.HashMap;

import static com.oocourse.library1.LibrarySystem.SCANNER;

public class Main {
    public static void main(String[] args) {
        Library library = new Library();
        library.initLibrary((HashMap<LibraryBookId, Integer>) SCANNER.getInventory());
        while (true) {
            LibraryCommand<?> command = SCANNER.nextCommand();
            if (command == null) { break; }
            LocalDate date = command.getDate();
            if (command.getCmd().equals("OPEN")) {
                // 在图书馆开门之前干点什么
                library.openLibrary(date);
            } else if (command.getCmd().equals("CLOSE")) {
                // 在图书馆关门之后干点什么
                library.closeLibrary(date);
            } else {
                LibraryRequest request = (LibraryRequest) command.getCmd();
                // 对 request 干点什么
                library.dealLibraryRequest(request, date);
            }
        }
    }
}
