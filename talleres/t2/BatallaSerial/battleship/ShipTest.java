package battleship;

import static battleship.TestFixtures.HERE;
import static battleship.TestFixtures.sailor;
import static battleship.TestFixtures.shipWithCrew;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de {@link Ship} y del comportamiento común heredado de
 * {@link Machine} (ubicación y movimiento), junto con {@link Sailor}.
 *
 * @author DOPO
 * @version 1.0
 */
@DisplayName("Ship y Sailor")
class ShipTest {

    // ------------------------------------------------------------------
    // Sailor
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Un marino conserva su nombre y su rango, y el rango se puede cambiar")
    void sailorKeepsNameAndRank() {
        Sailor sailor = new Sailor("Blas", 3);
        assertEquals("Blas", sailor.getName());
        assertEquals(3, sailor.getRank());
        sailor.setRank(4);
        assertEquals(4, sailor.getRank());
    }

    @Test
    @DisplayName("Un marino sin nombre no se puede crear")
    void sailorRequiresName() {
        assertThrows(NullPointerException.class, () -> new Sailor(null, 1));
    }

    @Test
    @DisplayName("Dos marinos con los mismos datos son personas distintas")
    void sailorsHaveObjectIdentity() {
        Sailor a = new Sailor("Blas", 3);
        Sailor b = new Sailor("Blas", 3);
        assertFalse(a.equals(b));
    }

    // ------------------------------------------------------------------
    // Construcción
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Un barco conserva su número y su posición")
    void shipKeepsNumberAndLocation() {
        Ship ship = shipWithCrew(900, HERE, 1);
        assertEquals(900, ship.getNumber());
        assertEquals(HERE, ship.getLocation());
    }

    @Test
    @DisplayName("Un barco siempre nace con al menos un marino")
    void shipStartsWithFirstSailor() {
        Ship ship = shipWithCrew(1, HERE, 1);
        assertEquals(1, ship.getSailors().size());
    }

    @Test
    @DisplayName("Agregar marinos aumenta la tripulación")
    void addSailorGrowsCrew() {
        Ship ship = shipWithCrew(1, HERE, 1);
        Sailor extra = sailor("extra");
        ship.addSailor(extra);
        assertEquals(2, ship.getSailors().size());
        assertTrue(ship.getSailors().contains(extra));
    }

    @Test
    @DisplayName("No acepta argumentos nulos")
    void shipRejectsNullArguments() {
        assertThrows(NullPointerException.class, () -> new Ship(1, null, sailor("a")));
        assertThrows(NullPointerException.class, () -> new Ship(1, HERE, null));
        Ship ship = shipWithCrew(1, HERE, 1);
        assertThrows(NullPointerException.class, () -> ship.addSailor(null));
    }

    @Test
    @DisplayName("La lista de marinos expuesta es de solo lectura")
    void sailorsListIsUnmodifiable() {
        Ship ship = shipWithCrew(1, HERE, 1);
        assertThrows(UnsupportedOperationException.class,
                () -> ship.getSailors().add(sailor("intruso")));
    }

    // ------------------------------------------------------------------
    // Debilidad
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Es débil con menos de cinco marinos")
    void isWeakWithFewerThanFiveSailors() {
        for (int crew = 1; crew < Ship.MIN_SAILORS; crew++) {
            assertTrue(shipWithCrew(1, HERE, crew).isWeak(), "tripulación de " + crew);
        }
    }

    @Test
    @DisplayName("No es débil con cinco marinos")
    void isNotWeakWithExactlyFiveSailors() {
        assertFalse(shipWithCrew(1, HERE, 5).isWeak());
    }

    @Test
    @DisplayName("No es débil con más de cinco marinos")
    void isNotWeakWithMoreThanFiveSailors() {
        assertFalse(shipWithCrew(1, HERE, 8).isWeak());
    }

    @Test
    @DisplayName("Los marinos autodestruidos no cuentan como tripulación")
    void destroyedSailorsDoNotCountAsCrew() {
        Ship ship = shipWithCrew(1, HERE, 5);
        assertFalse(ship.isWeak());
        ship.getSailors().get(0).selfDestruct();
        assertTrue(ship.isWeak());
    }

    // ------------------------------------------------------------------
    // Movimiento
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Con tripulación activa puede moverse")
    void canMoveWithActiveCrew() {
        assertTrue(shipWithCrew(1, HERE, 1).canMove());
    }

    @Test
    @DisplayName("Sin ningún marino activo no puede moverse")
    void cannotMoveWithoutActiveSailors() {
        Ship ship = shipWithCrew(1, HERE, 2);
        for (Sailor sailor : ship.getSailors()) {
            sailor.selfDestruct();
        }
        assertFalse(ship.canMove());
    }

    @Test
    @DisplayName("moveNorth sube una posición de latitud")
    void moveNorthIncreasesLatitude() throws BattleShipException {
        Ship ship = shipWithCrew(1, new Position(10, 10), 1);
        ship.moveNorth();
        assertEquals(new Position(10, 11), ship.getLocation());
    }

    @Test
    @DisplayName("moveNorth desde la latitud máxima da la vuelta al tablero")
    void moveNorthWrapsAroundBoard() throws BattleShipException {
        Ship ship = shipWithCrew(1, new Position(10, 90), 1);
        ship.moveNorth();
        assertEquals(new Position(10, -90), ship.getLocation());
    }

    @Test
    @DisplayName("moveNorth lanza excepción si no puede moverse y no cambia su posición")
    void moveNorthThrowsWhenCannotMove() {
        Ship ship = shipWithCrew(1, new Position(10, 10), 1);
        ship.getSailors().get(0).selfDestruct();
        BattleShipException error = assertThrows(BattleShipException.class, ship::moveNorth);
        assertEquals(BattleShipException.CANNOT_MOVE, error.getMessage());
        assertEquals(new Position(10, 10), ship.getLocation());
    }

    @Test
    @DisplayName("advance desplaza la distancia indicada en ambos ejes")
    void advanceMovesShip() {
        Ship ship = shipWithCrew(1, new Position(10, 10), 1);
        ship.advance(5, -4);
        assertEquals(new Position(15, 6), ship.getLocation());
    }

    @Test
    @DisplayName("moveTowards da un paso hacia el objetivo")
    void moveTowardsMovesOneStep() {
        Ship ship = shipWithCrew(1, new Position(10, 10), 1);
        ship.moveTowards(new Position(20, 0));
        assertEquals(new Position(11, 9), ship.getLocation());
    }

    @Test
    @DisplayName("isAt solo es verdadero en su posición exacta")
    void isAtOnlyAtItsPosition() {
        Ship ship = shipWithCrew(1, HERE, 1);
        assertTrue(ship.isAt(HERE));
        assertFalse(ship.isAt(HERE.advance(1, 0)));
    }

    // ------------------------------------------------------------------
    // Destrucción y nodriza
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Un barco es afectado por las explosiones")
    void shipCanBeDestroyed() {
        assertTrue(shipWithCrew(1, HERE, 1).canBeDestroyed());
    }

    @Test
    @DisplayName("Como nodriza entrega una instrucción que identifica al barco")
    void shipGivesInstruction() {
        String instruction = shipWithCrew(900, HERE, 1).giveInstruction();
        assertTrue(instruction.contains("900"));
    }

    @Test
    @DisplayName("Un barco necesita marinos y no tiene pilotos asignados")
    void shipNeedsSailorsAndHasNoPilots() {
        Ship ship = shipWithCrew(1, HERE, 1);
        assertTrue(ship.needsSailors());
        assertTrue(ship.assignedPilots().isEmpty());
    }
}
