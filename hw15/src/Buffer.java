import com.oocourse.library3.LibraryBookId;
import com.oocourse.library3.LibraryRequest;

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

    public boolean personhasBookB(String studentId) {
        for (LibraryRequest request: successOrderOfToday) {
            if (request.getStudentId().equals(studentId) && request.getBookId().isTypeB()) {
                return true;
            }
        }
        for (LibraryRequest request: failedButAcceptedOrder) {
            if (request.getStudentId().equals(studentId) && request.getBookId().isTypeB()) {
                return true;
            }
        }
        return false;
    }

    public boolean personhasBookC(String studentId, LibraryBookId bookId) {
        for (LibraryRequest request: successOrderOfToday) {
            if (request.getStudentId().equals(studentId)
                    && request.getBookId().isTypeC()
                        && request.getBookId().getUid().equals(bookId.getUid())) {
                return true;
            }
        }
        for (LibraryRequest request: failedButAcceptedOrder) {
            if (request.getStudentId().equals(studentId)
                    && request.getBookId().isTypeC()
                    && request.getBookId().getUid().equals(bookId.getUid())) {
                return true;
            }
        }
        return false;
    }
}
