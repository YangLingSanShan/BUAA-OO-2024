import com.oocourse.library1.LibraryBookId;
import com.oocourse.library1.LibraryRequest;

import java.time.LocalDate;
import java.util.HashMap;

public interface Server {
    public void addBookRequest(LibraryRequest request);

    public void addBook(LibraryBookId book);

    public void delBook(LibraryBookId book);

    public boolean hasBook(LibraryBookId book);

    public HashMap<LibraryBookId, Integer> getBooks(LocalDate date);
}
