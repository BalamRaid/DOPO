package domain;

/**
 * Episodio: contenido audiovisual que pertenece a una serie.
 * <p>
 * Hereda de {@link Audiovisual} los datos de medición (intentos, terminados,
 * votos y suma de votos) y agrega la referencia a la serie de la que forma
 * parte.
 */
public class Episode extends Audiovisual{
    private Series series;

    /**
     * Crea un episodio de la serie indicada, sin datos de medición.
     *
     * @param title título del episodio
     * @param series serie a la que pertenece
     */
    public Episode(String title, Series series){
        super(title);
        this.series=series;
    }

    /**
     * Crea un episodio con sus datos de medición.
     *
     * @param title título del episodio
     * @param series serie a la que pertenece
     * @param attempts intentos
     * @param completed terminados
     * @param votes votos
     * @param sumVotes suma de votos
     */
    public Episode(String title, Series series, int attempts, int completed, int votes, int sumVotes){
        super(title, attempts, completed, votes, sumVotes);
        this.series=series;
    }
}
