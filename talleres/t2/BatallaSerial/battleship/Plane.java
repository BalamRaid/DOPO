package battleship;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Avión de una flota, identificado por su placa.
 *
 * <p>Un avión es débil si no tiene piloto principal. Mientras está en el aire
 * es inmune a las explosiones que ocurren en la superficie.</p>
 *
 * @author DOPO
 * @version 1.0
 */
public class Plane extends Machine {

    private final String plate;
    private boolean inAir;
    private Sailor pilot;
    private Sailor copilot;

    /**
     * Crea un avión que inicialmente está en tierra y sin copiloto.
     *
     * @param plate    placa que identifica al avión, no puede ser nula
     * @param location posición inicial
     * @param pilot    piloto principal; puede ser {@code null} si aún no se asigna
     */
    public Plane(String plate, Position location, Sailor pilot) {
        super(location);
        this.plate = Objects.requireNonNull(plate, "La placa no puede ser nula");
        this.pilot = pilot;
        this.inAir = false;
    }

    /**
     * Consulta la placa.
     *
     * @return placa del avión
     */
    public String getPlate() {
        return plate;
    }

    /**
     * Indica si el avión está en el aire.
     *
     * @return {@code true} si está volando
     */
    public boolean isInAir() {
        return inAir;
    }

    /**
     * Cambia el estado de vuelo del avión.
     *
     * @param inAir {@code true} si el avión despega o está en el aire
     */
    public void setInAir(boolean inAir) {
        this.inAir = inAir;
    }

    /**
     * Consulta el piloto principal.
     *
     * @return piloto principal, o {@code null} si no tiene
     */
    public Sailor getPilot() {
        return pilot;
    }

    /**
     * Asigna el piloto principal.
     *
     * @param pilot nuevo piloto, o {@code null} para dejar el avión sin piloto
     */
    public void setPilot(Sailor pilot) {
        this.pilot = pilot;
    }

    /**
     * Consulta el copiloto.
     *
     * @return copiloto, o {@code null} si no tiene
     */
    public Sailor getCopilot() {
        return copilot;
    }

    /**
     * Asigna el copiloto (opcional).
     *
     * @param copilot nuevo copiloto, o {@code null} para quitarlo
     */
    public void setCopilot(Sailor copilot) {
        this.copilot = copilot;
    }

    /**
     * Un avión en el aire no es afectado por las explosiones.
     *
     * @return {@code true} únicamente si el avión está en tierra
     */
    @Override
    public boolean canBeDestroyed() {
        return !inAir;
    }

    /**
     * Un avión es débil si no tiene piloto principal. Un piloto que ya se
     * autodestruyó equivale a no tener piloto.
     *
     * @return {@code true} si no tiene piloto activo
     */
    @Override
    public boolean isWeak() {
        return pilot == null || pilot.isDestroyed();
    }

    /**
     * Un avión en el aire sin piloto activo no puede moverse.
     *
     * @return {@code false} si está en el aire y es débil; {@code true} en otro caso
     */
    @Override
    public boolean canMove() {
        return !(inAir && isWeak());
    }

    /**
     * Consulta el piloto asignado al avión. Un avión sin piloto activo no
     * tiene pilotos asignados.
     *
     * @return lista con el piloto principal, o vacía si no tiene piloto activo
     */
    @Override
    public List<Sailor> assignedPilots() {
        return isWeak() ? Collections.<Sailor>emptyList() : Collections.singletonList(pilot);
    }
}