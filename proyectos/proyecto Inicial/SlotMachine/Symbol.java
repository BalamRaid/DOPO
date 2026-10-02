/**
 * Representa un único símbolo identificado por el nombre de un color
 * CSS estándar. Es inmutable: una vez creado, su color no cambia.
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