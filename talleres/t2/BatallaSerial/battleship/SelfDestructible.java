package battleship;

/**
 * Contrato de los elementos de una flota que están preparados para
 * autodestruirse (marinos, barcos, aviones y cápsulas).
 *
 * <p>Cada elemento decide por sí mismo, según su propia regla, si debe
 * autodestruirse y puede informar la causa de esa decisión. La flota solo
 * conoce este contrato, por lo que no depende de los tipos concretos.</p>
 *
 * @author DOPO
 * @version 1.0
 */
public interface SelfDestructible {

    /** Causa informada cuando el elemento aún no ha decidido autodestruirse. */
    String NO_DECISION = "No ha tomado la decisión de autodestruirse";

    /** Causa informada cuando el elemento recibió la instrucción de autodestruirse. */
    String INSTRUCTION_CAUSE = "Recibió la instrucción de autodestruirse";

    /**
     * Evalúa la regla propia del elemento.
     *
     * @return {@code true} si el elemento decide autodestruirse
     */
    boolean mustSelfDestruct();

    /**
     * Informa la causa por la cual el elemento tomó la decisión.
     *
     * @return causa de la decisión, o {@link #NO_DECISION} si aún no hay decisión
     */
    String getSelfDestructCause();

    /**
     * Ejecuta la autodestrucción: el elemento queda destruido.
     */
    void selfDestruct();

    /**
     * Indica si el elemento ya fue destruido.
     *
     * @return {@code true} si el elemento está destruido
     */
    boolean isDestroyed();
}
