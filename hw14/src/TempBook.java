import com.oocourse.library2.LibraryBookId;

public class TempBook {
    private LibraryBookId libraryBookId;
    private int time;

    public TempBook(LibraryBookId libraryBookId) {
        this.libraryBookId = libraryBookId;
        time = 0;
    }

    public LibraryBookId getLibraryBookId() {
        return libraryBookId;
    }

    public int getTime() {
        return time;
    }

    public boolean isTypeAU() {
        return libraryBookId.isTypeAU();
    }

    public boolean isTypeBU() {
        return libraryBookId.isTypeBU();
    }

    public String getID() {
        return libraryBookId.getUid();
    }

    public void addTime() {
        time++;
    }

}
