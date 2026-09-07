import java.util.HashMap;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Chat {

    private int id;
    private String type;
    private boolean closed;
    private Room room;
    private HashMap<Integer,Message> messages;

    public boolean getClosed() {
        return closed;
    }
    
    public boolean hasUserInteracted(User u, LocalDate startDate, LocalDate endDate) {
        boolean interacted = false;
        if(type.equals("GROUP")) {
            for (Message m: messages.values()) {
                LocalDateTime createdAt = m.getCreatedAt();
                if(!createdAt.toLocalDate().isBefore(startDate) && !createdAt.toLocalDate().isAfter(endDate)) {
                    User author = m.getAuthor();
                    if(author.equals(u)) {
                        interacted = true;
                    }
                }
            } 
        } else {
            for (Message m: messages.values()) {
                boolean reactedInMessage = m.hasUserReacted(u, startDate, endDate);
                if(reactedInMessage) {
                    interacted = true;
                }
            }
        }
        return interacted;
    }
    
}
