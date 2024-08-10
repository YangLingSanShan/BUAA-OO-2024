import com.oocourse.library2.LibraryBookId;
import com.oocourse.library2.LibraryRequest;

import java.time.LocalDate;
import java.util.HashMap;

public class Bookshelf implements Server {

    private HashMap<LibraryBookId, Integer> books = new HashMap<>();

    @Override
    public void addBookRequest(LibraryRequest request) {
        LibraryBookId book = request.getBookId();
        if (books.containsKey(book)) {
            books.replace(book, books.get(book) + 1);
        } else {
            books.put(book, 1);
        }
    }

    @Override
    public void addBook(LibraryBookId book) {
        if (books.containsKey(book)) {
            books.replace(book, books.get(book) + 1);
        } else {
            books.put(book, 1);
        }
    }

    @Override
    public void delBook(LibraryBookId book) {
        books.replace(book, books.get(book) - 1);
    }

    @Override
    public boolean hasBook(LibraryBookId book) {
        return books.containsKey(book) && books.get(book) > 0;
    }

    @Override
    public HashMap<LibraryBookId, Integer> getBooks(LocalDate date) {
        return books;
    }

    public int getBookSum(LibraryBookId book) {
        return books.getOrDefault(book, 0);
    }

    public void initShelf(HashMap<LibraryBookId, Integer> inventory) {
        this.books = inventory;
    }
}
