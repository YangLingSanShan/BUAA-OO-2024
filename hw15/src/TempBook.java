import com.oocourse.library3.LibraryBookId;

public class TempBook {
    private LibraryBookId libraryBookId;
    private int time;
    private String donator;

    public TempBook(LibraryBookId libraryBookId, String donator) {
        this.libraryBookId = libraryBookId;
        time = 0;
        this.donator = donator;
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

    public String getDonator() {
        return donator;
    }

}
