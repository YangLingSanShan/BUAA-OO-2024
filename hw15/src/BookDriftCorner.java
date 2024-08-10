import com.oocourse.library3.LibraryBookId;

import java.util.ArrayList;
import java.util.HashMap;

public class BookDriftCorner {
    private HashMap<LibraryBookId, ArrayList<TempBook>> books = new HashMap<>();

    public void addBook(TempBook tempBook) {
        if (!books.containsKey(tempBook.getLibraryBookId())) {
            books.put(tempBook.getLibraryBookId(), new ArrayList<>());
        }
        ArrayList<TempBook> tempBooks = books.get(tempBook.getLibraryBookId());
        tempBooks.add(tempBook);
    }

    public int getBookSum(LibraryBookId book) {
        ArrayList<TempBook> targetBooks
                = books.getOrDefault(book, null);
        return targetBooks == null ? 0 : targetBooks.size();
    }

    public TempBook delBook(LibraryBookId book) {
        ArrayList<TempBook> tempBooks = books.get(book);
        if (tempBooks != null) {
            if (!tempBooks.isEmpty()) {
                TempBook tempBook = tempBooks.get(0);
                tempBooks.remove(0);
                return tempBook;
            }
        }
        return null;
    }

    public boolean hasBook(LibraryBookId book) {
        return getBookSum(book) != 0;
    }
}
