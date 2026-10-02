package battleship;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Máquina de guerra que pertenece a una flota y ocupa una posición del tablero.
 *
 * <p>Es la raíz de la jerarquía. Define el comportamiento común (ubicación y
 * movimiento) y deja a cada subclase decidir, mediante polimorfismo, cuándo
 * es débil ({@link #isWeak()}) y cuándo puede ser destruida
 * ({@link #canBeDestroyed()}). Así, las flotas no necesitan conocer los tipos
 * concretos de máquinas y se pueden agregar nuevos tipos sin modificarlas
 * (principio abierto/cerrado).</p>
 *
 * @author DOPO
 * @version 1.0
 */
public abstract class Machine implements SelfDestructible {

    private Position location;
    private boolean instructed;
    private boolean destroyed;

    /**
     * Crea una máquina en la posición indicada.
     *
     * @param location posición inicial, no puede ser nula
     */
    protected Machine(Position location) {
        this.location = Objects.requireNonNull(location, "La posición no puede ser nula");
    }

    /**
     * Consulta la posición actual.
     *
     * @return posición actual de la máquina
     */
    public Position getLocation() {
        return location;
    }

    /**
     * Desplaza la máquina la distancia indicada. El tablero es circular.
     *
     * @param dLon avance en longitud
     * @param dLat avance en latitud
     */
    public void advance(int dLon, int dLat) {
        location = location.advance(dLon, dLat);
    }

    /**
     * Da un paso hacia la posición objetivo por el camino más corto.
     *
     * @param target posición hacia la cual se mueve la máquina
     */
    public void moveTowards(Position target) {
        location = location.stepTowards(target);
    }

    /**
     * Indica si la máquina está exactamente en la posición dada.
     *
     * @param position posición a consultar
     * @return {@code true} si la máquina ocupa esa posición
     */
    public boolean isAt(Position position) {
        return location.equals(position);
    }

    /**
     * Indica si la máquina es afectada por una explosión en su posición.
     * Por defecto todas las máquinas son destruibles; las subclases
     * inmunes (aviones en el aire) lo sobrescriben.
     *
     * @return {@code true} si una explosión en su posición la destruiría
     */
    public boolean canBeDestroyed() {
        return true;
    }

    /**
     * Indica si la máquina está en condiciones de moverse. Por defecto
     * siempre puede; las subclases con restricciones lo sobrescriben.
     *
     * @return {@code true} si la máquina puede moverse
     */
    public boolean canMove() {
        return true;
    }

    /**
     * Mueve la máquina una posición al norte. El tablero es circular.
     *
     * @throws BattleShipException si la máquina no puede moverse
     */
    public void moveNorth() throws BattleShipException {
        if (!canMove()) {
            throw new BattleShipException(BattleShipException.CANNOT_MOVE);
        }
        advance(0, 1);
    }

    /**
     * Indica si la máquina necesita marinos para operar. Por defecto sí;
     * las máquinas sin tripulantes (como {@link Capsule}) lo sobrescriben.
     *
     * @return {@code true} si la máquina requiere tripulación
     */
    public boolean needsSailors() {
        return true;
    }

    /**
     * Consulta los pilotos que la máquina tiene asignados. Por defecto no
     * tiene ninguno; {@link Plane} lo sobrescribe.
     *
     * @return pilotos asignados a la máquina
     */
    public List<Sailor> assignedPilots() {
        return Collections.emptyList();
    }

    /**
     * Valida que los pilotos asignados a la máquina sean consistentes con su
     * tripulación. Por defecto no hay nada que validar;
     * {@link AircraftCarrier} lo sobrescribe.
     *
     * @throws BattleShipException si algún piloto asignado no es válido
     */
    public void validatePilots() throws BattleShipException {
        // Por defecto no hay pilotos que validar
    }

    /**
     * Indica si la máquina es débil según las reglas propias de su tipo.
     *
     * @return {@code true} si la máquina es débil
     */
    public abstract boolean isWeak();

    /**
     * Le comunica a la máquina la instrucción de autodestruirse. Los tipos
     * cuya regla no depende de la instrucción (como {@link Capsule}) la
     * ignoran al decidir.
     */
    public void receiveSelfDestructInstruction() {
        instructed = true;
    }

    /**
     * Regla por defecto: la máquina se autodestruye si recibió la instrucción.
     *
     * @return {@code true} si recibió la instrucción de autodestruirse
     */
    @Override
    public boolean mustSelfDestruct() {
        return instructed;
    }

    /**
     * Causa por defecto de la decisión.
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
}