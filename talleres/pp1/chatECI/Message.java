import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.LocalDate;

public class Message {

    private int id;
    private String content;
    private LocalDateTime timestamp;
    private boolean read;
    private boolean deleted;
    private Chat chat;
    private ArrayList<Reaction> reactions;
    private HashMap<Integer,Notification> notifications;
    private User author;
    private LocalDateTime createdAt;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public User getAuthor() {
        return author;
    }
    
    public boolean hasUserReacted(User u, LocalDate startDate, LocalDate endDate) {
        boolean reacted = false;
        for (Reaction r: reactions) {
            LocalDateTime createdAt = r.getCreatedAt();
            if(!createdAt.toLocalDate().isBefore(startDate) && !createdAt.toLocalDate().isAfter(endDate)) {
                User author = r.getAuthor();
                if(author.equals(u)) {
                    reacted = true;
                }
            }
        }
        return reacted;
    }
    
}
