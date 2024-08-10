import com.oocourse.library1.LibraryBookId;
import com.oocourse.library1.LibraryMoveInfo;
import com.oocourse.library1.LibraryRequest;
import com.oocourse.library1.LibrarySystem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class Library {
    private AppointmentOffice appointmentOffice = new AppointmentOffice();
    private Bookshelf bookshelf = new Bookshelf();
    private BorrowReturnOffice borrowReturnOffice = new BorrowReturnOffice();
    private Buffer buffer = new Buffer();
    private LocalDate today;
    private HashMap<String, Person> people = new HashMap<>();
    private ArrayList<LibraryMoveInfo> information = new ArrayList<>();

    // 初始化图书馆
    public void initLibrary(HashMap<LibraryBookId, Integer> inventory) {
        // initLibrary的具体实现
        bookshelf.initShelf(inventory);
    }

    // 开馆: 将所有BR的书放回书架, 将6天前的书全部放回书架
    public void openLibrary(LocalDate date) {
        this.today = date;
        // 其他开馆逻辑
        borrowReturnOfficeToShelf();
        appointmentOfficeToShelf(today, true);
        LibrarySystem.PRINTER.move(date, information);
        information.clear();
    }

    // 从借还办公室到书架
    public void borrowReturnOfficeToShelf() {
        // 方法的具体实现
        HashMap<LibraryBookId, Integer> toAdd = borrowReturnOffice.getBooks(today);
        for (LibraryBookId book : toAdd.keySet()) {
            for (int i = 1; i <= toAdd.get(book); i++) {
                LibraryMoveInfo info = new LibraryMoveInfo(book, "bro", "bs");
                information.add(info);
                bookshelf.addBook(book);
            }
        }
        toAdd.clear();
    }

    // 闭馆: 缓冲区域书给AO、过期预约书目给书架、
    public void closeLibrary(LocalDate date) {
        // 闭馆逻辑

        bufferToAppointmentOffice();
        appointmentOfficeToShelf(date, false);
        LibrarySystem.PRINTER.move(date, information);
        information.clear();
    }

    // 从预约办公室到书架：过期
    public void appointmentOfficeToShelf(LocalDate date, boolean flag) {
        // 方法的具体实现
        //true open false close
        LocalDate cleanDate = date.minusDays(flag ? 5 : 4);
        HashMap<LibraryBookId, Integer> toAdd = appointmentOffice.getCleanBooks(cleanDate);
        if (toAdd != null) {
            for (LibraryBookId book : toAdd.keySet()) {
                for (int i = 1; i <= toAdd.get(book); i++) {
                    LibraryMoveInfo info = new LibraryMoveInfo(book, "ao", "bs");
                    information.add(info);
                    bookshelf.addBook(book);
                }
            }
            toAdd.clear();
        }
    }

    // 从缓冲区到预约办公室：预约
    public void bufferToAppointmentOffice() {
        // 方法的具体实现
        ArrayList<LibraryRequest> requests = buffer.getTodayOrder();
        ArrayList<LibraryRequest> success = new ArrayList<>();
        for (LibraryRequest request : requests) {
            if (bookshelf.hasBook(request.getBookId())) {
                bookshelf.delBook(request.getBookId());
                success.add(request);
                LibraryMoveInfo info = new LibraryMoveInfo(
                        request.getBookId(), "bs", "ao", request.getStudentId());
                information.add(info);
                //LibrarySystem.PRINTER.move(today, info);
            }
        }
        appointmentOffice.addTodayOrder(today, success);
        requests.clear();
    }

    // 从书架到缓冲区
    public void shelfToBuffer(LibraryRequest request) {
        // 方法的具体实现
        //从架子上取出来
        //bookshelf.delBook(request.getBookId());
        //给到缓冲区
        buffer.addOrder(request);
    }

    // 处理图书馆请求
    public void dealLibraryRequest(LibraryRequest request, LocalDate date) {
        // 方法的具体实现
        switch (request.getType()) {
            case QUERIED:
                int res = dealQueryRequest(request);
                LibrarySystem.PRINTER.info(date, request.getBookId(), res);
                break;
            case BORROWED:
                if (dealBorrowRequest(request)) {
                    LibrarySystem.PRINTER.accept(date, request);
                } else {
                    LibrarySystem.PRINTER.reject(date, request);
                }
                break;
            case ORDERED:
                if (dealOrderRequest(request)) {
                    LibrarySystem.PRINTER.accept(date, request);
                } else {
                    LibrarySystem.PRINTER.reject(date, request);
                }
                break;
            case PICKED:
                if (dealPickRequest(request)) {
                    LibrarySystem.PRINTER.accept(date, request);
                } else {
                    LibrarySystem.PRINTER.reject(date, request);
                }
                break;
            case RETURNED:
                dealReturnRequest(request);
                LibrarySystem.PRINTER.accept(date, request);
                break;
            default:
                break;
        }
    }

    // 查询请求
    public int dealQueryRequest(LibraryRequest request) {
        // 方法的具体实现
        LibraryBookId book = request.getBookId();
        return bookshelf.getBookSum(book);
    }

    // 借书请求
    public boolean dealBorrowRequest(LibraryRequest request) {
        // 方法的具体实现
        LibraryBookId book = request.getBookId();
        Person person = getPerson(request.getStudentId());
        if (book.isTypeA() || !bookshelf.hasBook(book)) {
            return false;
        }
        //此处保证了一定书存在于架子上
        if ((book.isTypeB() && person.hasBtype()) || (book.isTypeC() && person.hasBook(book))) {
            shelfToBorrowReturnOffice(request);
            return false;
        }
        bookshelf.delBook(book);
        person.borrowBook(book);
        return true;
    }

    // 预约请求
    public boolean dealOrderRequest(LibraryRequest request) {
        // 方法的具体实现
        LibraryBookId book = request.getBookId();
        Person person = getPerson(request.getStudentId());
        if ((book.isTypeA()) ||
                (book.isTypeB() && person.hasBtype()) ||
                (book.isTypeC() && person.hasBook(book))) {
            return false;
        }
        shelfToBuffer(request);
        return true;
    }

    // 取书请求
    public boolean dealPickRequest(LibraryRequest request) {
        // 方法的具体实现
        LibraryBookId book = request.getBookId();
        Person person = getPerson(request.getStudentId());
        if ((book.isTypeB() && person.hasBtype()) ||
                (book.isTypeC() && person.hasBook(book))) {
            return false;
        }
        if (appointmentOffice.hasBook(book)) {
            boolean flag = appointmentOffice.delPersonBook(book, request.getStudentId());
            if (flag) {
                person.borrowBook(book);
                return true;
            }
        }
        return false;
    }

    // 还书请求
    public void dealReturnRequest(LibraryRequest request) {
        // 方法的具体实现
        LibraryBookId bookId = request.getBookId();
        Person person = getPerson(request.getStudentId());
        person.returnBook(bookId);
        borrowReturnOffice.addBookRequest(request);
    }

    public Person getPerson(String id) {
        if (!people.containsKey(id)) {
            people.put(id, new Person(id));
        }
        return people.get(id);
    }

    public void shelfToBorrowReturnOffice(LibraryRequest request) {
        //从架子上取出来
        bookshelf.delBook(request.getBookId());
        //给借还处
        borrowReturnOffice.addBookRequest(request);
    }

}
