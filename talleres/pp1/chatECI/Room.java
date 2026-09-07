import java.time.LocalDate;
import java.util.HashMap;

public class Room {

    private int Id;
    private String name;
    private String description;
    private LocalDate createdDate;
    private int capacity;
    private boolean private_;
    private HashMap<Integer,User> participants;
    private HashMap<Integer,Chat> chats;

    public boolean hasUserBeenActive(User u, LocalDate startDate, LocalDate endDate) {
        boolean wasInteracted = false;
        for (Chat c: chats.values()) {
            if(!c.getClosed()) {
                boolean activeInChat = c.hasUserInteracted(u, startDate, endDate);
                wasInteracted = wasInteracted || activeInChat;
            }
        }
        return wasInteracted;
    }
    
}
