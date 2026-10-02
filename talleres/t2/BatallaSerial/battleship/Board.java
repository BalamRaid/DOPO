package battleship;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Tablero del juego. Agrupa las flotas que se enfrentan y permite a cada una
 * consultar la situación de sus enemigas.
 *
 * @author DOPO
 * @version 1.0
 */
public class Board {

    private final ArrayList<Fleet> fleets;

    /**
     * Crea un tablero sin flotas.
     */
    public Board() {
        this.fleets = new ArrayList<>();
    }

    /**
     * Consulta las flotas del tablero.
     *
     * @return vista de solo lectura de las flotas
     */
    public List<Fleet> getFleets() {
        return Collections.unmodifiableList(fleets);
    }

    /**
     * Incorpora una flota al tablero y la asocia con él.
     *
     * @param fleet flota a incorporar, no puede ser nula
     */
    public void addFleet(Fleet fleet) {
        Objects.requireNonNull(fleet, "La flota no puede ser nula");
        fleets.add(fleet);
        fleet.setBoard(this);
    }

    /**
     * Mueve todas las flotas al norte y cuenta las que completaron el
     * movimiento. Una flota cuyo movimiento se detuvo por una excepción no se
     * cuenta, pero no impide que las demás se muevan.
     *
     * @return cantidad de flotas que completaron el movimiento
     */
    public int toNorth() {
        int completed = 0;
        for (Fleet fleet : fleets) {
            try {
                fleet.moveNorth();
                completed++;
            } catch (BattleShipException e) {
                // El movimiento de esta flota se detuvo: no se cuenta como completo
            }
        }
        return completed;
    }

    /**
     * Consulta las flotas que tienen pilotos infiltrados: un piloto de un
     * avión de la flota que pertenece a la tripulación de otra flota.
     *
     * @return flotas con pilotos infiltrados
     * @throws BattleShipException si alguna flota no tiene marinos asignados
     */
    public ArrayList<Fleet> infiltrated() throws BattleShipException {
        for (Fleet fleet : fleets) {
            if (fleet.getSailors().isEmpty()) {
                throw new BattleShipException(BattleShipException.FLEET_WITHOUT_SAILORS);
            }
        }
        return fleets.stream()
                .filter(this::hasInfiltratedPilots)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Consulta el poder del tablero: la suma del poder de sus flotas. Una
     * flota con problemas de poder no aporta al total.
     *
     * @return poder del tablero
     * @throws BattleShipException si más de la mitad de las flotas tienen
     *                             problemas de poder
     */
    public int power() throws BattleShipException {
        int total = 0;
        int fleetsWithIssues = 0;
        for (Fleet fleet : fleets) {
            try {
                total += fleet.power();
            } catch (BattleShipException e) {
                fleetsWithIssues++;
            }
        }
        if (fleetsWithIssues * 2 > fleets.size()) {
            throw new BattleShipException(BattleShipException.MOST_FLEETS_WITH_POWER_ISSUES);
        }
        return total;
    }

    /**
     * Una flota tiene pilotos infiltrados si alguno de sus pilotos no es de
     * su tripulación pero sí pertenece a otra flota del tablero.
     */
    private boolean hasInfiltratedPilots(Fleet fleet) {
        return fleet.assignedPilots().stream()
                .anyMatch(pilot -> !fleet.hasSailor(pilot) && belongsToAnotherFleet(pilot, fleet));
    }

    private boolean belongsToAnotherFleet(Sailor sailor, Fleet fleet) {
        return fleets.stream().anyMatch(other -> other != fleet && other.hasSailor(sailor));
    }

    /**
     * Consulta las máquinas enemigas que serían destruidas por una explosión.
     * Son enemigas todas las de las flotas distintas a la indicada.
     *
     * @param fleet     flota que realiza el ataque
     * @param longitude longitud de la explosión
     * @param latitude  latitud de la explosión
     * @return máquinas de las demás flotas que serían destruidas
     */
    ArrayList<Machine> enemiesAffectedAt(Fleet fleet, int longitude, int latitude) {
        return fleets.stream()
                .filter(other -> other != fleet)
                .flatMap(other -> other.willBeDestroyed(longitude, latitude).stream())
                .collect(Collectors.toCollection(ArrayList::new));
    }
}