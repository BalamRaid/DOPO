import java.util.HashMap;
import java.time.LocalDate;

public class User {

    private int id;
    private String name;
    private String email;
    private String type;
    private String status;
    private HashMap<Integer, Room> rooms;
    private HashMap<Integer, Notification> inbox;

    public String getStatus() {
        return status;
    }
    
    public boolean hasBeenActive(LocalDate startDate, LocalDate endDate) {
        boolean wasActive = false;
        for (Room r: rooms.values()) {
            boolean activeInRoom = r.hasUserBeenActive(this, startDate, endDate);
            wasActive = activeInRoom || wasActive;
        }
        return wasActive;
    }
    
    public void suspend() {
        this.status = "SUSPENDED";
    }
    
}
