import com.oocourse.library2.LibraryBookId;
import com.oocourse.library2.LibraryRequest;

import java.util.ArrayList;

public class Buffer {

    private ArrayList<LibraryRequest> successOrderOfToday = new ArrayList<>();
    private ArrayList<LibraryRequest> failedButAcceptedOrder = new ArrayList<>();

    public ArrayList<LibraryRequest> getTodayOrder() {
        return successOrderOfToday;
    }

    public void addOrder(LibraryRequest request) {
        successOrderOfToday.add(request);
    }

    public boolean hasBook(LibraryBookId book) {
        for (LibraryRequest libraryRequest: successOrderOfToday) {
            if (libraryRequest.getBookId().equals(book)) {
                return true;
            }
        }

        for (LibraryRequest libraryRequest: failedButAcceptedOrder) {
            if (libraryRequest.getBookId().equals(book)) {
                return true;
            }
        }
        return false;
    }

    public void addFailRequest(LibraryRequest request) {
        failedButAcceptedOrder.add(request);
    }

}
