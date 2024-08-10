import com.oocourse.library2.LibraryBookId;
import com.oocourse.library2.LibraryRequest;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;

public class BorrowReturnOffice implements Server {

    private HashMap<LibraryBookId, Integer> failAndReturn = new HashMap<>();
    private HashSet<TempBook> tempBooks = new HashSet<>();

    @Override
    public void addBookRequest(LibraryRequest request) {
        LibraryBookId book = request.getBookId();
        if (failAndReturn.containsKey(book)) {
            failAndReturn.replace(book, failAndReturn.get(book) + 1);
        } else {
            failAndReturn.put(book, 1);
        }
    }

    public void addBookRequest(TempBook tempBook) {
        tempBooks.add(tempBook);
    }

    @Override
    public void addBook(LibraryBookId book) {
        if (failAndReturn.containsKey(book)) {
            failAndReturn.replace(book, failAndReturn.get(book) + 1);
        } else {
            failAndReturn.put(book, 1);
        }
    }

    @Override
    public void delBook(LibraryBookId book) {

    }

    @Override
    public boolean hasBook(LibraryBookId bookId) {
        return failAndReturn.containsKey(bookId);
    }

    @Override
    public HashMap<LibraryBookId, Integer> getBooks(LocalDate date) {
        return failAndReturn;
    }

    public HashSet<TempBook> getTempBooks() {
        return tempBooks;
    }
}
