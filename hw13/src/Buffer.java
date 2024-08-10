import com.oocourse.library1.LibraryRequest;

import java.util.ArrayList;

public class Buffer {

    private ArrayList<LibraryRequest> successOrderOfToday = new ArrayList<>();

    public ArrayList<LibraryRequest> getTodayOrder() {
        return successOrderOfToday;
    }

    public void addOrder(LibraryRequest request) {
        successOrderOfToday.add(request);
    }

}
