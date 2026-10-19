package domain;

/**
 * Contenido de la plataforma NeoFlix.
 * <p>
 * Define el contrato común de todos los contenidos: el título y los tres
 * métodos que cualquier contenido debe ofrecer (valoración, popularidad y
 * representación textual). Los indicadores pueden no ser calculables; por eso
 * los métodos declaran {@link NeoFlixException}.
 */
public abstract class Content{
    /** Título del contenido. */
    protected String title;

    /**
     * Crea un contenido con el título indicado.
     *
     * @param title título del contenido
     */
    public Content(String title){
        this.title=title;
    }

    /**
     * Retorna el título del contenido.
     *
     * @return título del contenido
     */
    public String getTitle(){
        return title;
    }

    /**
     * Retorna la valoración calculada, en el rango [0..10].
     *
     * @return valoración del contenido
     * @throws NeoFlixException VALUE_UNKNOWN si la valoración no se ha medido; DATA_ERROR si los datos violan un invariante
     */
    public abstract int rating() throws NeoFlixException;

    /**
     * Retorna la popularidad calculada, en el rango [0..100].
     *
     * @return popularidad del contenido
     * @throws NeoFlixException VALUE_UNKNOWN si la popularidad no se ha medido; DATA_ERROR si los datos violan un invariante
     */
    public abstract int popularity() throws NeoFlixException;

    /**
     * Retorna la representación textual del contenido.
     *
     * @param withIndicators true para incluir los indicadores
     * @return representación textual del contenido
     * @throws NeoFlixException si la información no se puede calcular
     */
    public abstract String data(boolean withIndicators) throws NeoFlixException;
}
