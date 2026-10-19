package domain;

/**
 * Excepción propia del dominio de NeoFlix.
 * <p>
 * Encapsula, mediante constantes, los mensajes de las condiciones de error
 * esperadas (reglas de negocio y datos) para que puedan compararse sin
 * ambigüedad con {@link #getMessage()} y reutilizarse en todas las capas.
 * Es una excepción verificada ({@code extends Exception}): el compilador obliga
 * a quien la invoca a declararla o atenderla, lo cual es adecuado para
 * condiciones recuperables.
 */
public class NeoFlixException extends Exception {

    private static final long serialVersionUID = 1L;

    /** Valor desconocido: el indicador aún no se ha medido (p. ej. sin intentos o sin votos). */
    public static final String VALUE_UNKNOWN = "Valor desconocido";

    /** Error en datos: los datos almacenados violan un invariante del dominio. */
    public static final String DATA_ERROR = "Error en datos";

    /** Contenido vacío: la operación requiere al menos un componente (texto literal del material original del curso). */
    public static final String CONTENT_EMPTY = "VAcio";

    /** Por implementar: operación fuera del alcance de esta versión. */
    public static final String TO_BE_IMPLEMENTED = "Por implementar";

    /** Título vacío: el nombre de un contenido nuevo no puede quedar en blanco. */
    public static final String EMPTY_TITLE = "El nombre no puede estar vacío";

    /** Contenido existente: ya hay un contenido con ese nombre en el catálogo. */
    public static final String CONTENT_EXISTS = "Ya existe un contenido con ese nombre";

    /** Valor no numérico: se esperaba un número y el texto no lo representa. */
    public static final String NOT_NUMERIC = "Se esperaba un valor numérico";

    /** Valor negativo: los datos de medición no admiten números negativos. */
    public static final String NEGATIVE_VALUE = "No se admiten valores negativos";

    /** Terminados inválidos: las visualizaciones completas superan los intentos. */
    public static final String INVALID_COMPLETED = "Los terminados superan los intentos";

    /** Votos inválidos: la suma de votos supera diez veces el número de votos. */
    public static final String INVALID_VOTES = "La suma de votos supera 10 veces los votos";

    /** Episodio no encontrado: la serie referencia un episodio que no existe. */
    public static final String EPISODE_NOT_FOUND = "Un episodio de la serie no existe";

    /**
     * Crea la excepción con el mensaje indicado (normalmente una de las constantes).
     *
     * @param message mensaje de la condición de error
     */
    public NeoFlixException(String message) {
        super(message);
    }

    /**
     * Crea la excepción traduciendo una causa de más bajo nivel, que se conserva
     * para la trazabilidad (p. ej. NumberFormatException -> NOT_NUMERIC).
     *
     * @param message mensaje de la condición de error
     * @param cause   causa original que se conserva
     */
    public NeoFlixException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Indica si esta excepción fue lanzada por la razón indicada.
     *
     * @param reason una de las constantes de mensaje de esta clase
     * @return {@code true} si el mensaje actual coincide exactamente con la razón
     */
    public boolean is(String reason) {
        return reason.equals(getMessage());
    }
}
