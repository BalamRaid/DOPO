import java.awt.Shape;
import java.awt.geom.Ellipse2D;

/**
 * Símbolo {@code ephemeral} (Ciclo 4): en cada giro de rueda decrementa su
 * tamaño hasta quedar como un punto. Escala de 1.0 a 0.15 en pasos de 0.15.
 * La lógica (configuration/distinct/jackpot) no cambia; solo el dibujo.
 *
 * @version 3.0 (Ciclo 4 M2)
 */
public class EphemeralSymbol extends Symbol {
    private double scale = 1.0;
    private static final double MIN = 0.15;
    private static final double STEP = 0.15;

    /**
     * Crea un símbolo ephemeral con el color indicado y escala 1.0.
     *
     * @param color nombre de color CSS del símbolo.
     */
    public EphemeralSymbol(String color) {
        super(color);
    }

    /**
     * Obtiene el tipo del símbolo.
     *
     * @return siempre {@code "ephemeral"}.
     */
    @Override
    public String getType() {
        return "ephemeral";
    }

    /**
     * Obtiene la escala visual actual.
     *
     * @return escala entre 0.15 y 1.0.
     */
    @Override
    public double getScale() {
        return scale;
    }

    /**
     * Decrementa la escala por un giro de rueda, sin bajar del mínimo.
     */
    @Override
    public void onSpun() {
        scale = Math.max(MIN, scale - STEP);
    }

    /**
     * Obtiene el color de la insignia ephemeral.
     *
     * @return {@code "orange"}.
     */
    @Override
    public String getBadgeColor() {
        return "orange";
    }

    /**
     * Obtiene la figura de la insignia ephemeral, centrada en (0,0).
     *
     * @return círculo de 10x10 para la esquina inferior derecha.
     */
    @Override
    public Shape getBadgeShape() {
        return new Ellipse2D.Double(-5, -5, 10, 10);
    }

    /**
     * Desplazamiento horizontal de la insignia.
     *
     * @return +18 (derecha).
     */
    @Override
    public int getBadgeDx() {
        return 18;
    }

    /**
     * Desplazamiento vertical de la insignia.
     *
     * @return +18 (abajo).
     */
    @Override
    public int getBadgeDy() {
        return 18;
    }
}
