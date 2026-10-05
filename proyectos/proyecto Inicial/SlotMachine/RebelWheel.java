import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * Rueda de tipo {@code rebel} (Ciclo 4).
 *
 * <p>No se deja bloquear, ni intercambiar, ni eliminar: {@code lock},
 * {@code swap} y {@code delWheel} sobre ella fallan ({@code ok()==false})
 * sin modificar la máquina. Sí se deja girar y configurar directamente.
 * Nunca queda en estado bloqueado.
 *
 * @version 3.0 (Ciclo 4 M1)
 */
public class RebelWheel extends Wheel {

    /**
     * Crea una rueda rebel en la posición 0 y sin bloqueo.
     */
    public RebelWheel() {
        super();
    }

    /**
     * Obtiene el tipo de la rueda.
     *
     * @return siempre {@code "rebel"}.
     */
    @Override
    public String getType() {
        return "rebel";
    }

    /**
     * Indica si acepta ser fijada.
     *
     * @return siempre false en esta clase.
     */
    @Override
    public boolean canBeLocked() {
        return false;
    }

    /**
     * Indica si acepta ser intercambiada.
     *
     * @return siempre false en esta clase.
     */
    @Override
    public boolean canBeSwapped() {
        return false;
    }

    /**
     * Indica si acepta ser eliminada.
     *
     * @return siempre false en esta clase.
     */
    @Override
    public boolean canBeDeleted() {
        return false;
    }

    /**
     * Fija o libera la rueda, vetando el bloqueo.
     *
     * <p>Aunque se invoque directamente, una rebel nunca queda bloqueada:
     * el valor true se ignora y el valor false se acepta (no-op).
     *
     * @param locked true (ignorado) o false (aceptado como no-op).
     */
    @Override
    public void setLocked(boolean locked) {
        if (!locked) {
            super.setLocked(false);
        }
    }

    /**
     * Obtiene el color del marcador visual de rebel.
     *
     * @return {@code "red"} para distinguirse claramente.
     */
    @Override
    public String getMarkerColor() {
        return "red";
    }

    /**
     * Obtiene la figura del marcador visual de rebel, centrada en (0,0).
     * Insignia pequeña de esquina para no tapar ni confundirse con el símbolo.
     *
     * @return cuadrado de 14x14 para la esquina superior derecha.
     */
    @Override
    public Shape getMarkerShape() {
        return new Rectangle2D.Double(-7, -7, 14, 14);
    }

    /**
     * Desplazamiento horizontal de la insignia rebel.
     *
     * @return +22 para situarla a la derecha del símbolo.
     */
    @Override
    public int getMarkerDx() {
        return 22;
    }

    /**
     * Desplazamiento vertical de la insignia rebel.
     *
     * @return -22 para situarla arriba del símbolo.
     */
    @Override
    public int getMarkerDy() {
        return -22;
    }
}
