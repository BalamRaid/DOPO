package domain;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Fachada del dominio de NeoFlix: el catálogo de contenidos (episodios y
 * series), su consulta, la adición validada de contenidos y el listado.
 */

public class NeoFlix{
    private ArrayList<Content> contents;
    private TreeMap<String,Episode> episodes;

    /**
     * Create a NeoFlix
     */
    public NeoFlix(){
        contents = new ArrayList<Content>();
        episodes = new TreeMap<String,Episode>();
        addSomeSeries();
    }

    
    /**
     * Carga los datos de ejemplo del catálogo: cinco episodios sueltos y la
     * serie "The Neo Ninja" que los reutiliza.
     * <p>
     * Los datos de muestra son responsabilidad del programador: si alguna
     * validación fallara (defecto de programación), la excepción se atiende
     * aquí mismo registrándola en el log y no se propaga.
     */
    private void addSomeSeries(){
        String [][] episodeData= {{"The Hidden Village","199","120","50","430"},
                                  {"The First Mission","140","126","60","510"},
                                  {"The Rival","199","120","50","430"},
                                  {"The Tournament","180","162","90","810"},
                                  {"The Final Battle","200","190","120","1080"}};
        StringBuffer titles=new StringBuffer();
        for (String [] e : episodeData){
            titles.append(e[0]+"\n");
        }
        try {
            for (String [] e: episodeData){
                addEpisode(e[0],"?", e[1],e[2],e[3],e[4]);
            }
            String [][] seriesData = {{"The Neo Ninja", "2020", titles.toString()}};
            for (String [] s: seriesData){
                addSeries(s[0],s[1],s[2]);
            }
        } catch (NeoFlixException e){
            Log.record(e);
        }
    }


    /**
     * Consulta un contenido del catálogo por su título exacto, sin distinguir
     * mayúsculas de minúsculas.
     *
     * @param name título buscado
     * @return el contenido encontrado, o null si no existe ninguno con ese título
     */
    public Content consult(String name){
        Content c=null;
        for(int i=0;i<contents.size() && c == null;i++){
            if (contents.get(i).getTitle().compareToIgnoreCase(name)==0) 
               c=contents.get(i);
        }
        return c;
    }

    
    /**
     * Adiciona un nuevo episodio al catálogo, validando el nombre y los datos
     * antes de modificar nada: si alguna validación falla, el catálogo queda
     * intacto.
     *
     * @param title título del episodio (no vacío y no repetido)
     * @param series título de la serie a la que pertenece, o "" o "?" si el episodio es suelto
     * @param attempts intentos de visualización (texto numérico no negativo)
     * @param completed visualizaciones completas (texto numérico no negativo, no mayor que los intentos)
     * @param votes número de votos (texto numérico no negativo)
     * @param sumVotes suma de votos (texto numérico no negativo, no mayor que votos*10)
     * @throws NeoFlixException si el nombre está vacío (EMPTY_TITLE) o repetido
     *         (CONTENT_EXISTS), si algún dato no es numérico (NOT_NUMERIC), o
     *         si los datos violan sus rangos (NEGATIVE_VALUE, INVALID_COMPLETED,
     *         INVALID_VOTES)
     */
    public void addEpisode(String title, String series, String attempts, String completed, String votes, String sumVotes) throws NeoFlixException {
        String name = checkNewTitle(title);
        int a = toNumber(attempts), c = toNumber(completed);
        int v = toNumber(votes),    s = toNumber(sumVotes);
        checkEpisodeData(a, c, v, s);
        Episode e = new Episode(name, findSeries(series), a, c, v, s);
        contents.add(e);
        episodes.put(name.toUpperCase(), e);
    }
    
    
    /**
     * Adiciona una nueva serie al catálogo con los episodios indicados.
     * Valida todo antes de modificar nada: si alguna línea no corresponde a un
     * episodio existente, no se adiciona ningún contenido.
     *
     * @param title título de la serie (no vacío y no repetido)
     * @param year año de estreno (texto numérico)
     * @param theEpisodes títulos de los episodios separados por saltos de línea (las líneas en blanco se ignoran)
     * @throws NeoFlixException igual que addEpisode, y EPISODE_NOT_FOUND si
     *         alguna línea no corresponde a un episodio existente del catálogo
     */
    public void addSeries(String title, String year, String theEpisodes) throws NeoFlixException {
        String name = checkNewTitle(title);
        Series s = new Series(name, toNumber(year));
        for (String line : theEpisodes.split("\n")) {
            String episodeName = line.trim();
            if (episodeName.isEmpty()) continue;
            Episode e = episodes.get(episodeName.toUpperCase());
            if (e == null) throw new NeoFlixException(NeoFlixException.EPISODE_NOT_FOUND);
            s.addEpisode(e);
        }
        contents.add(s);
    }

    /**
     * Valida y normaliza el título de un nuevo contenido.
     *
     * @param title título propuesto
     * @return el título sin espacios externos
     * @throws NeoFlixException EMPTY_TITLE si queda vacío; CONTENT_EXISTS si ya
     *         existe un contenido con ese nombre (sin distinguir mayúsculas)
     */
    private String checkNewTitle(String title) throws NeoFlixException {
        String clean = (title == null) ? "" : title.trim();
        if (clean.isEmpty())        throw new NeoFlixException(NeoFlixException.EMPTY_TITLE);
        if (consult(clean) != null) throw new NeoFlixException(NeoFlixException.CONTENT_EXISTS);
        return clean;
    }

    /**
     * Convierte un texto a número entero, traduciendo el error de formato.
     *
     * @param text texto numérico propuesto
     * @return el valor entero
     * @throws NeoFlixException NOT_NUMERIC si el texto no representa un entero
     *         (se conserva la causa original para la trazabilidad)
     */
    private int toNumber(String text) throws NeoFlixException {
        try {
            return Integer.parseInt(text == null ? "" : text.trim());
        } catch (NumberFormatException e) {
            throw new NeoFlixException(NeoFlixException.NOT_NUMERIC, e);
        }
    }

    /**
     * Valida los rangos de los datos de medición de un episodio. Los valores
     * en cero son válidos: significan "indicador aún no medido".
     *
     * @param a intentos de visualización
     * @param c visualizaciones completas
     * @param v número de votos
     * @param s suma de votos
     * @throws NeoFlixException NEGATIVE_VALUE si algún dato es negativo;
     *         INVALID_COMPLETED si los terminados superan los intentos;
     *         INVALID_VOTES si la suma de votos supera diez veces los votos
     */
    private void checkEpisodeData(int a, int c, int v, int s) throws NeoFlixException {
        if (a < 0 || c < 0 || v < 0 || s < 0)
            throw new NeoFlixException(NeoFlixException.NEGATIVE_VALUE);
        if (c > a)
            throw new NeoFlixException(NeoFlixException.INVALID_COMPLETED);
        if ((long) s > (long) v * 10)
            throw new NeoFlixException(NeoFlixException.INVALID_VOTES);
    }

    /**
     * Busca la serie con el título indicado.
     *
     * @param name título de la serie ("" o "?" si el episodio es suelto)
     * @return la serie encontrada, o null si no existe o el contenido no es una serie
     */
    private Series findSeries(String name) {
        Content c = consult(name);
        return (c instanceof Series) ? (Series) c : null;
    }

    /**
     * Selecciona los contenidos cuyo título empieza con el prefijo indicado,
     * sin distinguir mayúsculas de minúsculas.
     *
     * @param prefix prefijo buscado
     * @return los contenidos que cumplen el patrón (lista vacía si ninguno)
     */
    public ArrayList<Content> select(String prefix){
        ArrayList<Content> answers=new ArrayList<Content>();
        String upper=prefix.toUpperCase();
        for(Content c : contents){
            if(c.getTitle().toUpperCase().startsWith(upper)){
                answers.add(c);
            }
        }
        return answers;
    }


    
    /**
     * Arma la representación textual de una lista de contenidos: el número de
     * elementos y, por cada uno, su línea (con indicadores si se piden). Un
     * error esperado al calcular los indicadores de un contenido se degrada a
     * una línea "**** mensaje" y el listado continúa.
     *
     * @param selected contenidos por listar
     * @param withIndicators true para incluir los indicadores de cada contenido
     * @return el texto del listado
     */
    public String data(ArrayList<Content> selected, boolean withIndicators){
        StringBuffer answer=new StringBuffer();
        answer.append(selected.size()+ " elementos\n");
        for(Content c : selected) {
            try{
                answer.append('>' + c.data(withIndicators));
                answer.append("\n");
            }catch(NeoFlixException e){
                answer.append("**** "+e.getMessage()+"\n");
            }
        }    
        return answer.toString();
    }
    
    
    /**
     * Busca contenidos por prefijo de título y los presenta con indicadores.
     *
     * @param prefix prefijo buscado
     * @return el texto de los contenidos encontrados
     */ 
    public String search(String prefix){
        return data(select(prefix),true);
    }
    
    
    /**
     * Retorna el listado completo del catálogo, sin indicadores.
     *
     * @return el texto con todos los contenidos
     */    
    public String toString(){
        return data(contents,false);
    }
    
    /**
     * Retorna el número de contenidos del catálogo.
     *
     * @return la cantidad de contenidos
     */
    public int numberContents(){
        return contents.size();
    }

}
