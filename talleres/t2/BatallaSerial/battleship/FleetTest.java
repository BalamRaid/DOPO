package battleship;

import static battleship.TestFixtures.addCapsule;
import static battleship.TestFixtures.addPlane;
import static battleship.TestFixtures.addShip;
import static battleship.TestFixtures.carrierWithCrew;
import static battleship.TestFixtures.registerCrew;
import static battleship.TestFixtures.sailor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de {@link Fleet}: métodos de consulta y movimiento (parte 1) y
 * métodos con excepciones {@code moveNorth}, {@code pilots} y {@code power}
 * (parte 4).
 *
 * @author DOPO
 * @version 1.0
 */
@DisplayName("Fleet")
class FleetTest {

    private static final Position ORIGIN = new Position(0, 0);

    private Fleet fleet;

    @BeforeEach
    void setUp() {
        fleet = new Fleet("LA GRAN ARMADA DE CASTILLA");
    }

    // ------------------------------------------------------------------
    // Construcción y acceso
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Una flota nueva tiene nombre y no tiene máquinas ni marinos")
    public void newFleetIsEmpty() {
        assertEquals("LA GRAN ARMADA DE CASTILLA", fleet.getName());
        assertTrue(fleet.getMachines().isEmpty());
        assertTrue(fleet.getSailors().isEmpty());
        assertTrue(fleet.getSelfDestroyed().isEmpty());
    }

    @Test
    @DisplayName("No acepta nombre, máquinas ni marinos nulos")
    void rejectsNulls() {
        assertThrows(NullPointerException.class, () -> new Fleet(null));
        assertThrows(NullPointerException.class, () -> fleet.addMachine(null));
        assertThrows(NullPointerException.class, () -> fleet.addSailor(null));
    }

    @Test
    @DisplayName("Las listas expuestas son de solo lectura")
    void exposedListsAreUnmodifiable() {
        assertThrows(UnsupportedOperationException.class,
                () -> fleet.getMachines().add(new Capsule(ORIGIN, TestFixtures.shipWithCrew(1, ORIGIN, 1))));
        assertThrows(UnsupportedOperationException.class,
                () -> fleet.getSailors().add(sailor("intruso")));
    }

    @Test
    @DisplayName("hasSailor distingue a los marinos de la flota de los ajenos")
    void hasSailorDistinguishesMembers() {
        Sailor member = sailor("miembro");
        fleet.addSailor(member);
        assertTrue(fleet.hasSailor(member));
        assertFalse(fleet.hasSailor(sailor("miembro")));
    }

    // ------------------------------------------------------------------
    // advance
    // ------------------------------------------------------------------

    @Test
    @DisplayName("advance mueve todas las máquinas la misma distancia")
    void advanceMovesAllMachines() {
        Ship ship = addShip(fleet, 1, new Position(10, 10), 1);
        Plane plane = addPlane(fleet, "A", new Position(50, -20), null, false);
        Capsule capsule = addCapsule(fleet, new Position(0, 0), ship);

        fleet.advance(5, 3);

        assertEquals(new Position(15, 13), ship.getLocation());
        assertEquals(new Position(55, -17), plane.getLocation());
        assertEquals(new Position(5, 3), capsule.getLocation());
    }

    @Test
    @DisplayName("advance respeta el tablero circular")
    void advanceWrapsAroundBoard() {
        Ship ship = addShip(fleet, 1, new Position(180, 90), 1);
        fleet.advance(1, 1);
        assertEquals(new Position(0, -90), ship.getLocation());
    }

    @Test
    @DisplayName("advance en una flota vacía no falla")
    void advanceOnEmptyFleet() {
        fleet.advance(1, 1);
        assertTrue(fleet.getMachines().isEmpty());
    }

    // ------------------------------------------------------------------
    // willBeDestroyed
    // ------------------------------------------------------------------

    @Test
    @DisplayName("willBeDestroyed devuelve solo las máquinas ubicadas en esa coordenada")
    void willBeDestroyedReturnsMachinesAtPosition() {
        Ship here = addShip(fleet, 1, new Position(10, 10), 1);
        Ship elsewhere = addShip(fleet, 2, new Position(11, 10), 1);

        ArrayList<Machine> result = fleet.willBeDestroyed(10, 10);

        assertEquals(1, result.size());
        assertTrue(result.contains(here));
        assertFalse(result.contains(elsewhere));
    }

    @Test
    @DisplayName("willBeDestroyed devuelve todas las máquinas que comparten la coordenada")
    void willBeDestroyedReturnsManyMachinesAtSamePosition() {
        Position spot = new Position(10, 10);
        addShip(fleet, 1, spot, 1);
        addShip(fleet, 2, spot, 1);
        addPlane(fleet, "A", spot, null, false);

        assertEquals(3, fleet.willBeDestroyed(10, 10).size());
    }

    @Test
    @DisplayName("Los aviones en el aire no se destruyen, los que están en tierra sí")
    void willBeDestroyedIgnoresAirbornePlanes() {
        Position spot = new Position(10, 10);
        Plane flying = addPlane(fleet, "AIR", spot, sailor("p1"), true);
        Plane grounded = addPlane(fleet, "GRD", spot, sailor("p2"), false);

        ArrayList<Machine> result = fleet.willBeDestroyed(10, 10);

        assertFalse(result.contains(flying));
        assertTrue(result.contains(grounded));
    }

    @Test
    @DisplayName("Las cápsulas son inmunes a las explosiones")
    void willBeDestroyedIgnoresCapsules() {
        Position spot = new Position(10, 10);
        Ship ship = addShip(fleet, 1, spot, 1);
        Capsule capsule = addCapsule(fleet, spot, ship);

        ArrayList<Machine> result = fleet.willBeDestroyed(10, 10);

        assertTrue(result.contains(ship));
        assertFalse(result.contains(capsule));
    }

    @Test
    @DisplayName("Sin máquinas en la coordenada la lista es vacía")
    void willBeDestroyedIsEmptyWhenNothingThere() {
        addShip(fleet, 1, new Position(10, 10), 1);
        assertTrue(fleet.willBeDestroyed(99, 99).isEmpty());
    }

    @Test
    @DisplayName("Las coordenadas de la explosión también se normalizan al tablero circular")
    void willBeDestroyedNormalizesCoordinates() {
        Ship ship = addShip(fleet, 1, new Position(0, 0), 1);
        assertTrue(fleet.willBeDestroyed(181, 181).contains(ship));
    }

    // ------------------------------------------------------------------
    // weakMachines
    // ------------------------------------------------------------------

    @Test
    @DisplayName("weakMachines identifica barcos, aviones y portaaviones débiles")
    void weakMachinesIdentifiesEachType() throws BattleShipException {
        Ship weakShip = addShip(fleet, 1, ORIGIN, 4);
        Ship strongShip = addShip(fleet, 2, ORIGIN, 5);
        Plane weakPlane = addPlane(fleet, "W", ORIGIN, null, false);
        Plane strongPlane = addPlane(fleet, "S", ORIGIN, sailor("p"), false);
        AircraftCarrier weakCarrier = carrierWithCrew(3, ORIGIN, 4, 1);
        fleet.addMachine(weakCarrier);
        AircraftCarrier strongCarrier = carrierWithCrew(4, ORIGIN, 5, 1);
        fleet.addMachine(strongCarrier);

        ArrayList<Machine> weak = fleet.weakMachines();

        assertEquals(3, weak.size());
        assertTrue(weak.contains(weakShip));
        assertTrue(weak.contains(weakPlane));
        assertTrue(weak.contains(weakCarrier));
        assertFalse(weak.contains(strongShip));
        assertFalse(weak.contains(strongPlane));
        assertFalse(weak.contains(strongCarrier));
    }

    @Test
    @DisplayName("Un portaaviones fuerte es débil si uno de sus aviones en el aire es débil")
    void carrierIsWeakThroughAirbornePlane() throws BattleShipException {
        AircraftCarrier carrier = carrierWithCrew(1, ORIGIN, 5, 1);
        Plane plane = TestFixtures.plane("A", ORIGIN, null, true);
        carrier.addPlane(plane);
        fleet.addMachine(carrier);
        fleet.addMachine(plane);

        ArrayList<Machine> weak = fleet.weakMachines();

        assertTrue(weak.contains(carrier));
        assertTrue(weak.contains(plane));
    }

    @Test
    @DisplayName("Las cápsulas nunca aparecen como débiles")
    void weakMachinesNeverContainsCapsules() {
        Ship ship = addShip(fleet, 1, ORIGIN, 1);
        Capsule capsule = addCapsule(fleet, ORIGIN, ship);
        assertFalse(fleet.weakMachines().contains(capsule));
    }

    @Test
    @DisplayName("Una flota sin máquinas débiles devuelve una lista vacía")
    void weakMachinesEmptyWhenAllStrong() {
        addShip(fleet, 1, ORIGIN, 5);
        assertTrue(fleet.weakMachines().isEmpty());
    }

    // ------------------------------------------------------------------
    // attack
    // ------------------------------------------------------------------

    @Test
    @DisplayName("attack mueve un paso hacia el objetivo solo a las máquinas que no son débiles")
    void attackMovesOnlyStrongMachines() {
        Ship strong = addShip(fleet, 1, new Position(10, 10), 5);
        Ship weak = addShip(fleet, 2, new Position(10, 10), 1);

        fleet.attack(20, 10);

        assertEquals(new Position(11, 10), strong.getLocation());
        assertEquals(new Position(10, 10), weak.getLocation());
    }

    @Test
    @DisplayName("attack avanza en diagonal cuando el objetivo difiere en ambos ejes")
    void attackMovesDiagonally() {
        Ship strong = addShip(fleet, 1, new Position(10, 10), 5);
        fleet.attack(5, 20);
        assertEquals(new Position(9, 11), strong.getLocation());
    }

    @Test
    @DisplayName("attack usa el camino más corto a través del borde del tablero")
    void attackUsesShortestCircularPath() {
        Ship strong = addShip(fleet, 1, new Position(1, 0), 5);
        fleet.attack(179, 0);
        assertEquals(new Position(0, 0), strong.getLocation());
    }

    @Test
    @DisplayName("Una máquina que ya está en el objetivo no se mueve")
    void attackKeepsMachineAtTarget() {
        Ship strong = addShip(fleet, 1, new Position(30, 30), 5);
        fleet.attack(30, 30);
        assertEquals(new Position(30, 30), strong.getLocation());
    }

    @Test
    @DisplayName("Las cápsulas participan en el ataque porque nunca son débiles")
    void attackMovesCapsules() {
        Ship ship = addShip(fleet, 1, new Position(10, 10), 1);
        Capsule capsule = addCapsule(fleet, new Position(10, 10), ship);

        fleet.attack(20, 20);

        assertEquals(new Position(11, 11), capsule.getLocation());
        assertEquals(new Position(10, 10), ship.getLocation());
    }

    @Test
    @DisplayName("Repetir attack acerca las máquinas hasta llegar al objetivo")
    void repeatedAttackReachesTarget() {
        Ship strong = addShip(fleet, 1, new Position(5, 5), 5);
        for (int i = 0; i < 10; i++) {
            fleet.attack(9, 2);
        }
        assertEquals(new Position(9, 2), strong.getLocation());
    }

    // ------------------------------------------------------------------
    // isGoodAttack
    // ------------------------------------------------------------------

    /** Crea un tablero con la flota bajo prueba y una flota enemiga. */
    private Fleet boardWithEnemy() {
        Board board = new Board();
        Fleet enemy = new Fleet("LA GRAN FLOTA BLANCA");
        board.addFleet(fleet);
        board.addFleet(enemy);
        return enemy;
    }

    @Test
    @DisplayName("Es un buen ataque si destruye enemigos y ninguna baja propia")
    void goodAttackDestroysEnemyWithoutOwnCasualties() {
        Fleet enemy = boardWithEnemy();
        addShip(fleet, 1, new Position(50, 0), 1);
        addShip(enemy, 2, new Position(100, 0), 1);

        assertTrue(fleet.isGoodAttack(100, 0));
    }

    @Test
    @DisplayName("No es un buen ataque si además alcanza una máquina propia")
    void notGoodIfOwnMachineIsHit() {
        Fleet enemy = boardWithEnemy();
        addShip(fleet, 1, new Position(100, 0), 1);
        addShip(enemy, 2, new Position(100, 0), 1);

        assertFalse(fleet.isGoodAttack(100, 0));
    }

    @Test
    @DisplayName("No es un buen ataque si no hay enemigos en la posición")
    void notGoodIfNoEnemyThere() {
        boardWithEnemy();
        addShip(fleet, 1, new Position(50, 0), 1);

        assertFalse(fleet.isGoodAttack(100, 0));
        assertFalse(fleet.isGoodAttack(50, 0));
    }

    @Test
    @DisplayName("Un enemigo en el aire o una cápsula enemiga no cuentan como destruidos")
    void immuneEnemiesDoNotMakeAGoodAttack() {
        Fleet enemy = boardWithEnemy();
        Position spot = new Position(100, 0);
        Ship enemyShip = TestFixtures.shipWithCrew(9, new Position(0, 0), 1);
        addPlane(enemy, "AIR", spot, sailor("p"), true);
        addCapsule(enemy, spot, enemyShip);

        assertFalse(fleet.isGoodAttack(100, 0));
    }

    @Test
    @DisplayName("Un avión propio en el aire o una cápsula propia no cuentan como bajas propias")
    void immuneOwnMachinesAreNotCasualties() {
        Fleet enemy = boardWithEnemy();
        Position spot = new Position(100, 0);
        Ship ownShip = addShip(fleet, 1, new Position(0, 0), 1);
        addPlane(fleet, "AIR", spot, sailor("p"), true);
        addCapsule(fleet, spot, ownShip);
        addShip(enemy, 2, spot, 1);

        assertTrue(fleet.isGoodAttack(100, 0));
    }

    @Test
    @DisplayName("Los enemigos pueden ser de cualquier otra flota del tablero")
    void enemiesCanBelongToAnyOtherFleet() {
        Board board = new Board();
        Fleet second = new Fleet("segunda");
        Fleet third = new Fleet("tercera");
        board.addFleet(fleet);
        board.addFleet(second);
        board.addFleet(third);
        addShip(third, 1, new Position(100, 0), 1);

        assertTrue(fleet.isGoodAttack(100, 0));
    }

    @Test
    @DisplayName("Una flota que no pertenece a ningún tablero no puede evaluar ataques")
    void fleetWithoutBoardHasNoGoodAttack() {
        addShip(fleet, 1, new Position(100, 0), 1);
        assertFalse(fleet.isGoodAttack(50, 0));
        assertFalse(fleet.isGoodAttack(100, 0));
    }

    // ------------------------------------------------------------------
    // moveNorth (con excepción)
    // ------------------------------------------------------------------

    @Test
    @DisplayName("moveNorth mueve todas las máquinas una posición al norte")
    void moveNorthMovesAllMachines() throws BattleShipException {
        Ship ship = addShip(fleet, 1, new Position(10, 10), 1);
        Plane plane = addPlane(fleet, "A", new Position(50, 20), sailor("p"), true);
        Capsule capsule = addCapsule(fleet, new Position(0, 0), ship);

        fleet.moveNorth();

        assertEquals(new Position(10, 11), ship.getLocation());
        assertEquals(new Position(50, 21), plane.getLocation());
        assertEquals(new Position(0, 1), capsule.getLocation());
    }

    @Test
    @DisplayName("moveNorth desde la latitud máxima da la vuelta al tablero")
    void moveNorthWrapsAroundBoard() throws BattleShipException {
        Ship ship = addShip(fleet, 1, new Position(10, 90), 1);
        fleet.moveNorth();
        assertEquals(new Position(10, -90), ship.getLocation());
    }

    @Test
    @DisplayName("moveNorth en una flota vacía no lanza excepción")
    void moveNorthOnEmptyFleet() throws BattleShipException {
        fleet.moveNorth();
    }

    @Test
    @DisplayName("Si una máquina no puede moverse, lanza excepción y el movimiento se detiene ahí")
    void moveNorthStopsAtFirstMachineThatCannotMove() {
        Ship before = addShip(fleet, 1, new Position(10, 10), 1);
        Plane stuck = addPlane(fleet, "STUCK", new Position(20, 20), null, true);
        Ship after = addShip(fleet, 2, new Position(30, 30), 1);

        BattleShipException error = assertThrows(BattleShipException.class, fleet::moveNorth);

        assertEquals(BattleShipException.CANNOT_MOVE, error.getMessage());
        assertEquals(new Position(10, 11), before.getLocation());
        assertEquals(new Position(20, 20), stuck.getLocation());
        assertEquals(new Position(30, 30), after.getLocation());
    }

    @Test
    @DisplayName("Un barco cuyos marinos se autodestruyeron impide el movimiento de la flota")
    void shipWithoutSailorsBlocksFleet() {
        Ship ship = addShip(fleet, 1, new Position(10, 10), 1);
        ship.getSailors().get(0).selfDestruct();

        assertThrows(BattleShipException.class, fleet::moveNorth);
        assertEquals(new Position(10, 10), ship.getLocation());
    }

    // ------------------------------------------------------------------
    // pilots
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Una flota sin aviones no tiene pilotos")
    void pilotsIsEmptyWithoutPlanes() throws BattleShipException {
        addShip(fleet, 1, ORIGIN, 3);
        assertTrue(fleet.pilots().isEmpty());
    }

    @Test
    @DisplayName("pilots devuelve los pilotos de todos los aviones")
    void pilotsReturnsAllPilots() throws BattleShipException {
        Sailor first = sailor("p1");
        Sailor second = sailor("p2");
        fleet.addSailor(first);
        fleet.addSailor(second);
        addPlane(fleet, "A", ORIGIN, first, true);
        addPlane(fleet, "B", ORIGIN, second, false);

        ArrayList<Sailor> pilots = fleet.pilots();

        assertEquals(2, pilots.size());
        assertTrue(pilots.contains(first));
        assertTrue(pilots.contains(second));
    }

    @Test
    @DisplayName("Los aviones sin piloto se ignoran")
    void pilotsIgnoresPlanesWithoutPilot() throws BattleShipException {
        Sailor pilot = sailor("p1");
        fleet.addSailor(pilot);
        addPlane(fleet, "A", ORIGIN, pilot, true);
        addPlane(fleet, "B", ORIGIN, null, true);

        assertEquals(1, fleet.pilots().size());
    }

    @Test
    @DisplayName("Lanza excepción si un piloto no es marino de la flota")
    void pilotsRejectsPilotOutsideFleet() {
        fleet.addSailor(sailor("otro"));
        addPlane(fleet, "A", ORIGIN, sailor("ajeno"), true);

        BattleShipException error = assertThrows(BattleShipException.class, fleet::pilots);

        assertEquals(BattleShipException.PILOT_NOT_IN_FLEET, error.getMessage());
    }

    @Test
    @DisplayName("Lanza excepción si un mismo piloto está asignado a más de un avión")
    void pilotsRejectsPilotInSeveralPlanes() {
        Sailor pilot = sailor("p1");
        fleet.addSailor(pilot);
        addPlane(fleet, "A", ORIGIN, pilot, true);
        addPlane(fleet, "B", ORIGIN, pilot, false);

        BattleShipException error = assertThrows(BattleShipException.class, fleet::pilots);

        assertEquals(BattleShipException.PILOT_IN_SEVERAL_PLANES, error.getMessage());
    }

    @Test
    @DisplayName("Lanza excepción si el piloto de un avión del portaaviones no es de su tripulación")
    void pilotsRejectsPilotOutsideCarrierCrew() throws BattleShipException {
        AircraftCarrier carrier = carrierWithCrew(1, ORIGIN, 2, 1);
        fleet.addMachine(carrier);
        registerCrew(fleet, carrier);
        Sailor pilot = sailor("de-la-flota-pero-no-del-portaaviones");
        fleet.addSailor(pilot); // es marino de la flota, pero no del portaaviones
        Plane plane = TestFixtures.plane("A", ORIGIN, pilot, true);
        carrier.addPlane(plane);
        fleet.addMachine(plane);

        BattleShipException error = assertThrows(BattleShipException.class, fleet::pilots);

        assertEquals(BattleShipException.PILOT_NOT_IN_CARRIER, error.getMessage());
    }

    @Test
    @DisplayName("Acepta el piloto de un avión del portaaviones si es de su tripulación")
    void pilotsAcceptsCarrierCrewPilot() throws BattleShipException {
        AircraftCarrier carrier = carrierWithCrew(1, ORIGIN, 2, 1);
        fleet.addMachine(carrier);
        registerCrew(fleet, carrier);
        Sailor pilot = carrier.getSailors().get(1);
        Plane plane = TestFixtures.plane("A", ORIGIN, pilot, true);
        carrier.addPlane(plane);
        fleet.addMachine(plane);

        ArrayList<Sailor> pilots = fleet.pilots();

        assertEquals(1, pilots.size());
        assertSame(pilot, pilots.get(0));
    }

    @Test
    @DisplayName("Un piloto autodestruido no cuenta como piloto asignado ni provoca error")
    void pilotsIgnoresDestroyedPilot() throws BattleShipException {
        Sailor pilot = sailor("p1");
        fleet.addSailor(pilot);
        addPlane(fleet, "A", ORIGIN, pilot, true);
        pilot.selfDestruct();

        assertTrue(fleet.pilots().isEmpty());
    }

    @Test
    @DisplayName("assignedPilots no valida: repite al piloto que maneja varios aviones")
    void assignedPilotsDoesNotValidate() {
        Sailor pilot = sailor("p1");
        addPlane(fleet, "A", ORIGIN, pilot, true);
        addPlane(fleet, "B", ORIGIN, pilot, true);

        assertEquals(2, fleet.assignedPilots().size());
    }

    // ------------------------------------------------------------------
    // power
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Una flota vacía tiene poder cero")
    void powerOfEmptyFleetIsZero() throws BattleShipException {
        assertEquals(0, fleet.power());
    }

    @Test
    @DisplayName("El poder es la cantidad de máquinas que no son débiles")
    void powerCountsNonWeakMachines() throws BattleShipException {
        addShip(fleet, 1, ORIGIN, 5); // fuerte
        addShip(fleet, 2, ORIGIN, 1); // débil
        addPlane(fleet, "A", ORIGIN, sailor("p"), false); // fuerte
        fleet.addSailor(sailor("piloto-registrado"));

        assertEquals(2, fleet.power());
    }

    @Test
    @DisplayName("Lanza excepción si hay menos marinos que máquinas")
    void powerRejectsFewerSailorsThanMachines() {
        Ship first = TestFixtures.shipWithCrew(1, ORIGIN, 1);
        Ship second = TestFixtures.shipWithCrew(2, ORIGIN, 1);
        fleet.addMachine(first);
        fleet.addMachine(second);
        fleet.addSailor(first.getSailors().get(0));

        BattleShipException error = assertThrows(BattleShipException.class, fleet::power);

        assertEquals(BattleShipException.FEWER_SAILORS_THAN_MACHINES, error.getMessage());
    }

    @Test
    @DisplayName("Con exactamente un marino por máquina no hay excepción")
    void powerAcceptsEqualSailorsAndMachines() throws BattleShipException {
        addShip(fleet, 1, ORIGIN, 1);
        addShip(fleet, 2, ORIGIN, 1);

        assertEquals(0, fleet.power());
    }

    @Test
    @DisplayName("Las cápsulas no necesitan marinos: no cuentan para el mínimo y sí suman poder")
    void powerDoesNotRequireSailorsForCapsules() throws BattleShipException {
        Ship ship = addShip(fleet, 1, ORIGIN, 1); // 1 marino, 1 máquina con tripulación
        addCapsule(fleet, ORIGIN, ship);
        addCapsule(fleet, ORIGIN, ship);

        assertEquals(2, fleet.power());
    }

    @Test
    @DisplayName("Los marinos autodestruidos dejan de contar, lo que puede provocar la excepción")
    void powerReactsToDestroyedSailors() throws BattleShipException {
        Ship ship = addShip(fleet, 1, ORIGIN, 2);
        addShip(fleet, 2, ORIGIN, 1);
        assertEquals(0, fleet.power());

        Sailor sailor = ship.getSailors().get(0);
        sailor.receiveSelfDestructInstruction();
        Sailor other = fleet.getSailors().get(fleet.getSailors().size() - 1);
        other.receiveSelfDestructInstruction();
        fleet.executeSelfDestructions();

        // Quedan 2 barcos y solo 1 marino registrado en la flota
        assertThrows(BattleShipException.class, fleet::power);
    }
}
