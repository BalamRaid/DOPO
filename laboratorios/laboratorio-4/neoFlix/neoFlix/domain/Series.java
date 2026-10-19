package domain;  
 
import java.util.ArrayList;

/**
 * Serie: contenido audiovisual compuesto por episodios, con año de estreno.
 * <p>
 * Sus indicadores se calculan a partir de los episodios: la valoración como
 * promedio de las valoraciones (con reglas de sustitución o fallo rápido) y la
 * popularidad como promedio de las popularidades conocidas.
 */
public class Series extends Content{

    private int year;
    private ArrayList<Episode> episodes;

    /**
     * Crea una serie sin episodios.
     *
     * @param title título de la serie
     * @param year año de estreno
     */
    public Series(String title, int year){
        super(title);
        this.year=year;
        episodes= new ArrayList<Episode>();
    }

    /**
     * Agrega un episodio al final de la serie.
     *
     * @param e episodio por agregar
     */
    public void addEpisode(Episode e){
        episodes.add(e);
    }
       
    
   /**
   * Retorna la valoración calculada de la serie: el promedio (división entera)
   * de las valoraciones de sus episodios. Es fail-fast: la valoración del
   * primer episodio que no se pueda calcular se propaga tal cual, porque la
   * serie no decide cómo sustituirla ni ignorarla.
   *
   * @return valoración de la serie, en el rango [0..10]
   * @throws NeoFlixException CONTENT_EMPTY si la serie no tiene episodios
   * @throws NeoFlixException VALUE_UNKNOWN si la valoración de algún episodio es desconocida
   * @throws NeoFlixException DATA_ERROR si la valoración de algún episodio no se puede calcular por un error en los datos
   */
   @Override
   public int rating() throws NeoFlixException{
       if (episodes.isEmpty()) throw new NeoFlixException(NeoFlixException.CONTENT_EMPTY);
       int sum = 0;
       for (Episode e : episodes) sum += e.rating();
       return sum / episodes.size();
   }
    
 
   /**
   * Retorna la valoración calculada de la serie usando {@code default_} como
   * valor de sustitución para los episodios de valoración desconocida e
   * ignorando, sin contarlos, los episodios cuyo dato es erróneo.
   *
   * @param default_ valor de sustitución (rango [0..10]) para las valoraciones desconocidas
   * @return valoración de la serie, en el rango [0..10]
   * @throws NeoFlixException VALUE_UNKNOWN si ningún episodio aporta valoración: la serie está vacía o todos sus episodios tienen error en los datos
   */
   public int rating(int default_) throws NeoFlixException{
       int sum = 0;
       int counted = 0;
       for (Episode e : episodes) {
           try {
               sum += e.rating();
               counted++;
           } catch (NeoFlixException ex) {
               if (ex.is(NeoFlixException.VALUE_UNKNOWN)) { sum += default_; counted++; }
               else if (!ex.is(NeoFlixException.DATA_ERROR)) throw ex;
           }
       }
       if (counted == 0) throw new NeoFlixException(NeoFlixException.VALUE_UNKNOWN);
       return sum / counted;
   }

   /**
   * Retorna la valoración calculada de la serie sustituyendo la valoración
   * desconocida de cada episodio por el promedio (división entera) de las
   * valoraciones conocidas: las de los episodios anteriores si {@code previous}
   * es {@code true}, o todas las de la serie si es {@code false}. Un error en
   * los datos de cualquier episodio se propaga.
   *
   * @param previous true para promediar solo los episodios anteriores; false para promediar toda la serie
   * @return valoración de la serie, en el rango [0..10]
   * @throws NeoFlixException CONTENT_EMPTY si la serie no tiene episodios
   * @throws NeoFlixException VALUE_UNKNOWN si no hay valoraciones de referencia para sustituir
   * @throws NeoFlixException DATA_ERROR si la valoración de algún episodio tiene error en los datos
   */
   public int rating(boolean previous) throws NeoFlixException{
       if (episodes.isEmpty()) throw new NeoFlixException(NeoFlixException.CONTENT_EMPTY);
       Integer[] known = knownRatings();
       int refSum = 0;
       int refCount = 0;
       if (!previous) {
           for (Integer value : known) {
               if (value != null) { refSum += value; refCount++; }
           }
       }
       int sum = 0;
       for (Integer value : known) {
           if (value != null) {
               sum += value;
               if (previous) { refSum += value; refCount++; }
           } else {
               if (refCount == 0) throw new NeoFlixException(NeoFlixException.VALUE_UNKNOWN);
               sum += refSum / refCount;
           }
       }
       return sum / known.length;
   }

   /**
   * Retorna las valoraciones de los episodios en el orden de la serie: un
   * elemento {@code null} indica valoración desconocida. Un error en los
   * datos de un episodio se propaga de inmediato.
   *
   * @return arreglo con las valoraciones conocidas (null en las desconocidas)
   * @throws NeoFlixException DATA_ERROR si la valoración de algún episodio tiene error en los datos
   */
   private Integer[] knownRatings() throws NeoFlixException{
       Integer[] ratings = new Integer[episodes.size()];
       for (int i = 0; i < ratings.length; i++) {
           try {
               ratings[i] = episodes.get(i).rating();
           } catch (NeoFlixException ex) {
               if (!ex.is(NeoFlixException.VALUE_UNKNOWN)) throw ex;
           }
       }
       return ratings;
   }
    
   
   /**
   * Retorna la popularidad calculada de la serie: el promedio (división
   * entera) de las popularidades conocidas de sus episodios. Un episodio sin
   * intentos no es "impopular", es "no medido": se ignora.
   *
   * @return popularidad de la serie, en el rango [0..100]
   * @throws NeoFlixException CONTENT_EMPTY si la serie no tiene episodios
   * @throws NeoFlixException VALUE_UNKNOWN si ningún episodio tiene popularidad conocida
   * @throws NeoFlixException DATA_ERROR si la popularidad de algún episodio viola sus invariantes
   */
   @Override
   public int popularity() throws NeoFlixException{
       if (episodes.isEmpty()) throw new NeoFlixException(NeoFlixException.CONTENT_EMPTY);
       int sum = 0;
       int counted = 0;
       for (Episode e : episodes) {
           try {
               sum += e.popularity();
               counted++;
           } catch (NeoFlixException ex) {
               if (!ex.is(NeoFlixException.VALUE_UNKNOWN)) throw ex;
           }
       }
       if (counted == 0) throw new NeoFlixException(NeoFlixException.VALUE_UNKNOWN);
       return sum / counted;
   }
    
    
    /**
    * Retorna la representación textual de la serie y, si se piden, sus
    * indicadores y el listado de episodios. Si los indicadores no se pueden
    * calcular, se reportan como incompletos y los episodios se listan igual.
    *
    * @param withIndicators true para incluir indicadores y listado de episodios
    * @return representación textual de la serie
    */
    @Override
    public String data(boolean withIndicators){
        StringBuffer answer = new StringBuffer(title + ": " + year);
        if (withIndicators) answer.append(indicators());
        for (Episode e : episodes) {
            answer.append("\n\t" + e.data(withIndicators));
        }
        return answer.toString();
    }

    /**
    * Retorna los indicadores de la serie entre paréntesis o el texto de
    * indicadores incompletos si alguno no se puede calcular.
    *
    * @return indicadores formateados para mostrar
    */
    private String indicators(){
        try {
            return " ( " + popularity() + " - " + rating() + " )";
        } catch (NeoFlixException ex) {
            return " *** Indicadores incompletos";
        }
    }
    

}
