package battleship;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Flota de máquinas de guerra (por ejemplo, "La Gran Flota Blanca").
 *
 * <p>La flota delega en cada {@link Machine} las reglas propias de su tipo
 * (si es débil, si puede ser destruida, cómo se mueve), por lo que no necesita
 * conocer las clases concretas de máquinas.</p>
 *
 * @author DOPO
 * @version 1.0
 */
public class Fleet {

    private final String name;
    private final ArrayList<Machine> machines;
    private final ArrayList<Sailor> sailors;
    private final ArrayList<SelfDestructible> selfDestroyed;
    private Board board;

    /**
     * Crea una flota sin máquinas.
     *
     * @param name nombre de la flota, no puede ser nulo
     */
    public Fleet(String name) {
        this.name = Objects.requireNonNull(name, "El nombre no puede ser nulo");
        this.machines = new ArrayList<>();
        this.sailors = new ArrayList<>();
        this.selfDestroyed = new ArrayList<>();
    }

    /**
     * Consulta el nombre.
     *
     * @return nombre de la flota
     */
    public String getName() {
        return name;
    }

    /**
     * Consulta las máquinas de la flota.
     *
     * @return vista de solo lectura de las máquinas
     */
    public List<Machine> getMachines() {
        return Collections.unmodifiableList(machines);
    }

    /**
     * Agrega una máquina a la flota.
     *
     * @param machine máquina a agregar, no puede ser nula
     */
    public void addMachine(Machine machine) {
        machines.add(Objects.requireNonNull(machine, "La máquina no puede ser nula"));
    }

    /**
     * Registra el tablero al que pertenece la flota. Lo invoca únicamente
     * {@link Board#addFleet(Fleet)} para mantener la asociación consistente.
     *
     * @param board tablero al que se incorpora la flota
     */
    void setBoard(Board board) {
        this.board = board;
    }

    /**
     * Mueve la flota una posición al norte. El tablero es circular. Si alguna
     * máquina no puede moverse, el movimiento de toda la flota se detiene:
     * las máquinas que ya se movieron conservan su nueva posición y las
     * restantes no se mueven.
     *
     * @throws BattleShipException si alguna máquina no pudo moverse al norte
     */
    public void moveNorth() throws BattleShipException {
        for (Machine machine : machines) {
            machine.moveNorth();
        }
    }

    /**
     * Mueve todas las máquinas la distancia indicada. El tablero es circular.
     *
     * @param dLon avance en longitud
     * @param dLat avance en latitud
     */
    public void advance(int dLon, int dLat) {
        machines.forEach(machine -> machine.advance(dLon, dLat));
    }

    /**
     * Consulta las máquinas que serían afectadas por una explosión en la
     * posición dada. Puede haber varias máquinas en una misma coordenada y los
     * aviones en el aire no se destruyen.
     *
     * @param longitude longitud de la explosión
     * @param latitude  latitud de la explosión
     * @return máquinas de la flota que serían destruidas
     */
    public ArrayList<Machine> willBeDestroyed(int longitude, int latitude) {
        Position explosion = new Position(longitude, latitude);
        return machines.stream()
                .filter(machine -> machine.isAt(explosion) && machine.canBeDestroyed())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Consulta las máquinas débiles de la flota. Cada tipo de máquina define
     * cuándo es débil.
     *
     * @return máquinas débiles
     */
    public ArrayList<Machine> weakMachines() {
        return machines.stream()
                .filter(Machine::isWeak)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Verifica si una posición de ataque es adecuada: destruye elementos
     * enemigos sin causar bajas propias.
     *
     * <p>Si la flota no pertenece a ningún tablero no conoce enemigos, por lo
     * que ningún ataque puede considerarse adecuado.</p>
     *
     * @param longitude longitud de la explosión
     * @param latitude  latitud de la explosión
     * @return {@code true} si destruye al menos una máquina enemiga y ninguna propia
     */
    public boolean isGoodAttack(int longitude, int latitude) {
        if (board == null) {
            return false;
        }
        return willBeDestroyed(longitude, latitude).isEmpty()
                && !board.enemiesAffectedAt(this, longitude, latitude).isEmpty();
    }

    /**
     * Mueve un paso hacia la posición de ataque todas las máquinas que no
     * son débiles.
     *
     * @param lon longitud del ataque
     * @param lat latitud del ataque
     */
    public void attack(int lon, int lat) {
        Position target = new Position(lon, lat);
        machines.stream()
                .filter(machine -> !machine.isWeak())
                .forEach(machine -> machine.moveTowards(target));
    }

    /**
     * Consulta los marinos de la flota.
     *
     * @return vista de solo lectura de los marinos
     */
    public List<Sailor> getSailors() {
        return Collections.unmodifiableList(sailors);
    }

    /**
     * Agrega un marino a la flota.
     *
     * @param sailor marino a agregar, no puede ser nulo
     */
    public void addSailor(Sailor sailor) {
        sailors.add(Objects.requireNonNull(sailor, "El marino no puede ser nulo"));
    }

    /**
     * Consulta todos los elementos de la flota que se han autodestruido.
     *
     * @return vista de solo lectura de los elementos autodestruidos
     */
    public List<SelfDestructible> getSelfDestroyed() {
        return Collections.unmodifiableList(selfDestroyed);
    }

    /**
     * Ejecuta las autodestrucciones de la flota. Cada elemento evalúa su
     * propia regla; los que deciden autodestruirse se destruyen, se retiran
     * de la flota y quedan registrados en {@link #getSelfDestroyed()}.
     *
     * <p>El proceso se repite hasta que ningún elemento nuevo decide
     * autodestruirse, de modo que las decisiones en cadena se propagan
     * (por ejemplo, un barco destruye a su cápsula y esta, a su vez, a la
     * cápsula que la tiene como nodriza).</p>
     *
     * @return elementos que se autodestruyeron en esta ejecución
     */
    public ArrayList<SelfDestructible> executeSelfDestructions() {
        ArrayList<SelfDestructible> destroyedNow = new ArrayList<>();
        ArrayList<SelfDestructible> pending = pendingSelfDestructions();
        while (!pending.isEmpty()) {
            for (SelfDestructible element : pending) {
                element.selfDestruct();
                // El elemento es un marino o una máquina: solo está en una de las dos listas
                machines.remove(element);
                sailors.remove(element);
            }
            destroyedNow.addAll(pending);
            pending = pendingSelfDestructions();
        }
        selfDestroyed.addAll(destroyedNow);
        return destroyedNow;
    }

    /**
     * Recolecta los elementos que en este momento deciden autodestruirse.
     */
    private ArrayList<SelfDestructible> pendingSelfDestructions() {
        ArrayList<SelfDestructible> pending = new ArrayList<>();
        machines.stream().filter(this::mustBeDestroyedNow).forEach(pending::add);
        sailors.stream().filter(this::mustBeDestroyedNow).forEach(pending::add);
        return pending;
    }

    private boolean mustBeDestroyedNow(SelfDestructible element) {
        return !element.isDestroyed() && element.mustSelfDestruct();
    }

    /**
     * Indica si el marino pertenece a la flota.
     *
     * @param sailor marino a consultar
     * @return {@code true} si el marino es de la flota
     */
    public boolean hasSailor(Sailor sailor) {
        return sailors.contains(sailor);
    }

    /**
     * Consulta los pilotos asignados a los aviones de la flota, sin validar
     * su consistencia. Un mismo marino aparece tantas veces como aviones pilotee.
     *
     * @return pilotos asignados a los aviones de la flota
     */
    public ArrayList<Sailor> assignedPilots() {
        return machines.stream()
                .flatMap(machine -> machine.assignedPilots().stream())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Consulta los pilotos de la flota validando que sean consistentes.
     *
     * @return pilotos asignados a los aviones de la flota
     * @throws BattleShipException si un piloto no es marino de la flota, si el
     *                             piloto de un avión de un portaaviones no es
     *                             marino del portaaviones, o si un piloto está
     *                             asignado a más de un avión
     */
    public ArrayList<Sailor> pilots() throws BattleShipException {
        for (Machine machine : machines) {
            machine.validatePilots();
        }
        ArrayList<Sailor> pilots = new ArrayList<>();
        for (Sailor pilot : assignedPilots()) {
            if (!sailors.contains(pilot)) {
                throw new BattleShipException(BattleShipException.PILOT_NOT_IN_FLEET);
            }
            if (pilots.contains(pilot)) {
                throw new BattleShipException(BattleShipException.PILOT_IN_SEVERAL_PLANES);
            }
            pilots.add(pilot);
        }
        return pilots;
    }

    /**
     * Consulta el poder de la flota: la cantidad de máquinas que no son
     * débiles.
     *
     * @return poder de la flota
     * @throws BattleShipException si la flota tiene menos marinos que máquinas
     *                             que requieren tripulación
     */
    public int power() throws BattleShipException {
        long crewedMachines = machines.stream().filter(Machine::needsSailors).count();
        if (sailors.size() < crewedMachines) {
            throw new BattleShipException(BattleShipException.FEWER_SAILORS_THAN_MACHINES);
        }
        return (int) machines.stream().filter(machine -> !machine.isWeak()).count();
    }
}