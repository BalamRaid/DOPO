package battleship;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Portaaviones: un barco con capacidad para transportar aviones.
 *
 * <p>Un portaaviones es débil si es un barco débil (tripulación insuficiente)
 * o si alguno de sus aviones que está en el aire es débil.</p>
 *
 * @author DOPO
 * @version 1.0
 */
public class AircraftCarrier extends Ship {

    private final int capacity;
    private final ArrayList<Plane> airPlanes;

    /**
     * Crea un portaaviones sin aviones asignados.
     *
     * @param number      número que identifica al portaaviones
     * @param location    posición inicial
     * @param firstSailor primer marino de la tripulación
     * @param capacity    cantidad máxima de aviones, debe ser positiva
     * @throws IllegalArgumentException si la capacidad no es positiva
     */
    public AircraftCarrier(int number, Position location, Sailor firstSailor, int capacity) {
        super(number, location, firstSailor);
        if (capacity <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser positiva");
        }
        this.capacity = capacity;
        this.airPlanes = new ArrayList<>();
    }

    /**
     * Consulta la capacidad.
     *
     * @return cantidad máxima de aviones que puede transportar
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Consulta los aviones asignados al portaaviones.
     *
     * @return vista de solo lectura de los aviones asignados
     */
    public List<Plane> getAirPlanes() {
        return Collections.unmodifiableList(airPlanes);
    }

    /**
     * Asigna un avión al portaaviones.
     *
     * @param plane avión a asignar
     * @throws BattleShipException si el portaaviones está lleno o el avión ya
     *                             está asignado
     */
    public void addPlane(Plane plane) throws BattleShipException {
        if (airPlanes.contains(plane)) {
            throw new BattleShipException(BattleShipException.PLANE_ALREADY_ASSIGNED);
        }
        if (airPlanes.size() >= capacity) {
            throw new BattleShipException(BattleShipException.CARRIER_FULL);
        }
        airPlanes.add(plane);
    }

    /**
     * Es débil si es un barco débil o si alguno de sus aviones en el aire
     * es débil.
     *
     * @return {@code true} si el portaaviones es débil
     */
    @Override
    public boolean isWeak() {
        return super.isWeak()
                || airPlanes.stream().anyMatch(plane -> plane.isInAir() && plane.isWeak());
    }

    /**
     * Valida que el piloto de cada avión asignado al portaaviones sea parte de
     * su tripulación.
     *
     * @throws BattleShipException si el piloto de algún avión asignado no es
     *                             marino del portaaviones
     */
    @Override
    public void validatePilots() throws BattleShipException {
        for (Plane plane : airPlanes) {
            for (Sailor pilot : plane.assignedPilots()) {
                if (!getSailors().contains(pilot)) {
                    throw new BattleShipException(BattleShipException.PILOT_NOT_IN_CARRIER);
                }
            }
        }
    }
}