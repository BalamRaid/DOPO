package battleship;

import static battleship.TestFixtures.HERE;
import static battleship.TestFixtures.carrierWithCrew;
import static battleship.TestFixtures.plane;
import static battleship.TestFixtures.sailor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de {@link AircraftCarrier}: capacidad, asignación de aviones,
 * debilidad que depende de sus aviones en el aire y validación de pilotos.
 *
 * @author DOPO
 * @version 1.0
 */
@DisplayName("AircraftCarrier")
class AircraftCarrierTest {

    // ------------------------------------------------------------------
    // Construcción y aviones
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Conserva su número y su capacidad, y nace sin aviones")
    void carrierStartsEmpty() {
        AircraftCarrier carrier = carrierWithCrew(333, HERE, 1, 3);
        assertEquals(333, carrier.getNumber());
        assertEquals(3, carrier.getCapacity());
        assertTrue(carrier.getAirPlanes().isEmpty());
    }

    @Test
    @DisplayName("Es un barco: hereda la tripulación y su condición de nodriza")
    void carrierIsAShip() {
        AircraftCarrier carrier = carrierWithCrew(333, HERE, 2, 1);
        Ship asShip = carrier;
        Mothership asMother = carrier;
        assertEquals(2, asShip.getSailors().size());
        assertTrue(asMother.giveInstruction().contains("333"));
    }

    @Test
    @DisplayName("La capacidad debe ser positiva")
    void capacityMustBePositive() {
        assertThrows(IllegalArgumentException.class, () -> carrierWithCrew(1, HERE, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> carrierWithCrew(1, HERE, 1, -2));
    }

    @Test
    @DisplayName("Asigna aviones hasta llenar su capacidad")
    void addsPlanesUpToCapacity() throws BattleShipException {
        AircraftCarrier carrier = carrierWithCrew(1, HERE, 1, 2);
        carrier.addPlane(plane("A", HERE, null, false));
        carrier.addPlane(plane("B", HERE, null, false));
        assertEquals(2, carrier.getAirPlanes().size());
    }

    @Test
    @DisplayName("Lanza excepción al superar su capacidad")
    void throwsWhenCarrierIsFull() throws BattleShipException {
        AircraftCarrier carrier = carrierWithCrew(1, HERE, 1, 1);
        carrier.addPlane(plane("A", HERE, null, false));
        BattleShipException error = assertThrows(BattleShipException.class,
                () -> carrier.addPlane(plane("B", HERE, null, false)));
        assertEquals(BattleShipException.CARRIER_FULL, error.getMessage());
        assertEquals(1, carrier.getAirPlanes().size());
    }

    @Test
    @DisplayName("Lanza excepción si el avión ya está asignado")
    void throwsWhenPlaneAlreadyAssigned() throws BattleShipException {
        AircraftCarrier carrier = carrierWithCrew(1, HERE, 1, 3);
        Plane plane = plane("A", HERE, null, false);
        carrier.addPlane(plane);
        BattleShipException error =
                assertThrows(BattleShipException.class, () -> carrier.addPlane(plane));
        assertEquals(BattleShipException.PLANE_ALREADY_ASSIGNED, error.getMessage());
        assertEquals(1, carrier.getAirPlanes().size());
    }

    @Test
    @DisplayName("La lista de aviones expuesta es de solo lectura")
    void airPlanesListIsUnmodifiable() {
        AircraftCarrier carrier = carrierWithCrew(1, HERE, 1, 1);
        assertThrows(UnsupportedOperationException.class,
                () -> carrier.getAirPlanes().add(plane("A", HERE, null, false)));
    }

    // ------------------------------------------------------------------
    // Debilidad
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Es débil si su tripulación es insuficiente, aunque no tenga aviones")
    void isWeakWhenCrewIsInsufficient() {
        assertTrue(carrierWithCrew(1, HERE, 4, 1).isWeak());
    }

    @Test
    @DisplayName("Con tripulación suficiente y sin aviones no es débil")
    void isNotWeakWithEnoughCrewAndNoPlanes() {
        assertFalse(carrierWithCrew(1, HERE, 5, 1).isWeak());
    }

    @Test
    @DisplayName("Es débil si alguno de sus aviones en el aire es débil")
    void isWeakWhenAirbornePlaneIsWeak() throws BattleShipException {
        AircraftCarrier carrier = carrierWithCrew(1, HERE, 5, 2);
        carrier.addPlane(plane("A", HERE, sailor("piloto"), true));
        carrier.addPlane(plane("B", HERE, null, true));
        assertTrue(carrier.isWeak());
    }

    @Test
    @DisplayName("Un avión débil que sigue en tierra no vuelve débil al portaaviones")
    void weakPlaneOnGroundDoesNotWeakenCarrier() throws BattleShipException {
        AircraftCarrier carrier = carrierWithCrew(1, HERE, 5, 1);
        carrier.addPlane(plane("A", HERE, null, false));
        assertFalse(carrier.isWeak());
    }

    @Test
    @DisplayName("Con aviones en el aire y todos con piloto no es débil")
    void isNotWeakWhenAllAirbornePlanesHavePilots() throws BattleShipException {
        AircraftCarrier carrier = carrierWithCrew(1, HERE, 5, 2);
        carrier.addPlane(plane("A", HERE, sailor("p1"), true));
        carrier.addPlane(plane("B", HERE, sailor("p2"), true));
        assertFalse(carrier.isWeak());
    }

    @Test
    @DisplayName("Si el piloto de un avión en el aire se autodestruye, el portaaviones pasa a ser débil")
    void becomesWeakWhenAirbornePilotIsDestroyed() throws BattleShipException {
        AircraftCarrier carrier = carrierWithCrew(1, HERE, 5, 1);
        Sailor pilot = sailor("p");
        carrier.addPlane(plane("A", HERE, pilot, true));
        assertFalse(carrier.isWeak());
        pilot.selfDestruct();
        assertTrue(carrier.isWeak());
    }

    // ------------------------------------------------------------------
    // Validación de pilotos
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Acepta aviones cuyo piloto pertenece a su tripulación")
    void validatesPilotsFromItsCrew() throws BattleShipException {
        AircraftCarrier carrier = carrierWithCrew(1, HERE, 1, 1);
        Sailor pilot = sailor("piloto");
        carrier.addSailor(pilot);
        carrier.addPlane(plane("A", HERE, pilot, true));
        carrier.validatePilots(); // no debe lanzar excepción
    }

    @Test
    @DisplayName("Lanza excepción si el piloto de un avión no es de su tripulación")
    void rejectsPilotOutsideItsCrew() throws BattleShipException {
        AircraftCarrier carrier = carrierWithCrew(1, HERE, 1, 1);
        carrier.addPlane(plane("A", HERE, sailor("ajeno"), true));
        BattleShipException error =
                assertThrows(BattleShipException.class, carrier::validatePilots);
        assertEquals(BattleShipException.PILOT_NOT_IN_CARRIER, error.getMessage());
    }

    @Test
    @DisplayName("Los aviones sin piloto no invalidan al portaaviones")
    void planesWithoutPilotAreIgnored() throws BattleShipException {
        AircraftCarrier carrier = carrierWithCrew(1, HERE, 1, 1);
        carrier.addPlane(plane("A", HERE, null, true));
        carrier.validatePilots(); // no debe lanzar excepción
    }

    @Test
    @DisplayName("Un portaaviones sin aviones no tiene nada que validar")
    void emptyCarrierValidates() throws BattleShipException {
        carrierWithCrew(1, HERE, 1, 1).validatePilots();
    }

    // ------------------------------------------------------------------
    // Movimiento
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Se mueve al norte como cualquier barco con tripulación")
    void carrierMovesNorth() throws BattleShipException {
        AircraftCarrier carrier = carrierWithCrew(1, new Position(0, 0), 1, 1);
        carrier.moveNorth();
        assertEquals(new Position(0, 1), carrier.getLocation());
    }
}
