import java.util.TreeMap;

/**
 * Punto de control de la aplicación miniTunes: administra un conjunto de
 * listas de reproducción identificadas por nombre, y permite definirlas,
 * asignarles contenido y combinarlas mediante operaciones unarias (agregar,
 * eliminar, seleccionar) y binarias (unión, intersección, diferencia).
 * <p>
 * Los nombres de las listas se mantienen ordenados alfabéticamente. Cada
 * operación pública reporta su éxito o fracaso a través de {@link #ok()},
 * consultable inmediatamente después de invocarla.
 *
 * @author ESCUELA 2026-02
 */
    
public class MiniTunes{
    
    /** Listas de reproducción registradas, indexadas por nombre y ordenadas alfabéticamente. */
    private TreeMap<String,Playlist> playlists;
    /** Indica si la última operación invocada se completó exitosamente. */
    private boolean ok;
    
    /**
     * Crea una instancia de miniTunes sin listas de reproducción definidas.
     */
    public MiniTunes(){
        playlists = new TreeMap<>();
    }

    /**
     * Define un nuevo nombre de lista de reproducción, inicialmente vacía.
     * Falla si el nombre ya está definido.
     *
     * @param name nombre de la nueva lista de reproducción
     */
    public void define(String name){
        if (playlists.containsKey(name)) {
            ok = false;
            return;
        }
        playlists.put(name, new Playlist(new String[0][0]));
        ok = true;
    }
     
    /**
     * Asigna al nombre {@code a}, ya definido, una lista de reproducción
     * construida a partir de datos crudos. Falla si {@code a} no ha sido
     * definido previamente con {@link #define(String)}.
     *
     * @param a        nombre de la lista destino, debe estar definido
     * @param playlist datos crudos de las canciones (ver {@link Playlist#Playlist(String[][])})
     */
    public void assign(String a, String [] [] playlist){
        if (!playlists.containsKey(a)) {
            ok = false;
            return;
        }
        playlists.put(a, new Playlist(playlist));
        ok = true;
    }    

    /**
     * Consulta el número de canciones de la lista de reproducción indicada.
     *
     * @param a nombre de la lista a consultar
     * @return el tamaño de la lista, o {@code -1} si el nombre no está definido
     */
    public int size(String a){
        Playlist pl = playlists.get(a);
        return pl == null ? -1 : pl.size();
    }
    
    /**
     * @return los nombres de todas las listas de reproducción definidas, en
     *         orden alfabético y separados por coma y espacio
     */
    @Override
    public String toString(){
        return String.join(", ", playlists.keySet());
    }
    
    /**
     * Consulta la representación textual de una lista de reproducción.
     *
     * @param name nombre de la lista a consultar
     * @return la representación de la lista (ver {@link Playlist#toString()}),
     *         o {@code null} si el nombre no está definido
     */
    public String toString(String name){
        Playlist pl = playlists.get(name);
        return pl == null ? null : pl.toString();
    }    
    
    /**
     * Asigna al nombre {@code a} el resultado de aplicar una operación unaria
     * sobre la lista {@code b}: {@code a := b op values}.
     * <p>
     * Operadores soportados: {@code 'a'} (agregar canción), {@code 'd'}
     * (eliminar canción) y {@code 's'} (seleccionar según patrón). Para
     * {@code 'a'} y {@code 'd'}, {@code values} representa los datos de una
     * canción; para {@code 's'}, representa el patrón de búsqueda. Falla si
     * {@code a} o {@code b} no están definidos, o si {@code op} no es uno de
     * los operadores soportados.
     *
     * @param a      nombre de la lista destino, debe estar definido
     * @param b      nombre de la lista origen, debe estar definido
     * @param op     operador unario ({@code 'a'}, {@code 'd'} o {@code 's'})
     * @param values datos de la canción o patrón de búsqueda, según el operador
     */
    public void assignUnary(String a, String b, char op, String [] values){
        if (!playlists.containsKey(a) || !playlists.containsKey(b)) {
            ok = false;
            return;
        }
        Playlist source = playlists.get(b);
        Playlist result;
        switch (op) {
            case 'a':
                result = source.add(values);
                break;
            case 'd':
                result = source.delete(values);
                break;
            case 's':
                result = source.select(values);
                break;
            default:
                ok = false;
                return;
        }
        playlists.put(a, result);
        ok = true;
    }
      
    /**
     * Asigna al nombre {@code a} el resultado de combinar las listas
     * {@code b} y {@code c}: {@code a := b op c}.
     * <p>
     * Operadores soportados: {@code 'u'} (unión), {@code 'i'} (intersección) y
     * {@code 'd'} (diferencia). Las canciones conservan su orden original en la
     * lista resultante. Falla si {@code a}, {@code b} o {@code c} no están
     * definidos, o si {@code op} no es uno de los operadores soportados.
     *
     * @param a  nombre de la lista destino, debe estar definido
     * @param b  nombre de la primera lista operando, debe estar definido
     * @param op operador binario ({@code 'u'}, {@code 'i'} o {@code 'd'})
     * @param c  nombre de la segunda lista operando, debe estar definido
     */
    public void assignBinary(String a, String b, char op, String c){
        if (!playlists.containsKey(a) || !playlists.containsKey(b) || !playlists.containsKey(c)) {
            ok = false;
            return;
        }
        Playlist left = playlists.get(b);
        Playlist right = playlists.get(c);
        Playlist result;
        switch (op) {
            case 'u': result = left.union(right); break;
            case 'i': result = left.intersect(right); break;
            case 'd': result = left.difference(right); break;
            default:
                ok = false;
                return;
        }
        playlists.put(a, result);
        ok = true;
    }
  
    /**
     * @return {@code true} si la última operación invocada sobre esta
     *         instancia se completó exitosamente; {@code false} en caso contrario
     */
    public boolean ok(){
        return ok;
    }
    
}
    



