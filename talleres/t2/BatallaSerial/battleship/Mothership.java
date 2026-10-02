package battleship;

/**
 * Máquina nodriza: puede dar instrucciones a las cápsulas submarinas.
 *
 * <p>Las nodrizas pueden ser un {@link Ship} o una {@link Capsule}. Como
 * ambas son tipos de máquina sin más relación entre sí, se modela el rol con
 * una interfaz. La cápsula depende de este contrato y no de los tipos
 * concretos (inversión de dependencias).</p>
 *
 * <p>Extiende {@link SelfDestructible} porque las cápsulas necesitan saber
 * si su nodriza fue destruida.</p>
 *
 * @author DOPO
 * @version 1.0
 */
public interface Mothership extends SelfDestructible {

    /**
     * Entrega la instrucción solicitada por una cápsula.
     *
     * @return texto de la instrucción
     */
    String giveInstruction();
}
