/**
 * Representa una lista de reproducción: una colección de canciones sobre la
 * cual se pueden realizar operaciones de consulta, adición, eliminación,
 * selección y combinación (unión, intersección y diferencia).
 * <p>
 * Cada canción se describe por título, artista, género, duración y
 * calificación; título y artista son obligatorios y su combinación debe ser
 * única dentro de la lista. La duración, si se conoce, debe estar entre 1 y
 * 9 minutos, y la calificación entre una y cinco estrellas.
 * <p>
 * Toda operación que "modifica" una lista (agregar, eliminar, seleccionar,
 * combinar) retorna en realidad una <b>nueva</b> instancia; las listas de
 * reproducción son inmutables.
 * <p>
 * <b>Invariante de clase:</b> {@code songs} nunca es {@code null}, no
 * contiene elementos {@code null} y no contiene dos canciones con la misma
 * pareja (título, artista).
 *
 * @author ESCUELA 2026-02
 */

//Each song is described by its title, artist, genre, duration, and rating.
//The title and artist are mandatory. The genre, duration, and rating may be unknown.
//The combination (title, artist) must be unique. Two songs cannot have the same title and artist.
//The duration (minutes) must be between 1 and 9.
//The rating must be between * and *****.
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Playlist {
    
    private final List<Song> songs;
    
    /**
     * Construye una lista de reproducción a partir de una matriz de datos
     * crudos. Cada fila representa una canción en el formato esperado por
     * {@link Song#from(String[])}; las filas inválidas o que dupliquen una
     * pareja (título, artista) ya incluida se descartan silenciosamente.
     *
     * @param songs matriz de canciones; puede ser {@code null} o contener filas inválidas
     */
    public Playlist(String[][] songs) {
        this(buildValidSongs(songs));
    }

    /**
     * Construye una lista de reproducción a partir de canciones ya validadas.
     * Constructor de uso interno, empleado por las operaciones que generan
     * nuevas listas a partir de esta.
     *
     * @param songs canciones ya válidas y sin duplicados
     */
    private Playlist(List<Song> songs) {
        this.songs = Collections.unmodifiableList(songs);
    }
    
    /**
     * Verifica si en {@code list} ya existe una canción con la misma pareja
     * (título, artista) que {@code song}.
     *
     * @param list lista donde buscar
     * @param song canción de referencia
     * @return {@code true} si ya existe una entrada equivalente
     */
    private static boolean containsEntry(List<Song> list, Song song) {
        for (Song s : list) {
            if (s.sameEntryAs(song)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Filtra y valida las filas de una matriz de datos crudos, descartando las
     * inválidas y las que dupliquen una pareja (título, artista) ya incluida.
     *
     * @param rows matriz de datos crudos, puede ser {@code null}
     * @return lista de canciones válidas y sin duplicados, en el orden de aparición
     */
    private static List<Song> buildValidSongs(String[][] rows) {
        List<Song> valid = new ArrayList<>();
        if (rows != null) {
            for (String[] row : rows) {
                Song song = Song.from(row);
                if (song != null && !containsEntry(valid, song)) {
                    valid.add(song);
                }
            }
        }
        return valid;
    }
    
    /**
     * Retorna una nueva lista de reproducción que incluye la canción dada. Si
     * la canción es inválida, o ya existe una con la misma pareja (título,
     * artista), retorna esta misma lista sin cambios.
     *
     * @param song datos crudos de la canción a agregar
     * @return la nueva lista con la canción agregada, o esta lista si no se pudo agregar
     */
    public Playlist add(String [] song){
        Song candidate = Song.from(song);
        if (candidate == null || containsEntry(songs, candidate)) {
            return this;
        }
        List<Song> updated = new ArrayList<>(songs);
        updated.add(candidate);
        return new Playlist(updated);
    }
    
    /**
     * Retorna una nueva lista de reproducción sin la canción cuya pareja
     * (título, artista) coincide con la indicada. Si no existe una canción
     * así, retorna esta misma lista sin cambios.
     *
     * @param song datos con el título y artista de la canción a eliminar
     * @return la nueva lista sin la canción indicada, o esta lista si no se encontró
     */
    public Playlist delete(String [] song){
        Song candidate = Song.from(song);
        if (candidate == null) {
            return this;
        }
        List<Song> updated = new ArrayList<>();
        boolean removed = false;
        for (Song s : songs) {
            if (!removed && s.sameEntryAs(candidate)) {
                removed = true;
                continue;
            }
            updated.add(s);
        }
        return removed ? new Playlist(updated) : this;
    }
    
    /**
     * Retorna una nueva lista de reproducción con las canciones que cumplen el
     * patrón de búsqueda indicado (ver {@link Song#matches(String[])}).
     *
     * @param values patrón de búsqueda con 5 posiciones; un valor {@code null} actúa como comodín
     * @return lista con las canciones que cumplen el patrón, en su orden original
     */
    public Playlist select(String [] values){
        List<Song> selected = new ArrayList<>();
        for (Song s : songs) {
            if (s.matches(values)) {
                selected.add(s);
            }
        }
        return new Playlist(selected);
    }      

    /**
     * @return el número de canciones de la lista
     */
    public int size(){
        return songs.size();
    }    
    
    /**
     * Retorna la representación textual de la lista: una canción por línea, en
     * mayúsculas y con columnas alineadas por espacios (título, artista,
     * género, duración y calificación). El género se muestra como {@code "."}
     * y la duración o calificación se dejan en blanco cuando son desconocidas.
     * Por ejemplo:
     * <pre>
     * TITLE    ARTIST          GENRE   DURATION   RATING
     * ONE      U2              ROCK           4   *****
     * NUMB     LINKIN PARK     ROCK           3
     * ALIVE    PEARL JAM       ROCK           5   ****
     * CREEP    RADIOHEAD       ROCK               *****
     * DREAMS   FLEETWOOD MAC   .              4   ****
     * </pre>
     *
     * @return representación tabulada de la lista, o cadena vacía si está vacía
     */
    @Override
    public String toString() {
        if (songs.isEmpty()) {
            return "";
        }
        int titleWidth = "TITLE".length();
        int artistWidth = "ARTIST".length();
        int genreWidth = "GENRE".length();
        for (Song s : songs) {
            titleWidth = Math.max(titleWidth, s.title().length());
            artistWidth = Math.max(artistWidth, s.artist().length());
            genreWidth = Math.max(genreWidth, s.genreOrDot().length());
        }
        titleWidth += 3;
        artistWidth += 3;
        genreWidth += 3;
        int durationWidth = "DURATION".length() + 3;

        StringBuilder sb = new StringBuilder();
        for (Song s : songs) {
            String line = justifyLeft(s.title(), titleWidth)
                    + justifyLeft(s.artist(), artistWidth)
                    + justifyLeft(s.genreOrDot(), genreWidth)
                    + justifyRight(s.durationOrBlank(), durationWidth)
                    + s.ratingOrBlank();
            sb.append(rstrip(line)).append("\n");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    /**
     * Rellena {@code value} con espacios a la derecha hasta alcanzar
     * {@code width}, para alinearlo a la izquierda.
     *
     * @param value texto a alinear
     * @param width ancho total deseado
     * @return el texto alineado a la izquierda
     */
    private static String justifyLeft(String value, int width) {
        StringBuilder sb = new StringBuilder(value);
        while (sb.length() < width) {
            sb.append(' ');
        }
        return sb.toString();
    }

    /**
     * Rellena {@code value} con espacios a la izquierda hasta alcanzar
     * {@code width}, para alinearlo a la derecha.
     *
     * @param value texto a alinear
     * @param width ancho total deseado
     * @return el texto alineado a la derecha
     */
    private static String justifyRight(String value, int width) {
        StringBuilder sb = new StringBuilder();
        while (sb.length() < width - value.length()) {
            sb.append(' ');
        }
        sb.append(value);
        return sb.toString();
    }

    /**
     * Elimina los espacios en blanco al final del texto.
     *
     * @param value texto a recortar
     * @return el texto sin espacios finales
     */
    private static String rstrip(String value) {
        int end = value.length();
        while (end > 0 && value.charAt(end - 1) == ' ') {
            end--;
        }
        return value.substring(0, end);
    }
    
    /**
     * Compara el contenido de esta lista con el de {@code pl}, canción por
     * canción y en el mismo orden.
     *
     * @param pl lista con la cual comparar; puede ser {@code null}
     * @return {@code true} si ambas listas contienen las mismas canciones, en el mismo orden
     */
    public boolean equals(Playlist pl){
        return pl != null && this.songs.equals(pl.songs);
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o){
        if (!(o instanceof Playlist)) return false;
        return equals((Playlist)o);
    }
    
    @Override
    public int hashCode() {
        return songs.hashCode();
    }
    
    /**
     * Retorna la unión de esta lista con {@code other}: todas las canciones de
     * ambas, sin duplicar parejas (título, artista); ante una coincidencia se
     * conserva la canción de esta lista. Ninguna de las dos listas originales
     * se modifica.
     *
     * @param other lista con la cual combinar
     * @return nueva lista con la unión de ambas
     */
    public Playlist union(Playlist other) {
        List<Song> combined = new ArrayList<>(songs);
        for (Song s : other.songs) {
            if (!containsEntry(combined, s)) {
                combined.add(s);
            }
        }
        return new Playlist(combined);
    }

    /**
     * Retorna la intersección de esta lista con {@code other}: las canciones de
     * esta lista cuya pareja (título, artista) también está presente en
     * {@code other}, en el orden de esta lista.
     *
     * @param other lista con la cual intersectar
     * @return nueva lista con la intersección
     */
    public Playlist intersect(Playlist other) {
        List<Song> result = new ArrayList<>();
        for (Song s : songs) {
            if (containsEntry(other.songs, s)) {
                result.add(s);
            }
        }
        return new Playlist(result);
    }

    /**
     * Retorna la diferencia de esta lista con {@code other}: las canciones de
     * esta lista cuya pareja (título, artista) no está presente en
     * {@code other}, en el orden de esta lista.
     *
     * @param other lista a sustraer
     * @return nueva lista con la diferencia
     */
    public Playlist difference(Playlist other) {
        List<Song> result = new ArrayList<>();
        for (Song s : songs) {
            if (!containsEntry(other.songs, s)) {
                result.add(s);
            }
        }
        return new Playlist(result);
    }
    
}
