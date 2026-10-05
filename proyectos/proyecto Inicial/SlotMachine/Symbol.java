import java.awt.Shape;

/**
 * Representa un único símbolo identificado por el nombre de un color
 * CSS estándar. Es la base de la jerarquía del Ciclo 4 (tipo {@code normal}).
 *
 * <p>Las subclases redefinen {@link #getType()}, la escala
 * ({@link #getScale()}, {@link #onSpun()}), la visibilidad
 * ({@link #isVisible()}, {@link #onSelected()}) y la insignia visual
 * ({@link #getBadgeColor()}, {@link #getBadgeShape()}).
 * El color es inmutable: una vez creado no cambia.
 *
 * @version 3.0 (Ciclo 4 M2)
 */
public class Symbol {
    private final String color;

    /**
     * Crea un símbolo normal con el color indicado.
     *
     * @param color nombre de color CSS del símbolo.
     */
    public Symbol(String color) {
        this.color = color;
    }

    /**
     * Obtiene el color del símbolo.
     *
     * @return nombre de color CSS del símbolo.
     */
    public String getColor() {
        return color;
    }

    /**
     * Obtiene el tipo del símbolo.
     *
     * @return {@code "normal"} en esta clase base.
     */
    public String getType() {
        return "normal";
    }

    /**
     * Obtiene la escala visual del símbolo (1.0 = tamaño base).
     *
     * @return siempre 1.0 en esta clase base.
     */
    public double getScale() {
        return 1.0;
    }

    /**
     * Indica si el símbolo está visible (no fantasma).
     *
     * @return siempre true en esta clase base.
     */
    public boolean isVisible() {
        return true;
    }

    /**
     * Hook invocado una vez por cada rueda movida en un giro.
     * La base no hace nada.
     */
    public void onSpun() {
    }

    /**
     * Hook invocado cada vez que una rueda aterriza en este símbolo.
     * La base no hace nada.
     */
    public void onSelected() {
    }

    /**
     * Obtiene el color de la insignia que distingue el tipo de símbolo.
     *
     * @return null en normal (sin insignia).
     */
    public String getBadgeColor() {
        return null;
    }

    /**
     * Obtiene la figura de la insignia que distingue el tipo, centrada en (0,0).
     *
     * @return null en normal (sin insignia).
     */
    public Shape getBadgeShape() {
        return null;
    }

    /**
     * Desplazamiento horizontal de la insignia respecto al centro de la rueda.
     *
     * @return 0 en normal.
     */
    public int getBadgeDx() {
        return 0;
    }

    /**
     * Desplazamiento vertical de la insignia respecto al centro de la rueda.
     *
     * @return 0 en normal.
     */
    public int getBadgeDy() {
        return 0;
    }
}
