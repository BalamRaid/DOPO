package domain;

/**
 * Película: contenido audiovisual medible con presupuesto.
 * <p>
 * Hereda de {@link Audiovisual} los indicadores (popularidad y valoración) y
 * agrega el presupuesto de producción.
 */
public class Movie extends Audiovisual{
    private int budge;

    /**
     * Crea una película con su presupuesto, sin datos de medición.
     *
     * @param title título de la película
     * @param budge presupuesto de producción
     */
    public Movie(String title, int budge){
        super(title);
        this.budge=budge;
    }
}
