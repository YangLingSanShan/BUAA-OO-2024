import com.oocourse.library3.LibraryRequest;
import com.oocourse.library3.LibraryCommand;
import com.oocourse.library3.LibraryBookId;
import com.oocourse.library3.LibraryQcsCmd;
import com.oocourse.library3.LibraryMoveInfo;
import com.oocourse.library3.LibrarySystem;
import com.oocourse.library3.LibraryReqCmd;
import com.oocourse.library3.annotation.SendMessage;
import com.oocourse.library3.annotation.Trigger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static com.oocourse.library3.LibraryBookId.Type;

public class Library {
    private Bookshelf bookshelf = new Bookshelf();
    private BorrowReturnOffice borrowReturnOffice = new BorrowReturnOffice();
    private Buffer buffer = new Buffer();
    private LocalDate today;
    private HashMap<String, Person> people = new HashMap<>();
    private ArrayList<LibraryMoveInfo> information = new ArrayList<>();
    private BookDriftCorner bookDriftCorner = new BookDriftCorner();
    private HashMap<Type, Integer> borrowTable = new HashMap<>();
    private AppointmentOffice appointmentOffice = new AppointmentOffice(this.people);

    // 初始化图书馆
    @Trigger(from = "InitState", to = "Bookshelf")
    public void initLibrary(HashMap<LibraryBookId, Integer> inventory) {
        // initLibrary的具体实现
        bookshelf.initShelf(inventory);
        borrowTable.put(Type.A, 114514);
        borrowTable.put(Type.AU, 114514);
        borrowTable.put(Type.B, 30);
        borrowTable.put(Type.BU, 7);
        borrowTable.put(Type.C, 60);
        borrowTable.put(Type.CU, 14);
    }

    // 开馆: 将所有BR的书放回书架, 将6天前的书全部放回书架
    public void openLibrary(LocalDate date) {
        this.today = date;
        // 其他开馆逻辑
        dealOverDueCredit();
        borrowReturnOfficeToShelf();
        borrowReturnOfficeToCorner();
        appointmentOfficeToShelf(today, true);
        LibrarySystem.PRINTER.move(date, information);
        information.clear();
    }

    public void borrowReturnOfficeToCorner() {
        HashSet<TempBook> toAdd = borrowReturnOffice.getTempBooks();
        for (TempBook book : toAdd) {
            if (book.getTime() >= 2) {
                Type type;
                if (book.isTypeAU()) {
                    type = Type.A;
                } else if (book.isTypeBU()) {
                    type = Type.B;
                } else {
                    type = Type.C;
                }
                Person person = getPerson(book.getDonator());
                person.addCredit(2);
                LibraryBookId newBook = new LibraryBookId(type, book.getID());
                LibraryMoveInfo info = new LibraryMoveInfo(book.getLibraryBookId(), "bro", "bs");
                information.add(info);
                bookshelf.addBook(newBook);
            } else {
                LibraryMoveInfo info = new LibraryMoveInfo(book.getLibraryBookId(), "bro", "bdc");
                information.add(info);
                bookDriftCorner.addBook(book);
            }
        }
        toAdd.clear();
    }

    // 从借还办公室到书架
    @Trigger(from = "BorrowReturnOffice", to = "Bookshelf")
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
    @Trigger(from = "AppointmentOffice", to = "Bookshelf")
    @SendMessage(from = "appointmentOffice", to = "bookShelf")
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
    @Trigger(from = "Bookshelf", to = "AppointmentOffice")
    @SendMessage(from = "bookShelf", to = "appointmentOffice")
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
            } else {
                buffer.addFailRequest(request);
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
    public void dealLibraryRequest(LibraryCommand command, LocalDate date) {
        // 方法的具体实现
        if (command instanceof LibraryQcsCmd) {
            String string = ((LibraryQcsCmd) command).getStudentId();
            LibrarySystem.PRINTER.info(date, string, getPerson(string).getCredit());
            return;
        }
        LibraryRequest request = ((LibraryReqCmd) command).getRequest();
        switch (request.getType()) {
            case QUERIED:
                int res = dealQueryRequest(request);
                LibrarySystem.PRINTER.info(date, request.getBookId(), res);
                break;
            case BORROWED:
                if (dealBorrowRequest(request)) {
                    LibrarySystem.PRINTER.accept(command);
                } else {
                    LibrarySystem.PRINTER.reject(command);
                }
                break;
            case ORDERED:
                if (dealOrderRequest(request)) {
                    LibrarySystem.PRINTER.accept(command);
                } else {
                    LibrarySystem.PRINTER.reject(command);
                }
                break;
            case PICKED:
                if (dealPickRequest(request)) {
                    LibrarySystem.PRINTER.accept(command);
                } else {
                    LibrarySystem.PRINTER.reject(command);
                }
                break;
            case RETURNED:
                boolean flag = dealReturnRequest(request);
                LibrarySystem.PRINTER.accept(command, flag ? "overdue" : "not overdue");
                break;
            case DONATED:
                dealDonateRequest(request);
                LibrarySystem.PRINTER.accept(command);
                break;
            case RENEWED:
                if (dealRenewRequest(request)) {
                    LibrarySystem.PRINTER.accept(command);
                } else {
                    LibrarySystem.PRINTER.reject(command);
                }
                break;
            default:
                break;
        }
    }

    public boolean dealRenewRequest(LibraryRequest request) {
        LibraryBookId book = request.getBookId();
        Person person = getPerson(request.getStudentId());
        if (person.isLaoLai()) {
            return false;
        }
        if (buffer.hasBook(book) || appointmentOffice.hasBook(book)) {
            if (!bookshelf.hasBook(book)) {
                return false;
            }
        }
        return person.renewBook(book, today);
    }

    // 查询请求
    public int dealQueryRequest(LibraryRequest request) {
        // 方法的具体实现
        LibraryBookId book = request.getBookId();
        if (book.isTypeA() || book.isTypeB() || book.isTypeC()) {
            return bookshelf.getBookSum(book);
        } else {
            return bookDriftCorner.getBookSum(book);
        }
    }

    // 借书请求
    @Trigger(from = "Bookshelf", to = "Person")
    @Trigger(from = "Bookshelf", to = "AppointmentOffice")
    public boolean dealBorrowRequest(LibraryRequest request) {
        // 方法的具体实现
        LibraryBookId book = request.getBookId();
        Person person = getPerson(request.getStudentId());

        if (book.isTypeA() || book.isTypeB() || book.isTypeC()) {
            if (book.isTypeA() || !bookshelf.hasBook(book)) {
                return false;
            }
            //此处保证了一定书存在于架子上
            if (person.isLaoLai()) {
                shelfCornerToBorrowReturnOffice(request);
                return false;
            }
            if ((book.isTypeB() && person.hasBtype()) || (book.isTypeC() && person.hasBook(book))) {
                shelfCornerToBorrowReturnOffice(request);
                return false;
            }
            bookshelf.delBook(book);
            person.borrowBook(book, today.plusDays(borrowTable.get(book.getType())));
        } else {
            if (book.isTypeAU() || !bookDriftCorner.hasBook(book)) {
                return false;
            }
            if (person.isLaoLai()) {
                shelfCornerToBorrowReturnOffice(request);
                return false;
            }
            if ((book.isTypeBU() && person.hasBUtype())
                    || (book.isTypeCU()) && person.hasBook(book)) {
                shelfCornerToBorrowReturnOffice(request);
                return false;
            }
            TempBook tempBook = bookDriftCorner.delBook(book);
            person.borrowBook(tempBook, today.plusDays(borrowTable.get(book.getType())));
        }
        return true;
    }

    // 预约请求
    public boolean dealOrderRequest(LibraryRequest request) {
        // 方法的具体实现
        LibraryBookId book = request.getBookId();
        Person person = getPerson(request.getStudentId());
        if (person.isLaoLai()) {
            return false;
        }
        if (book.isTypeAU() || book.isTypeBU() || book.isTypeCU()) {
            return false;
        }

        if ((book.isTypeA()) ||
                (book.isTypeB() && person.hasBtype()) ||
                (book.isTypeC() && person.hasBook(book))) {
            return false;
        }
        if (book.isTypeB() && buffer.personhasBookB(request.getStudentId())) {
            return false;
        }
        if (book.isTypeC() && buffer.personhasBookC(request.getStudentId(), book)) {
            return false;
        }
        if (book.isTypeB() && appointmentOffice.personhasBookB(request.getStudentId())) {
            return false;
        }
        if (book.isTypeC() && appointmentOffice.personhasBookC(request.getStudentId(), book)) {
            return false;
        }
        shelfToBuffer(request);
        return true;
    }

    // 取书请求
    @Trigger(from = "AppointmentOffice", to = "Person")
    public boolean dealPickRequest(LibraryRequest request) {
        // 方法的具体实现
        LibraryBookId book = request.getBookId();
        Person person = getPerson(request.getStudentId());
        if (book.isTypeA() || book.isTypeB() || book.isTypeC()) {
            if (book.isTypeA() ||
                    (book.isTypeB() && person.hasBtype()) ||
                    (book.isTypeC() && person.hasBook(book))) {
                return false;
            }
            if (appointmentOffice.hasBook(book, request.getStudentId())) {
                boolean flag = appointmentOffice.delPersonBook(book, request.getStudentId());
                if (flag) {
                    person.borrowBook(book, today.plusDays(borrowTable.get(book.getType())));
                    return true;
                }
            }
        }
        return false;
    }

    // 还书请求
    @Trigger(from = "Person", to = "BorrowReturnOffice")
    public boolean dealReturnRequest(LibraryRequest request) {
        // 方法的具体实现
        LibraryBookId bookId = request.getBookId();
        Person person = getPerson(request.getStudentId());
        boolean flag = person.isOverDue(bookId, today);
        TempBook book = person.returnBook(bookId);
        if (bookId.isTypeA() || bookId.isTypeB() || bookId.isTypeC()) {
            borrowReturnOffice.addBookRequest(request);
        } else {
            borrowReturnOffice.addBookRequest(book);
        }
        if (!flag) {
            person.addCredit(1);
        }
        return flag;
    }

    public Person getPerson(String id) {
        if (!people.containsKey(id)) {
            people.put(id, new Person(id));
        }
        return people.get(id);
    }

    public void shelfCornerToBorrowReturnOffice(LibraryRequest request) {
        //从架子上取出来
        LibraryBookId libraryBookId = request.getBookId();
        if (libraryBookId.isTypeA() || libraryBookId.isTypeB() || libraryBookId.isTypeC()) {
            bookshelf.delBook(libraryBookId);
            //给借还处
            borrowReturnOffice.addBookRequest(request);
        } else {
            TempBook book = bookDriftCorner.delBook(libraryBookId);
            borrowReturnOffice.addBookRequest(book);
        }
    }

    public void dealDonateRequest(LibraryRequest request) {
        Person person = getPerson(request.getStudentId());
        TempBook tempBook = new TempBook(request.getBookId(), request.getStudentId());
        person.addCredit(2);
        bookDriftCorner.addBook(tempBook);
    }

    public void dealOverDueCredit() {
        for (Person person : people.values()) {
            person.dealOverDue(today);
        }
    }

    @SendMessage(from = "library", to = "bookShelf")
    public void orderNewBook() {

    }

    @SendMessage(from = "appointmentOffice", to = "person")
    public void getOrderedBook() {

    }
}
