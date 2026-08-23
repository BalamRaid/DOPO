/**
 * Represents a single symbol identified by a CSS standard color name.
 * Immutable: once created, its color does not change.
 */
public class Symbol {
    private String color;

    public Symbol(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}