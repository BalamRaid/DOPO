package battleship;

import java.util.Objects;

/**
 * Cápsula submarina sin tripulantes que pide instrucciones a una nodriza.
 *
 * <p>Alcanza profundidades superiores a 8.000 metros, por lo que es inmune a
 * los ataques y nunca es débil. Su nodriza puede ser un {@link Ship} o
 * incluso otra cápsula; en este último caso, la instrucción se propaga por
 * la cadena hasta llegar a un barco.</p>
 *
 * <p>Su regla de autodestrucción es distinta a la de los demás elementos: se
 * autodestruye únicamente cuando su nodriza es destruida, sin importar si
 * recibió o no la instrucción general de autodestrucción.</p>
 *
 * @author DOPO
 * @version 1.0
 */
public class Capsule extends Machine implements Mothership {

    /** Causa informada cuando la cápsula decide autodestruirse por su nodriza. */
    public static final String MOTHER_DESTROYED_CAUSE = "Su nodriza fue destruida";

    private final Mothership mother;

    /**
     * Crea una cápsula asociada a su nodriza. La nodriza se define al crear la
     * cápsula y no cambia, lo que impide ciclos en la cadena de nodrizas.
     *
     * @param location posición inicial
     * @param mother   nodriza de la cápsula (barco u otra cápsula), no puede ser nula
     */
    public Capsule(Position location, Mothership mother) {
        super(location);
        this.mother = Objects.requireNonNull(mother, "La nodriza no puede ser nula");
    }

    /**
     * Consulta la nodriza.
     *
     * @return nodriza de la cápsula
     */
    public Mothership getMother() {
        return mother;
    }

    /**
     * Pide instrucción a la nodriza.
     *
     * @return instrucción entregada por la nodriza
     */
    public String requestInstruction() {
        return mother.giveInstruction();
    }

    /**
     * Como nodriza de otra cápsula, retransmite la instrucción que recibe de
     * su propia nodriza.
     *
     * @return instrucción de la cadena de nodrizas
     */
    @Override
    public String giveInstruction() {
        return requestInstruction();
    }

    /**
     * Una cápsula nunca es débil.
     *
     * @return siempre {@code false}
     */
    @Override
    public boolean isWeak() {
        return false;
    }

    /**
     * Una cápsula no tiene tripulantes, por lo que no requiere marinos.
     *
     * @return siempre {@code false}
     */
    @Override
    public boolean needsSailors() {
        return false;
    }

    /**
     * Una cápsula es inmune a las explosiones por la profundidad a la que opera.
     *
     * @return siempre {@code false}
     */
    @Override
    public boolean canBeDestroyed() {
        return false;
    }

    /**
     * Se autodestruye si su nodriza fue destruida.
     *
     * @return {@code true} si la nodriza está destruida
     */
    @Override
    public boolean mustSelfDestruct() {
        return mother.isDestroyed();
    }

    /**
     * Informa la causa de su decisión.
     *
     * @return {@link #MOTHER_DESTROYED_CAUSE} si la nodriza fue destruida;
     *         {@link SelfDestructible#NO_DECISION} en caso contrario
     */
    @Override
    public String getSelfDestructCause() {
        return mother.isDestroyed() ? MOTHER_DESTROYED_CAUSE : NO_DECISION;
    }
}