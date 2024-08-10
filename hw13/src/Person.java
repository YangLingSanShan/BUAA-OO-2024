import com.oocourse.library1.LibraryBookId;

import java.util.HashSet;

public class Person {

    private String id;
    private HashSet<LibraryBookId> books = new HashSet<>();

    public Person(String id) {
        this.id = id;
    }

    public boolean hasBook(LibraryBookId book) {
        return books.contains(book);
    }

    public void borrowBook(LibraryBookId book) {
        books.add(book);
    }

    public void returnBook(LibraryBookId book) {
        assert (hasBook(book));
        books.remove(book);
    }

    public boolean hasBtype() {
        for (LibraryBookId bookId: books) {
            if (bookId.isTypeB()) {
                return true;
            }
        }
        return false;
    }

    public String getId() {
        return this.id;
    }
}
