package battleship;

import static battleship.TestFixtures.HERE;
import static battleship.TestFixtures.plane;
import static battleship.TestFixtures.sailor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de {@link Plane}: debilidad por piloto, inmunidad en el aire,
 * movimiento y pilotos asignados.
 *
 * @author DOPO
 * @version 1.0
 */
@DisplayName("Plane")
class PlaneTest {

    // ------------------------------------------------------------------
    // Construcción
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Un avión nuevo está en tierra, sin copiloto, con su placa y su piloto")
    void newPlaneStartsOnGround() {
        Sailor pilot = sailor("piloto");
        Plane plane = new Plane("chkgood", HERE, pilot);
        assertEquals("chkgood", plane.getPlate());
        assertSame(pilot, plane.getPilot());
        assertNull(plane.getCopilot());
        assertFalse(plane.isInAir());
        assertEquals(HERE, plane.getLocation());
    }

    @Test
    @DisplayName("No se puede crear un avión sin placa")
    void planeRequiresPlate() {
        assertThrows(NullPointerException.class, () -> new Plane(null, HERE, sailor("p")));
    }

    @Test
    @DisplayName("Se pueden cambiar el piloto, el copiloto y el estado de vuelo")
    void settersUpdateState() {
        Plane plane = new Plane("A", HERE, null);
        Sailor pilot = sailor("piloto");
        Sailor copilot = sailor("copiloto");
        plane.setPilot(pilot);
        plane.setCopilot(copilot);
        plane.setInAir(true);
        assertSame(pilot, plane.getPilot());
        assertSame(copilot, plane.getCopilot());
        assertTrue(plane.isInAir());
    }

    // ------------------------------------------------------------------
    // Debilidad
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Sin piloto principal es débil")
    void isWeakWithoutPilot() {
        assertTrue(plane("A", HERE, null, false).isWeak());
    }

    @Test
    @DisplayName("Con piloto principal no es débil")
    void isNotWeakWithPilot() {
        assertFalse(plane("A", HERE, sailor("p"), false).isWeak());
    }

    @Test
    @DisplayName("Tener copiloto pero no piloto sigue siendo débil")
    void copilotDoesNotReplacePilot() {
        Plane plane = plane("A", HERE, null, false);
        plane.setCopilot(sailor("copiloto"));
        assertTrue(plane.isWeak());
    }

    @Test
    @DisplayName("Un piloto autodestruido equivale a no tener piloto")
    void destroyedPilotMakesPlaneWeak() {
        Sailor pilot = sailor("p");
        Plane plane = plane("A", HERE, pilot, false);
        assertFalse(plane.isWeak());
        pilot.selfDestruct();
        assertTrue(plane.isWeak());
    }

    // ------------------------------------------------------------------
    // Inmunidad
    // ------------------------------------------------------------------

    @Test
    @DisplayName("En tierra puede ser destruido por una explosión")
    void planeOnGroundCanBeDestroyed() {
        assertTrue(plane("A", HERE, sailor("p"), false).canBeDestroyed());
    }

    @Test
    @DisplayName("En el aire es inmune a las explosiones")
    void planeInAirIsImmune() {
        assertFalse(plane("A", HERE, sailor("p"), true).canBeDestroyed());
    }

    @Test
    @DisplayName("Al aterrizar vuelve a ser vulnerable")
    void landingRemovesImmunity() {
        Plane plane = plane("A", HERE, sailor("p"), true);
        plane.setInAir(false);
        assertTrue(plane.canBeDestroyed());
    }

    // ------------------------------------------------------------------
    // Movimiento
    // ------------------------------------------------------------------

    @Test
    @DisplayName("En tierra sin piloto sí puede moverse")
    void planeOnGroundWithoutPilotCanMove() {
        assertTrue(plane("A", HERE, null, false).canMove());
    }

    @Test
    @DisplayName("En el aire con piloto puede moverse")
    void planeInAirWithPilotCanMove() {
        assertTrue(plane("A", HERE, sailor("p"), true).canMove());
    }

    @Test
    @DisplayName("En el aire sin piloto no puede moverse")
    void planeInAirWithoutPilotCannotMove() {
        assertFalse(plane("A", HERE, null, true).canMove());
    }

    @Test
    @DisplayName("moveNorth lanza excepción cuando el avión en el aire no tiene piloto")
    void moveNorthThrowsWhenAirbornePlaneHasNoPilot() {
        Plane plane = plane("A", new Position(5, 5), null, true);
        BattleShipException error = assertThrows(BattleShipException.class, plane::moveNorth);
        assertEquals(BattleShipException.CANNOT_MOVE, error.getMessage());
        assertEquals(new Position(5, 5), plane.getLocation());
    }

    @Test
    @DisplayName("moveNorth mueve un avión con piloto")
    void moveNorthMovesPlaneWithPilot() throws BattleShipException {
        Plane plane = plane("A", new Position(5, 5), sailor("p"), true);
        plane.moveNorth();
        assertEquals(new Position(5, 6), plane.getLocation());
    }

    // ------------------------------------------------------------------
    // Pilotos asignados y tripulación
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Sus pilotos asignados son solo su piloto principal")
    void assignedPilotsContainsOnlyPilot() {
        Sailor pilot = sailor("p");
        Plane plane = plane("A", HERE, pilot, false);
        plane.setCopilot(sailor("copiloto"));
        assertEquals(1, plane.assignedPilots().size());
        assertSame(pilot, plane.assignedPilots().get(0));
    }

    @Test
    @DisplayName("Sin piloto no tiene pilotos asignados")
    void noAssignedPilotsWithoutPilot() {
        assertTrue(plane("A", HERE, null, false).assignedPilots().isEmpty());
    }

    @Test
    @DisplayName("Un piloto autodestruido no cuenta como piloto asignado")
    void destroyedPilotIsNotAssigned() {
        Sailor pilot = sailor("p");
        Plane plane = plane("A", HERE, pilot, false);
        pilot.selfDestruct();
        assertTrue(plane.assignedPilots().isEmpty());
    }

    @Test
    @DisplayName("Un avión necesita marinos")
    void planeNeedsSailors() {
        assertTrue(plane("A", HERE, null, false).needsSailors());
    }
}
