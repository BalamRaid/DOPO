package domain;

/**
 * Contenido audiovisual medible: concentra los datos de medición (intentos,
 * terminados, votos y suma de votos) y deriva de ellos la popularidad y la
 * valoración.
 * <p>
 * Invariante de los datos: {@code 0 <= completed <= attempts},
 * {@code 0 <= votes} y {@code 0 <= sumVotes <= votes*10}. Cuando una operación
 * necesita una parte del invariante que no se cumple, lanza
 * {@link NeoFlixException#DATA_ERROR}; cuando el dato no se ha medido, lanza
 * {@link NeoFlixException#VALUE_UNKNOWN}.
 */
public abstract class Audiovisual extends Content{

    private int attempts;
    private int completed;
    private int votes;
    private int sumVotes;

    /**
     * Crea un contenido audiovisual con sus datos de medición.
     *
     * @param title título del contenido
     * @param attempts intentos
     * @param completed terminados
     * @param votes votos
     * @param sumVotes suma de votos
     */
    public Audiovisual(String title, int attempts, int completed, int votes, int sumVotes){
        super(title);
        this.attempts=attempts;
        this.completed=completed;
        this.votes=votes;
        this.sumVotes=sumVotes;
    }

    /**
     * Crea un contenido audiovisual sin datos de medición (todo en cero).
     *
     * @param title título del contenido
     */
    public Audiovisual(String title){
        this(title,0,0,0,0);
    }

    /**
     * Retorna la popularidad: el porcentaje (división entera) de intentos
     * terminados sobre el total de intentos.
     *
     * @return popularidad, en el rango [0..100]
     * @throws NeoFlixException VALUE_UNKNOWN si no hay intentos (no medido)
     * @throws NeoFlixException DATA_ERROR si los terminados superan los intentos (invariante violado)
     */
    @Override
    public int popularity() throws NeoFlixException{
       if (attempts == 0) throw new NeoFlixException(NeoFlixException.VALUE_UNKNOWN);
       if (completed > attempts) throw new NeoFlixException(NeoFlixException.DATA_ERROR);
       return (completed*100)/attempts;
    }

    /**
     * Retorna la valoración: el promedio (división entera) de los votos.
     *
     * @return valoración, en el rango [0..10]
     * @throws NeoFlixException VALUE_UNKNOWN si no hay votos (no medido)
     * @throws NeoFlixException DATA_ERROR si la suma de votos supera votos*10 (invariante violado)
     */
    @Override
    public int rating() throws NeoFlixException{
       if (votes == 0) throw new NeoFlixException(NeoFlixException.VALUE_UNKNOWN);
       if (sumVotes>votes*10) throw new NeoFlixException(NeoFlixException.DATA_ERROR);
       return sumVotes/votes;
    }

    /**
     * Retorna el título y, si se piden, los indicadores. Si estos no se pueden
     * calcular, se reportan como incompletos.
     *
     * @param withIndicators true para incluir los indicadores
     * @return representación textual del contenido
     */
    @Override
    public String data(boolean withIndicators){
        String theData= title;
        if (withIndicators){
            try{
                theData+=" ( " +popularity()+" - "+ rating()+" )";
            } catch (NeoFlixException e){
                theData += "*** Indicadores incompletos";
            }
        }
        return theData;
    }
}
