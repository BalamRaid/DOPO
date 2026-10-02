package battleship;

/**
 * Excepción de dominio del juego "Batalla naval".
 *
 * <p>Se lanza cuando una operación viola las reglas del juego (por ejemplo,
 * agregar un avión a un portaaviones que ya no tiene capacidad). Los mensajes
 * se centralizan en constantes para evitar textos duplicados y facilitar las
 * pruebas.</p>
 *
 * @author DOPO
 * @version 1.0
 */
public class BattleShipException extends Exception {

    private static final long serialVersionUID = 1L;

    /** Mensaje cuando un portaaviones alcanzó su capacidad máxima. */
    public static final String CARRIER_FULL =
            "El portaaviones ha alcanzado su capacidad máxima";

    /** Mensaje cuando un avión ya fue asignado al portaaviones. */
    public static final String PLANE_ALREADY_ASSIGNED =
            "El avión ya está asignado al portaaviones";

    /** Mensaje cuando una máquina no puede moverse al norte. */
    public static final String CANNOT_MOVE =
            "La máquina no puede moverse al norte";

    /** Mensaje cuando un piloto no es marino de la flota. */
    public static final String PILOT_NOT_IN_FLEET =
            "Un piloto no es marino de la flota";

    /** Mensaje cuando el piloto de un avión del portaaviones no es de su tripulación. */
    public static final String PILOT_NOT_IN_CARRIER =
            "El piloto de un avión asignado al portaaviones no es marino del portaaviones";

    /** Mensaje cuando un piloto está asignado a más de un avión. */
    public static final String PILOT_IN_SEVERAL_PLANES =
            "Un piloto está asignado a más de un avión";

    /** Mensaje cuando la flota tiene menos marinos que máquinas con tripulación. */
    public static final String FEWER_SAILORS_THAN_MACHINES =
            "La flota tiene menos marinos que máquinas";

    /** Mensaje cuando una flota no tiene marinos asignados. */
    public static final String FLEET_WITHOUT_SAILORS =
            "Hay una flota sin marinos asignados";

    /** Mensaje cuando más de la mitad de las flotas tienen problemas de poder. */
    public static final String MOST_FLEETS_WITH_POWER_ISSUES =
            "Más de la mitad de las flotas tienen problemas de poder";

    /**
     * Crea una excepción con el mensaje indicado.
     *
     * @param message descripción del error ocurrido
     */
    public BattleShipException(String message) {
        super(message);
    }
}