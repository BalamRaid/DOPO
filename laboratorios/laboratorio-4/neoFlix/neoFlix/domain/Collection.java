package domain;
import java.util.TreeMap;

/**
 * Colección: contenido temático que agrupa otros contenidos en un mapa
 * ordenado por título.
 * <p>
 * Sus indicadores aún no están implementados en esta versión: toda operación
 * lanza {@link NeoFlixException#TO_BE_IMPLEMENTED}.
 */
public class Collection extends Content{

    private String theme;
    private TreeMap<String, Content> contents;

    /**
     * Crea una colección vacía con el tema indicado.
     *
     * @param title título de la colección
     * @param theme tema de la colección
     */
    public Collection(String title, String theme){
        super(title);
        this.theme=theme;
        contents= new TreeMap<String,Content>();
    }

    /**
     * Indica que la popularidad de una colección todavía no está implementada.
     *
     * @return nunca retorna; siempre lanza la excepción
     * @throws NeoFlixException TO_BE_IMPLEMENTED siempre
     */
    @Override
    public int popularity() throws NeoFlixException{
        throw new NeoFlixException(NeoFlixException.TO_BE_IMPLEMENTED);
    }

    /**
     * Indica que la valoración de una colección todavía no está implementada.
     *
     * @return nunca retorna; siempre lanza la excepción
     * @throws NeoFlixException TO_BE_IMPLEMENTED siempre
     */
    @Override
    public int rating() throws NeoFlixException{
        throw new NeoFlixException(NeoFlixException.TO_BE_IMPLEMENTED);
    }

    /**
     * Indica que la representación textual de una colección todavía no está
     * implementada.
     *
     * @param withIndicators true para incluir los indicadores
     * @return nunca retorna; siempre lanza la excepción
     * @throws NeoFlixException TO_BE_IMPLEMENTED siempre
     */
    @Override
    public String data(boolean withIndicators) throws NeoFlixException{
        throw new NeoFlixException(NeoFlixException.TO_BE_IMPLEMENTED);
    }

}
