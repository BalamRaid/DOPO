import java.awt.Shape;
import java.awt.geom.Ellipse2D;

/**
 * Rueda de tipo {@code lefty} (Ciclo 4).
 *
 * <p>Al girar ({@code spin(wheel)}, {@code spin(wheel,steps)} o {@code spin()}),
 * si existe una rueda a su izquierda copia su índice visible en lugar de rotar.
 * Si es la primera rueda (sin vecina) gira como una rueda normal.
 * La configuración directa ({@code spin(String[])} y {@code placeSymbol})
 * no copia: asigna el símbolo pedido para permitir un montaje determinista.
 *
 * @version 3.0 (Ciclo 4 M1)
 */
public class LeftyWheel extends Wheel {

    /**
     * Crea una rueda lefty en la posición 0 y sin bloqueo.
     */
    public LeftyWheel() {
        super();
    }

    /**
     * Obtiene el tipo de la rueda.
     *
     * @return siempre {@code "lefty"}.
     */
    @Override
    public String getType() {
        return "lefty";
    }

    /**
     * Indica si al girar copia a la izquierda.
     *
     * @return siempre true en esta clase.
     */
    @Override
    public boolean copiesLeft() {
        return true;
    }

    /**
     * Obtiene el color del marcador visual de lefty.
     *
     * @return {@code "dodgerblue"} para distinguirse de normal y rebel.
     */
    @Override
    public String getMarkerColor() {
        return "dodgerblue";
    }

    /**
     * Obtiene la figura del marcador visual de lefty, centrada en (0,0).
     * Insignia pequeña de esquina para no tapar el símbolo.
     *
     * @return elipse de 14x14 para la esquina superior izquierda.
     */
    @Override
    public Shape getMarkerShape() {
        return new Ellipse2D.Double(-7, -7, 14, 14);
    }

    /**
     * Desplazamiento horizontal de la insignia lefty.
     *
     * @return -22 para situarla a la izquierda del símbolo.
     */
    @Override
    public int getMarkerDx() {
        return -22;
    }

    /**
     * Desplazamiento vertical de la insignia lefty.
     *
     * @return -22 para situarla arriba del símbolo.
     */
    @Override
    public int getMarkerDy() {
        return -22;
    }
}
