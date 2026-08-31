/**
 * Representa una canción dentro de una lista de reproducción de miniTunes.
 * <p>
 * Una canción se identifica de forma única por la combinación de su título y
 * artista (normalizados a mayúsculas, sin espacios redundantes). El género es
 * opcional; la duración, si se conoce, debe estar entre 1 y 9 minutos, y la
 * calificación, si se conoce, entre una y cinco estrellas ({@code "*"} a
 * {@code "*****"}).
 * <p>
 * Las instancias son inmutables y sólo se obtienen mediante el método de
 * fábrica {@link #from(String[])}, que garantiza que nunca exista una
 * canción con datos inválidos.
 *
 * @author ESCUELA 2026-02
 */

import java.util.Objects;

class Song {

    private final String title;
    private final String artist;
    private final String genre;
    private final Integer duration;
    private final String rating;

    /**
     * Construye una canción ya validada y normalizada.
     *
     * @param title    título normalizado (mayúsculas, sin espacios redundantes)
     * @param artist   artista normalizado
     * @param genre    género normalizado, o {@code null} si es desconocido
     * @param duration duración en minutos (1 a 9), o {@code null} si es desconocida
     * @param rating   calificación en estrellas, o {@code null} si es desconocida
     */
    private Song(String title, String artist, String genre, Integer duration, String rating) {
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.duration = duration;
        this.rating = rating;
    }

    /**
     * Fábrica que construye una canción a partir de una fila de datos crudos,
     * validando y normalizando cada campo.
     * <p>
     * El arreglo debe tener exactamente 5 posiciones, en el orden: título,
     * artista, género, duración y calificación. Título y artista son
     * obligatorios; los demás campos pueden ser {@code null} (desconocidos). La
     * duración, si se informa, debe ser un entero entre 1 y 9; la calificación,
     * si se informa, debe ser una secuencia de 1 a 5 asteriscos (los espacios
     * internos se ignoran).
     *
     * @param row fila con los datos crudos de la canción
     * @return la canción construida, o {@code null} si la fila es inválida
     *         (tamaño incorrecto, título o artista vacíos, o duración/
     *         calificación fuera de rango o con formato incorrecto)
     */
    static Song from(String[] row) {
        if (row == null || row.length != 5) {
            return null;
        }
        String title = normalize(row[0]);
        String artist = normalize(row[1]);
        if (title == null || artist == null) {
            return null;
        }
        String genre = normalize(row[2]);

        Integer duration = null;
        if (row[3] != null) {
            try {
                duration = Integer.parseInt(row[3].trim());
            } catch (NumberFormatException e) {
                return null;
            }
            if (duration < 1 || duration > 9) {
                return null;
            }
        }

        String rating = null;
        if (row[4] != null) {
            rating = row[4].replaceAll("\\s+", "");
            if (!rating.matches("\\*{1,5}")) {
                return null;
            }
        }

        return new Song(title, artist, genre, duration, rating);
    }

    /**
     * Normaliza un texto: recorta los espacios en los extremos, colapsa los
     * espacios internos múltiples en uno solo y lo convierte a mayúsculas.
     *
     * @param s texto a normalizar, puede ser {@code null}
     * @return el texto normalizado, o {@code null} si {@code s} es {@code null}
     *         o queda vacío después de normalizar
     */
    private static String normalize(String s) {
        if (s == null) {
            return null;
        }
        String cleaned = s.trim().replaceAll("\\s+", " ");
        return cleaned.isEmpty() ? null : cleaned.toUpperCase();
    }

    /**
     * Indica si esta canción representa la misma entrada que {@code other}, es
     * decir, si coinciden en título y artista: los campos que identifican de
     * forma única una canción dentro de una lista de reproducción.
     *
     * @param other canción con la cual comparar
     * @return {@code true} si título y artista coinciden
     */
    boolean sameEntryAs(Song other) {
        return title.equals(other.title) && artist.equals(other.artist);
    }
    
    /** @return el título de la canción, ya normalizado. */
    String title() { return title; }
    
    /** @return el artista de la canción, ya normalizado. */
    String artist() { return artist; }
    
    /** @return el género, o {@code "."} si es desconocido, para su representación textual. */
    String genreOrDot() { return genre == null ? "." : genre; }
    
    /** @return la duración como texto, o cadena vacía si es desconocida. */
    String durationOrBlank() { return duration == null ? "" : String.valueOf(duration); }
    
    /** @return la calificación, o cadena vacía si es desconocida. */
    String ratingOrBlank() { return rating == null ? "" : rating; }

    /**
     * Compara esta canción con otro objeto teniendo en cuenta todos sus
     * atributos (título, artista, género, duración y calificación), a
     * diferencia de {@link #sameEntryAs(Song)}, que sólo considera título y
     * artista.
     *
     * @param o objeto a comparar
     * @return {@code true} si {@code o} es una {@code Song} con los mismos valores en todos sus atributos
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Song)) return false;
        Song other = (Song) o;
        return title.equals(other.title)
            && artist.equals(other.artist)
            && Objects.equals(genre, other.genre)
            && Objects.equals(duration, other.duration)
            && Objects.equals(rating, other.rating);
    }

    /**
     * {@inheritDoc}
     * Consistente con {@link #equals(Object)}: se calcula a partir de todos
     * los atributos de la canción.
     */
    @Override
    public int hashCode() {
        return Objects.hash(title, artist, genre, duration, rating);
    }
    
    /**
     * Determina si esta canción satisface un patrón de búsqueda.
     * <p>
     * El arreglo de criterios debe tener 5 posiciones, en el mismo orden que
     * los datos de una canción (título, artista, género, duración,
     * calificación). Un criterio {@code null} actúa como comodín (no
     * restringe); un criterio no nulo debe coincidir, tras normalizarlo, con
     * el valor correspondiente de la canción.
     *
     * @param criteria patrón de búsqueda con 5 posiciones
     * @return {@code true} si la canción cumple todos los criterios no nulos;
     *         {@code false} si {@code criteria} es {@code null}, no tiene
     *         tamaño 5, o si algún criterio no coincide
     */
    boolean matches(String[] criteria) {
        if (criteria == null || criteria.length != 5) {
            return false;
        }
        return fieldMatches(criteria[0], title)
            && fieldMatches(criteria[1], artist)
            && fieldMatches(criteria[2], genre)
            && fieldMatches(criteria[3], duration == null ? null : String.valueOf(duration))
            && fieldMatches(criteria[4], rating);
    }

    /**
     * Compara un criterio de búsqueda (posiblemente {@code null}, es decir,
     * comodín) contra el valor real de un campo.
     *
     * @param criterion valor buscado, sin normalizar; {@code null} equivale a "cualquiera"
     * @param actual    valor real del campo, ya normalizado; puede ser {@code null}
     * @return {@code true} si el criterio es comodín, o si coincide con
     *         {@code actual} una vez normalizado
     */
    private static boolean fieldMatches(String criterion, String actual) {
        if (criterion == null) {
            return true;
        }
        String normalized = normalize(criterion);
        return normalized == null ? actual == null : normalized.equals(actual);
    }
    
}