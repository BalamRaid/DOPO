/**
 * Utility class holding the set of valid CSS standard color names.
 * Used by the model to validate symbol colors independently of any
 * graphical rendering.
 */
public class CssColors {
    private static final java.util.Set<String> VALID_NAMES = java.util.Set.of(
        "red", "blue", "green", "yellow", "orange", "purple"
        
    );

    public static boolean isValid(String name) {
        return VALID_NAMES.contains(name);
    }
}