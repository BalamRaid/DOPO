import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * Símbolo {@code shy} (Ciclo 4): alterna visible/invisible cada vez que una
 * rueda lo selecciona (aterriza en él por giro o asignación directa).
 * Invisible se dibuja como fantasma gris; la lógica
 * (configuration/distinct/jackpot) no cambia.
 *
 * @version 3.0 (Ciclo 4 M2)
 */
public class ShySymbol extends Symbol {
    private boolean visible = true;

    /**
     * Crea un símbolo shy visible con el color indicado.
     *
     * @param color nombre de color CSS del símbolo.
     */
    public ShySymbol(String color) {
        super(color);
    }

    /**
     * Obtiene el tipo del símbolo.
     *
     * @return siempre {@code "shy"}.
     */
    @Override
    public String getType() {
        return "shy";
    }

    /**
     * Indica si el símbolo está visible.
     *
     * @return true si visible; false si fantasma.
     */
    @Override
    public boolean isVisible() {
        return visible;
    }

    /**
     * Alterna visible/invisible por selección.
     */
    @Override
    public void onSelected() {
        visible = !visible;
    }

    /**
     * Obtiene el color de la insignia shy (siempre dibujada).
     *
     * @return {@code "purple"}.
     */
    @Override
    public String getBadgeColor() {
        return "purple";
    }

    /**
     * Obtiene la figura de la insignia shy, centrada en (0,0).
     *
     * @return cuadrado de 10x10 para la esquina inferior izquierda.
     */
    @Override
    public Shape getBadgeShape() {
        return new Rectangle2D.Double(-5, -5, 10, 10);
    }

    /**
     * Desplazamiento horizontal de la insignia.
     *
     * @return -18 (izquierda).
     */
    @Override
    public int getBadgeDx() {
        return -18;
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
