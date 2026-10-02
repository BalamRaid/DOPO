package battleship;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de {@link Position}: normalización sobre el tablero circular,
 * desplazamientos, igualdad y avance hacia un objetivo.
 *
 * @author DOPO
 * @version 1.0
 */
@DisplayName("Position - tablero circular")
class PositionTest {

    // ------------------------------------------------------------------
    // Construcción y normalización
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Conserva las coordenadas que ya están dentro del tablero")
    void keepsCoordinatesInsideBoard() {
        Position position = new Position(90, -45);
        assertEquals(90, position.getLongitude());
        assertEquals(-45, position.getLatitude());
    }

    @Test
    @DisplayName("Acepta exactamente los límites del tablero")
    void acceptsBoardLimits() {
        Position min = new Position(0, -90);
        Position max = new Position(180, 90);
        assertEquals(0, min.getLongitude());
        assertEquals(-90, min.getLatitude());
        assertEquals(180, max.getLongitude());
        assertEquals(90, max.getLatitude());
    }

    @Test
    @DisplayName("Una longitud mayor a 180 da la vuelta al tablero")
    void wrapsLongitudeAboveMaximum() {
        assertEquals(0, new Position(181, 0).getLongitude());
        assertEquals(1, new Position(182, 0).getLongitude());
    }

    @Test
    @DisplayName("Una longitud negativa da la vuelta al tablero")
    void wrapsNegativeLongitude() {
        assertEquals(180, new Position(-1, 0).getLongitude());
    }

    @Test
    @DisplayName("Una latitud mayor a 90 da la vuelta al tablero")
    void wrapsLatitudeAboveMaximum() {
        assertEquals(-90, new Position(0, 91).getLatitude());
    }

    @Test
    @DisplayName("Una latitud menor a -90 da la vuelta al tablero")
    void wrapsLatitudeBelowMinimum() {
        assertEquals(90, new Position(0, -91).getLatitude());
    }

    // ------------------------------------------------------------------
    // advance
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Avanzar al norte desde la latitud máxima lleva a la latitud mínima")
    void advanceNorthFromMaxLatitudeWraps() {
        Position result = new Position(0, 90).advance(0, 1);
        assertEquals(new Position(0, -90), result);
    }

    @Test
    @DisplayName("Avanzar al este desde la longitud máxima lleva a la longitud mínima")
    void advanceEastFromMaxLongitudeWraps() {
        Position result = new Position(180, 0).advance(1, 0);
        assertEquals(new Position(0, 0), result);
    }

    @Test
    @DisplayName("Avanzar al oeste desde la longitud mínima lleva a la longitud máxima")
    void advanceWestFromMinLongitudeWraps() {
        Position result = new Position(0, 0).advance(-1, 0);
        assertEquals(new Position(180, 0), result);
    }

    @Test
    @DisplayName("Avanzar suma ambos desplazamientos a la vez")
    void advanceAppliesBothDisplacements() {
        Position result = new Position(10, 10).advance(5, -3);
        assertEquals(new Position(15, 7), result);
    }

    @Test
    @DisplayName("Avanzar no modifica la posición original (inmutabilidad)")
    void advanceDoesNotModifyOriginal() {
        Position original = new Position(10, 10);
        original.advance(50, 50);
        assertEquals(new Position(10, 10), original);
    }

    @Test
    @DisplayName("Un desplazamiento enorme no desborda y queda dentro del tablero")
    void advanceWithHugeDistanceStaysInsideBoard() {
        Position result = new Position(180, 90).advance(Integer.MAX_VALUE, Integer.MAX_VALUE);
        assertTrue(result.getLongitude() >= Position.MIN_LONGITUDE
                && result.getLongitude() <= Position.MAX_LONGITUDE);
        assertTrue(result.getLatitude() >= Position.MIN_LATITUDE
                && result.getLatitude() <= Position.MAX_LATITUDE);

        Position negative = new Position(0, -90).advance(Integer.MIN_VALUE, Integer.MIN_VALUE);
        assertTrue(negative.getLongitude() >= Position.MIN_LONGITUDE
                && negative.getLongitude() <= Position.MAX_LONGITUDE);
        assertTrue(negative.getLatitude() >= Position.MIN_LATITUDE
                && negative.getLatitude() <= Position.MAX_LATITUDE);
    }

    @Test
    @DisplayName("Dar una vuelta completa al tablero regresa a la misma posición")
    void fullLapReturnsToSamePosition() {
        Position start = new Position(33, -12);
        Position result = start.advance(181, 181);
        assertEquals(start, result);
    }

    // ------------------------------------------------------------------
    // equals, hashCode y toString
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Dos posiciones con las mismas coordenadas son iguales y tienen el mismo hash")
    void equalPositions() {
        Position a = new Position(10, 20);
        Position b = new Position(10, 20);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("La igualdad se evalúa sobre las coordenadas ya normalizadas")
    void equalityUsesNormalizedCoordinates() {
        assertEquals(new Position(0, 0), new Position(181, 181));
    }

    @Test
    @DisplayName("Posiciones distintas, null u otros tipos no son iguales")
    void differentPositionsAreNotEqual() {
        Position position = new Position(10, 20);
        assertNotEquals(position, new Position(11, 20));
        assertNotEquals(position, new Position(10, 21));
        assertFalse(position.equals(null));
        assertFalse(position.equals("(10, 20)"));
    }

    @Test
    @DisplayName("toString muestra longitud y latitud")
    void toStringShowsCoordinates() {
        assertEquals("(10, 20)", new Position(10, 20).toString());
    }

    // ------------------------------------------------------------------
    // stepTowards
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Si ya está en el objetivo, no se mueve")
    void stepTowardsSamePositionStays() {
        Position position = new Position(10, 10);
        assertEquals(position, position.stepTowards(new Position(10, 10)));
    }

    @Test
    @DisplayName("Da un paso por cada eje hacia el objetivo")
    void stepTowardsMovesOneStepPerAxis() {
        Position result = new Position(10, 10).stepTowards(new Position(5, 20));
        assertEquals(new Position(9, 11), result);
    }

    @Test
    @DisplayName("Un eje ya alineado no se mueve")
    void stepTowardsAlignedAxisStays() {
        Position result = new Position(10, 10).stepTowards(new Position(20, 10));
        assertEquals(new Position(11, 10), result);
    }

    @Test
    @DisplayName("Toma el camino más corto cruzando el borde de longitud")
    void stepTowardsUsesShortestPathAcrossLongitudeEdge() {
        Position result = new Position(1, 0).stepTowards(new Position(179, 0));
        assertEquals(new Position(0, 0), result);
    }

    @Test
    @DisplayName("Toma el camino más corto cruzando el borde de latitud")
    void stepTowardsUsesShortestPathAcrossLatitudeEdge() {
        Position result = new Position(0, 89).stepTowards(new Position(0, -89));
        assertEquals(new Position(0, 90), result);
    }

    @Test
    @DisplayName("Repetir pasos llega finalmente al objetivo")
    void repeatedStepsReachTarget() {
        Position current = new Position(3, -5);
        Position target = new Position(8, 4);
        for (int i = 0; i < 20; i++) {
            current = current.stepTowards(target);
        }
        assertEquals(target, current);
    }
}
