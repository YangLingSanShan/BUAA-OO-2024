import com.oocourse.library2.LibraryBookId;
import com.oocourse.library2.LibraryRequest;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Objects;

public class AppointmentOffice implements Server {

    private TreeMap<LocalDate, HashMap<String, HashMap<LibraryBookId, Integer>>>
            waitingBooks = new TreeMap<>();

    @Override
    public void addBookRequest(LibraryRequest request) {
        return;
    }

    @Override
    public void addBook(LibraryBookId book) {
        return;
    }

    @Override
    public void delBook(LibraryBookId book) {

    }

    public boolean delPersonBook(LibraryBookId book, String personID) {
        for (HashMap<String, HashMap<LibraryBookId, Integer>> map : waitingBooks.values()) {
            if (map.containsKey(personID)) {
                HashMap<LibraryBookId, Integer> books = map.get(personID);
                for (LibraryBookId id : books.keySet()) {
                    if (id.getType() == book.getType() &&
                            Objects.equals(id.getUid(), book.getUid())) {
                        if (books.get(id) > 0) {
                            books.replace(id, books.get(id) - 1);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasBook(LibraryBookId book) {
        for (HashMap<String, HashMap<LibraryBookId, Integer>> map : waitingBooks.values()) {
            for (HashMap<LibraryBookId, Integer> books : map.values()) {
                if (books.containsKey(book)) {
                    if (books.get(book) > 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean hasBook(LibraryBookId book, String student) {
        for (HashMap<String, HashMap<LibraryBookId, Integer>> map : waitingBooks.values()) {
            if (map.containsKey(student)) {
                HashMap<LibraryBookId, Integer> books = map.get(student);
                if (books.containsKey(book) && books.get(book) > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public HashMap<LibraryBookId, Integer> getBooks(LocalDate date) {
        return null;//waitingBooks.getOrDefault(date, null);
    }

    public void addTodayOrder(LocalDate date, ArrayList<LibraryRequest> infos) {
        if (!waitingBooks.containsKey(date)) {
            waitingBooks.put(date, new HashMap<>());
        }
        HashMap<String, HashMap<LibraryBookId, Integer>> today = waitingBooks.get(date);
        for (LibraryRequest request : infos) {
            LibraryBookId book = request.getBookId();
            String personID = request.getStudentId();
            if (!today.containsKey(personID)) {
                today.put(personID, new HashMap<>());
            }
            HashMap<LibraryBookId, Integer> orders = today.get(personID);
            if (orders.containsKey(book)) {
                orders.replace(book, orders.get(book) + 1);
            } else {
                orders.put(book, 1);
            }
        }
    }

    public HashMap<LibraryBookId, Integer> getCleanBooks(LocalDate cleanDate) {
        HashMap<LibraryBookId, Integer> result = new HashMap<>();

        Iterator<Map.Entry<LocalDate, HashMap<String, HashMap<LibraryBookId, Integer>>>>
                iterator = waitingBooks.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<LocalDate, HashMap<String, HashMap<LibraryBookId, Integer>>> entry
                    = iterator.next();
            LocalDate date = entry.getKey();
            if (date.isBefore(cleanDate)) {
                HashMap<String, HashMap<LibraryBookId, Integer>> map = entry.getValue();
                for (HashMap<LibraryBookId, Integer> books : map.values()) {
                    for (LibraryBookId libraryBookId : books.keySet()) {
                        if (books.get(libraryBookId) == 0) {
                            continue;
                        }
                        if (result.containsKey(libraryBookId)) {
                            result.replace(libraryBookId,
                                    result.get(libraryBookId) + books.get(libraryBookId));
                        } else {
                            result.put(libraryBookId, books.get(libraryBookId));
                        }
                    }
                }
                iterator.remove();
            }
        }
        return result;
    }
}
