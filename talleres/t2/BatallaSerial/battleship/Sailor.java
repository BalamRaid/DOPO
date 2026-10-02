package battleship;

import java.util.Objects;

/**
 * Marino que pertenece a una flota. Puede formar parte de la tripulación de un
 * barco o actuar como piloto o copiloto de un avión.
 *
 * <p>La identidad de un marino es la de su objeto: dos marinos con el mismo
 * nombre y rango siguen siendo personas distintas.</p>
 *
 * @author DOPO
 * @version 1.0
 */
public class Sailor implements SelfDestructible {

    private final String name;
    private int rank;
    private boolean instructed;
    private boolean destroyed;

    /**
     * Crea un marino.
     *
     * @param name nombre del marino, no puede ser nulo
     * @param rank rango del marino
     */
    public Sailor(String name, int rank) {
        this.name = Objects.requireNonNull(name, "El nombre no puede ser nulo");
        this.rank = rank;
    }

    /**
     * Consulta el nombre.
     *
     * @return nombre del marino
     */
    public String getName() {
        return name;
    }

    /**
     * Consulta el rango.
     *
     * @return rango del marino
     */
    public int getRank() {
        return rank;
    }

    /**
     * Modifica el rango (por ejemplo, tras un ascenso).
     *
     * @param rank nuevo rango
     */
    public void setRank(int rank) {
        this.rank = rank;
    }

    /**
     * Le comunica al marino la instrucción de autodestruirse.
     */
    public void receiveSelfDestructInstruction() {
        instructed = true;
    }

    /**
     * Un marino se autodestruye si recibió la instrucción.
     *
     * @return {@code true} si recibió la instrucción de autodestruirse
     */
    @Override
    public boolean mustSelfDestruct() {
        return instructed;
    }

    /**
     * Informa la causa de la decisión del marino.
     *
     * @return {@link SelfDestructible#INSTRUCTION_CAUSE} si recibió la
     *         instrucción; {@link SelfDestructible#NO_DECISION} en caso contrario
     */
    @Override
    public String getSelfDestructCause() {
        return instructed ? INSTRUCTION_CAUSE : NO_DECISION;
    }

    @Override
    public void selfDestruct() {
        destroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public String toString() {
        return name + " (rango " + rank + ")";
    }
}