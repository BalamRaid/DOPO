package battleship;

import static battleship.TestFixtures.HERE;
import static battleship.TestFixtures.addCapsule;
import static battleship.TestFixtures.addPlane;
import static battleship.TestFixtures.addShip;
import static battleship.TestFixtures.sailor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas del comportamiento de autodestrucción: la decisión y su causa en
 * marinos, barcos y aviones, y la ejecución en cadena que realiza la flota.
 *
 * @author DOPO
 * @version 1.0
 */
@DisplayName("Autodestrucción")
class SelfDestructionTest {

    private Fleet fleet;

    @BeforeEach
    void setUp() {
        fleet = new Fleet("LA GRAN ARMADA DE CASTILLA");
    }

    // ------------------------------------------------------------------
    // Decisión y causa por elemento
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Un marino nuevo no ha decidido nada")
    void sailorStartsWithoutDecision() {
        Sailor sailor = sailor("s");
        assertFalse(sailor.mustSelfDestruct());
        assertFalse(sailor.isDestroyed());
        assertEquals(SelfDestructible.NO_DECISION, sailor.getSelfDestructCause());
    }

    @Test
    @DisplayName("Un marino decide autodestruirse al recibir la instrucción y explica por qué")
    void sailorSelfDestructsOnInstruction() {
        Sailor sailor = sailor("s");
        sailor.receiveSelfDestructInstruction();
        assertTrue(sailor.mustSelfDestruct());
        assertEquals(SelfDestructible.INSTRUCTION_CAUSE, sailor.getSelfDestructCause());
        assertFalse(sailor.isDestroyed());
        sailor.selfDestruct();
        assertTrue(sailor.isDestroyed());
    }

    @Test
    @DisplayName("Un barco decide autodestruirse al recibir la instrucción y explica por qué")
    void shipSelfDestructsOnInstruction() {
        Ship ship = TestFixtures.shipWithCrew(1, HERE, 1);
        assertFalse(ship.mustSelfDestruct());
        assertEquals(SelfDestructible.NO_DECISION, ship.getSelfDestructCause());
        ship.receiveSelfDestructInstruction();
        assertTrue(ship.mustSelfDestruct());
        assertEquals(SelfDestructible.INSTRUCTION_CAUSE, ship.getSelfDestructCause());
    }

    @Test
    @DisplayName("Un avión decide autodestruirse al recibir la instrucción y explica por qué")
    void planeSelfDestructsOnInstruction() {
        Plane plane = TestFixtures.plane("A", HERE, sailor("p"), true);
        assertFalse(plane.mustSelfDestruct());
        plane.receiveSelfDestructInstruction();
        assertTrue(plane.mustSelfDestruct());
        assertEquals(SelfDestructible.INSTRUCTION_CAUSE, plane.getSelfDestructCause());
    }

    @Test
    @DisplayName("La causa se conserva después de destruirse")
    void causeIsKeptAfterDestruction() {
        Ship ship = TestFixtures.shipWithCrew(1, HERE, 1);
        ship.receiveSelfDestructInstruction();
        ship.selfDestruct();
        assertTrue(ship.isDestroyed());
        assertEquals(SelfDestructible.INSTRUCTION_CAUSE, ship.getSelfDestructCause());
    }

    // ------------------------------------------------------------------
    // Ejecución en la flota
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Sin instrucciones nadie se autodestruye")
    void nobodyDestroysWithoutInstruction() {
        addShip(fleet, 1, HERE, 5);
        ArrayList<SelfDestructible> destroyed = fleet.executeSelfDestructions();
        assertTrue(destroyed.isEmpty());
        assertTrue(fleet.getSelfDestroyed().isEmpty());
        assertEquals(1, fleet.getMachines().size());
        assertEquals(5, fleet.getSailors().size());
    }

    @Test
    @DisplayName("Una flota vacía no tiene nada que autodestruir")
    void emptyFleetHasNothingToDestroy() {
        assertTrue(fleet.executeSelfDestructions().isEmpty());
    }

    @Test
    @DisplayName("Un barco instruido se destruye, sale de la flota y queda registrado")
    void instructedShipIsDestroyedAndRegistered() {
        Ship ship = addShip(fleet, 1, HERE, 1);
        ship.receiveSelfDestructInstruction();

        ArrayList<SelfDestructible> destroyed = fleet.executeSelfDestructions();

        assertEquals(1, destroyed.size());
        assertTrue(destroyed.contains(ship));
        assertTrue(ship.isDestroyed());
        assertFalse(fleet.getMachines().contains(ship));
        assertTrue(fleet.getSelfDestroyed().contains(ship));
    }

    @Test
    @DisplayName("Un marino instruido se destruye y sale de la lista de marinos de la flota")
    void instructedSailorLeavesFleet() {
        Ship ship = addShip(fleet, 1, HERE, 3);
        Sailor sailor = ship.getSailors().get(0);
        sailor.receiveSelfDestructInstruction();

        fleet.executeSelfDestructions();

        assertTrue(sailor.isDestroyed());
        assertFalse(fleet.hasSailor(sailor));
        assertEquals(2, fleet.getSailors().size());
        assertTrue(fleet.getSelfDestroyed().contains(sailor));
        assertEquals(1, fleet.getMachines().size());
    }

    @Test
    @DisplayName("Un avión instruido se destruye y sale de la flota")
    void instructedPlaneIsDestroyed() {
        Plane plane = addPlane(fleet, "A", HERE, sailor("p"), true);
        plane.receiveSelfDestructInstruction();

        fleet.executeSelfDestructions();

        assertTrue(plane.isDestroyed());
        assertFalse(fleet.getMachines().contains(plane));
        assertTrue(fleet.getSelfDestroyed().contains(plane));
    }

    @Test
    @DisplayName("La cápsula se autodestruye cuando su barco nodriza se destruye")
    void capsuleFollowsItsShip() {
        Ship ship = addShip(fleet, 1, HERE, 1);
        Capsule capsule = addCapsule(fleet, HERE, ship);
        ship.receiveSelfDestructInstruction();

        ArrayList<SelfDestructible> destroyed = fleet.executeSelfDestructions();

        assertEquals(2, destroyed.size());
        assertTrue(capsule.isDestroyed());
        assertEquals(Capsule.MOTHER_DESTROYED_CAUSE, capsule.getSelfDestructCause());
    }

    @Test
    @DisplayName("La decisión se propaga en cadena: barco, cápsula y cápsula de la cápsula")
    void decisionCascadesThroughChain() {
        Ship ship = addShip(fleet, 1, HERE, 1);
        Capsule first = addCapsule(fleet, HERE, ship);
        Capsule second = addCapsule(fleet, HERE, first);
        Capsule third = addCapsule(fleet, HERE, second);
        ship.receiveSelfDestructInstruction();

        ArrayList<SelfDestructible> destroyed = fleet.executeSelfDestructions();

        assertEquals(4, destroyed.size());
        assertTrue(first.isDestroyed());
        assertTrue(second.isDestroyed());
        assertTrue(third.isDestroyed());
        assertTrue(fleet.getMachines().isEmpty());
    }

    @Test
    @DisplayName("La cascada funciona sin importar el orden en que se registraron las máquinas")
    void cascadeIsIndependentOfRegistrationOrder() {
        Ship ship = TestFixtures.shipWithCrew(1, HERE, 1);
        Capsule first = new Capsule(HERE, ship);
        Capsule second = new Capsule(HERE, first);
        // Se registran de la más dependiente a la menos dependiente
        fleet.addMachine(second);
        fleet.addMachine(first);
        fleet.addMachine(ship);
        ship.receiveSelfDestructInstruction();

        assertEquals(3, fleet.executeSelfDestructions().size());
        assertTrue(fleet.getMachines().isEmpty());
    }

    @Test
    @DisplayName("Solo se destruyen las cápsulas de la nodriza destruida, no las de otras")
    void onlyCapsulesOfDestroyedMotherFollow() {
        Ship doomed = addShip(fleet, 1, HERE, 1);
        Ship survivor = addShip(fleet, 2, HERE, 1);
        Capsule doomedCapsule = addCapsule(fleet, HERE, doomed);
        Capsule safeCapsule = addCapsule(fleet, HERE, survivor);
        doomed.receiveSelfDestructInstruction();

        fleet.executeSelfDestructions();

        assertTrue(doomedCapsule.isDestroyed());
        assertFalse(safeCapsule.isDestroyed());
        assertTrue(fleet.getMachines().contains(survivor));
        assertTrue(fleet.getMachines().contains(safeCapsule));
    }

    @Test
    @DisplayName("Ejecutar dos veces no duplica los registros")
    void executingTwiceDoesNotDuplicate() {
        Ship ship = addShip(fleet, 1, HERE, 1);
        ship.receiveSelfDestructInstruction();

        fleet.executeSelfDestructions();
        ArrayList<SelfDestructible> second = fleet.executeSelfDestructions();

        assertTrue(second.isEmpty());
        assertEquals(1, fleet.getSelfDestroyed().size());
    }

    @Test
    @DisplayName("La flota acumula los elementos autodestruidos de varias ejecuciones")
    void fleetAccumulatesDestroyedElements() {
        Ship first = addShip(fleet, 1, HERE, 1);
        Ship second = addShip(fleet, 2, HERE, 1);

        first.receiveSelfDestructInstruction();
        fleet.executeSelfDestructions();
        second.receiveSelfDestructInstruction();
        fleet.executeSelfDestructions();

        assertEquals(2, fleet.getSelfDestroyed().size());
        assertTrue(fleet.getSelfDestroyed().contains(first));
        assertTrue(fleet.getSelfDestroyed().contains(second));
    }

    @Test
    @DisplayName("Cada elemento registrado puede informar la causa de su decisión")
    void everyRegisteredElementReportsItsCause() {
        Ship ship = addShip(fleet, 1, HERE, 1);
        Capsule capsule = addCapsule(fleet, HERE, ship);
        ship.receiveSelfDestructInstruction();
        fleet.executeSelfDestructions();

        for (SelfDestructible element : fleet.getSelfDestroyed()) {
            assertTrue(element.isDestroyed());
            assertFalse(SelfDestructible.NO_DECISION.equals(element.getSelfDestructCause()));
        }
        assertEquals(SelfDestructible.INSTRUCTION_CAUSE, ship.getSelfDestructCause());
        assertEquals(Capsule.MOTHER_DESTROYED_CAUSE, capsule.getSelfDestructCause());
    }

    @Test
    @DisplayName("Los elementos destruidos ya no participan en las consultas de la flota")
    void destroyedMachinesLeaveFleetQueries() {
        Ship ship = addShip(fleet, 1, HERE, 1); // débil: 1 marino
        assertTrue(fleet.weakMachines().contains(ship));
        assertTrue(fleet.willBeDestroyed(HERE.getLongitude(), HERE.getLatitude()).contains(ship));

        ship.receiveSelfDestructInstruction();
        fleet.executeSelfDestructions();

        assertTrue(fleet.weakMachines().isEmpty());
        assertTrue(fleet.willBeDestroyed(HERE.getLongitude(), HERE.getLatitude()).isEmpty());
    }

    @Test
    @DisplayName("La lista de autodestruidos expuesta es de solo lectura")
    void selfDestroyedListIsUnmodifiable() {
        assertThrows(UnsupportedOperationException.class,
                () -> fleet.getSelfDestroyed().add(sailor("intruso")));
    }
    
    @Test
    @DisplayName("Prueba temporal")
    public void pruebaTemporal() {
        assertTrue(true);
    }
}
