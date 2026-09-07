import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.time.LocalDate;

/**
 * Representa la aplicación central de ECIChat. Administra el conjunto de
 * usuarios registrados y los cuartos (rooms) existentes en el sistema.
 *
 * INVARIANTE DE CLASE:
 * - rooms != null && users != null
 * - para toda entrada (k, room) en rooms: room != null
 * - para toda entrada (k, user) en users: user != null
 */

public class ECIChatApp {
    
    private Map<Integer, Room> rooms;
    private Map<Integer, User> users;

    public ECIChatApp() {
        this.rooms = new HashMap<>();
        this.users = new HashMap<>();
    }
    
    public void suspendInactiveUsers() {
        LocalDate today = LocalDate.now();
        LocalDate sixMonthsAgo = today.minusMonths(6);
        for (User u: users.values()) {
            if (u.getStatus().equals("ACTIVE")) {
                boolean isActive = u.hasBeenActive(sixMonthsAgo, today);
                if (!isActive) {
                    u.suspend();
                }
            }
        }
    }
    
}
