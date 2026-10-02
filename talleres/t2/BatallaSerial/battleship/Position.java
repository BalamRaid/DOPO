package battleship; 

import java.util.Objects;

/**
 * Coordenada inmutable del tablero del juego.
 *
 * <p>El tablero es circular: la longitud toma valores en [0, 180] y la latitud
 * en [-90, 90]. Cualquier valor fuera de esos rangos se normaliza dando la
 * vuelta al tablero (por ejemplo, avanzar al norte desde la latitud 90 lleva a
 * la latitud -90).</p>
 *
 * <p>Al ser inmutable, una misma posición puede compartirse de forma segura y
 * cada desplazamiento produce una nueva instancia.</p>
 *
 * @author DOPO
 * @version 1.0
 */
public final class Position {

    /** Longitud mínima del tablero. */
    public static final int MIN_LONGITUDE = 0;
    /** Longitud máxima del tablero. */
    public static final int MAX_LONGITUDE = 180;
    /** Latitud mínima del tablero. */
    public static final int MIN_LATITUDE = -90;
    /** Latitud máxima del tablero. */
    public static final int MAX_LATITUDE = 90;

    private static final int LONGITUDE_SIZE = MAX_LONGITUDE - MIN_LONGITUDE + 1;
    private static final int LATITUDE_SIZE = MAX_LATITUDE - MIN_LATITUDE + 1;

    private final int longitude;
    private final int latitude;

    /**
     * Crea una posición normalizando las coordenadas al tablero circular.
     *
     * @param longitude longitud deseada (se ajusta al rango [0, 180])
     * @param latitude  latitud deseada (se ajusta al rango [-90, 90])
     */
    public Position(int longitude, int latitude) {
        this.longitude = wrap(longitude, MIN_LONGITUDE, LONGITUDE_SIZE);
        this.latitude = wrap(latitude, MIN_LATITUDE, LATITUDE_SIZE);
    }

    /**
     * Consulta la longitud.
     *
     * @return longitud en [0, 180]
     */
    public int getLongitude() {
        return longitude;
    }

    /**
     * Consulta la latitud.
     *
     * @return latitud en [-90, 90]
     */
    public int getLatitude() {
        return latitude;
    }

    /**
     * Calcula la posición resultante de desplazarse la distancia indicada.
     *
     * @param dLon desplazamiento en longitud (puede ser negativo)
     * @param dLat desplazamiento en latitud (puede ser negativo)
     * @return nueva posición, ya normalizada al tablero circular
     */
    public Position advance(int dLon, int dLat) {
        return new Position((long) longitude + dLon, (long) latitude + dLat);
    }

    /**
     * Calcula la posición que resulta de dar un paso hacia el objetivo por el
     * camino más corto en cada eje (considerando que el tablero es circular).
     *
     * @param target posición hacia la cual se quiere avanzar
     * @return nueva posición, un paso más cerca del objetivo en cada eje
     */
    public Position stepTowards(Position target) {
        int dLon = shortestStep(longitude, target.longitude, LONGITUDE_SIZE);
        int dLat = shortestStep(latitude, target.latitude, LATITUDE_SIZE);
        return advance(dLon, dLat);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Position)) {
            return false;
        }
        Position that = (Position) other;
        return longitude == that.longitude && latitude == that.latitude;
    }

    @Override
    public int hashCode() {
        return Objects.hash(longitude, latitude);
    }

    @Override
    public String toString() {
        return "(" + longitude + ", " + latitude + ")";
    }

    // ------------------------------------------------------------------
    // Métodos auxiliares
    // ------------------------------------------------------------------

    private Position(long longitude, long latitude) {
        this.longitude = wrap(longitude, MIN_LONGITUDE, LONGITUDE_SIZE);
        this.latitude = wrap(latitude, MIN_LATITUDE, LATITUDE_SIZE);
    }

    /**
     * Normaliza un valor al rango circular [min, min + size - 1].
     */
    private static int wrap(long value, int min, int size) {
        return (int) Math.floorMod(value - min, (long) size) + min;
    }

    /**
     * Determina la dirección (-1, 0 o 1) del camino más corto entre dos
     * valores de un eje circular.
     */
    private static int shortestStep(int from, int to, int size) {
        int forward = Math.floorMod(to - from, size);
        if (forward == 0) {
            return 0;
        }
        return forward <= size - forward ? 1 : -1;
    }
}
