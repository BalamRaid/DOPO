import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * Símbolo {@code giant} (Ciclo 4, tipo nuevo Req19): opuesto a ephemeral,
 * en cada giro de rueda incrementa su tamaño hasta 2.0 en pasos de 0.15.
 * Reutiliza la infraestructura de escala (OCP). Solo cambia el dibujo.
 *
 * @version 3.0 (Ciclo 4 M2)
 */
public class GiantSymbol extends Symbol {
    private double scale = 1.0;
    private static final double MAX = 2.0;
    private static final double STEP = 0.15;

    /**
     * Crea un símbolo giant con el color indicado y escala 1.0.
     *
     * @param color nombre de color CSS del símbolo.
     */
    public GiantSymbol(String color) {
        super(color);
    }

    /**
     * Obtiene el tipo del símbolo.
     *
     * @return siempre {@code "giant"}.
     */
    @Override
    public String getType() {
        return "giant";
    }

    /**
     * Obtiene la escala visual actual.
     *
     * @return escala entre 1.0 y 2.0.
     */
    @Override
    public double getScale() {
        return scale;
    }

    /**
     * Incrementa la escala por un giro de rueda, sin superar el máximo.
     */
    @Override
    public void onSpun() {
        scale = Math.min(MAX, scale + STEP);
    }

    /**
     * Obtiene el color de la insignia giant.
     *
     * @return {@code "darkgreen"}.
     */
    @Override
    public String getBadgeColor() {
        return "darkgreen";
    }

    /**
     * Obtiene la figura de la insignia giant, centrada en (0,0).
     *
     * @return cuadrado de 10x10 para la esquina inferior derecha.
     */
    @Override
    public Shape getBadgeShape() {
        return new Rectangle2D.Double(-5, -5, 10, 10);
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
