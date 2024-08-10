import com.oocourse.library3.LibraryBookId;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;

public class Person {

    private String id;
    private HashMap<LocalDate, HashSet<LibraryBookId>> books = new HashMap<>();
    private HashMap<LocalDate, HashSet<TempBook>> tempBooks = new HashMap<>();
    private int credit;
    private HashSet<LibraryBookId> overDueBooks = new HashSet<>();

    public Person(String id) {
        this.id = id;
        this.credit = 10;
    }

    public boolean hasBook(LibraryBookId book) {
        if (book.isTypeA() || book.isTypeB() || book.isTypeC()) {
            for (HashSet<LibraryBookId> bookIds : books.values()) {
                if (bookIds.contains(book)) {
                    return true;
                }
            }
        } else {
            for (HashSet<TempBook> bookIds : tempBooks.values()) {
                for (TempBook tempBook : bookIds) {
                    if (tempBook.getLibraryBookId().equals(book)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void borrowBook(LibraryBookId book, LocalDate dueDate) {
        if (!books.containsKey(dueDate)) {
            books.put(dueDate, new HashSet<>());
        }
        HashSet<LibraryBookId> bookIds = books.get(dueDate);
        bookIds.add(book);
    }

    public void borrowBook(TempBook book, LocalDate dueDate) {
        book.addTime();
        if (!tempBooks.containsKey(dueDate)) {
            tempBooks.put(dueDate, new HashSet<>());
        }
        HashSet<TempBook> bookIds = tempBooks.get(dueDate);
        bookIds.add(book);
    }

    public TempBook returnBook(LibraryBookId book) {
        // assert (hasBook(book));
        overDueBooks.remove(book);
        if (book.isTypeA() || book.isTypeB() || book.isTypeC()) {
            for (LocalDate date : books.keySet()) {
                HashSet<LibraryBookId> bookIds = books.get(date);
                if (bookIds.contains(book)) {
                    bookIds.remove(book);
                    return null;
                }
            }
        } else {
            for (LocalDate date : tempBooks.keySet()) {
                HashSet<TempBook> bookIds = tempBooks.get(date);
                for (TempBook tempBook : bookIds) {
                    if (tempBook.getLibraryBookId().equals(book)) {
                        bookIds.remove(tempBook);
                        return tempBook;
                    }
                }
            }
        }
        return null;
    }

    public boolean hasBtype() {
        for (HashSet<LibraryBookId> bookIds : books.values()) {
            for (LibraryBookId bookId : bookIds) {
                if (bookId.isTypeB()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasBUtype() {
        for (HashSet<TempBook> bookHashSet : tempBooks.values()) {
            for (TempBook bookId : bookHashSet) {
                if (bookId.isTypeBU()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isOverDue(LibraryBookId book, LocalDate today) {
        // assert (hasBook(book));
        //ture 是over
        if (book.isTypeA() || book.isTypeB() || book.isTypeC()) {
            for (LocalDate date : books.keySet()) {
                HashSet<LibraryBookId> bookIds = books.get(date);
                if (bookIds.contains(book)) {
                    return today.isAfter(date);
                }
            }
        } else {
            for (LocalDate date : tempBooks.keySet()) {
                HashSet<TempBook> bookIds = tempBooks.get(date);
                for (TempBook tempBook : bookIds) {
                    if (tempBook.getLibraryBookId().equals(book)) {
                        return today.isAfter(date);
                    }
                }
            }
        }
        assert (false);
        return false;
    }

    public boolean renewBook(LibraryBookId book, LocalDate today) {
        for (LocalDate date : books.keySet()) {
            for (LibraryBookId id : books.get(date)) {
                if (id.equals(book)) {
                    if (today.isAfter(date) || today.isBefore(date.minusDays(4))) {
                        return false;
                    }
                    books.get(date).remove(book);
                    this.borrowBook(book, date.plusDays(30));
                    return true;
                }
            }
        }
        return false;
    }

    public void addCredit(int credit) {
        this.credit = Math.min(this.credit + credit, 20);
    }

    public void subCredit(int credit) {
        this.credit -= credit;
    }

    public void dealOverDue(LocalDate today) {

        for (LocalDate date : books.keySet()) {
            if (date.isBefore(today)) {
                for (LibraryBookId bookId: books.get(date)) {
                    if (!overDueBooks.contains(bookId)) {
                        overDueBooks.add(bookId);
                        subCredit(2);
                    }
                }
            }
        }
        for (LocalDate date : tempBooks.keySet()) {
            if (date.isBefore(today)) {
                for (TempBook book: tempBooks.get(date)) {
                    LibraryBookId bookId = book.getLibraryBookId();
                    if (!overDueBooks.contains(bookId)) {
                        overDueBooks.add(bookId);
                        subCredit(2);
                    }
                }
            }
        }
    }

    public boolean isLaoLai() {
        return credit < 0;
    }

    public int getCredit() {
        return credit;
    }
}
