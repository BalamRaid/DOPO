import java.time.LocalDateTime;

public class Reaction {

    private String emoji;
    private User author;
    private LocalDateTime createdAt;
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public User getAuthor() {
        return author;
    }
    
}
