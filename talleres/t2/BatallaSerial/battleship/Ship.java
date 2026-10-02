package battleship;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Barco tripulado por uno o más marinos.
 *
 * <p>Un barco es débil cuando tiene menos de {@value #MIN_SAILORS} marinos.</p>
 *
 * @author DOPO
 * @version 1.0
 */
public class Ship extends Machine implements Mothership {

    /** Cantidad mínima de marinos para que un barco no sea débil. */
    public static final int MIN_SAILORS = 5;

    private final int number;
    private final ArrayList<Sailor> sailors;

    /**
     * Crea un barco con su primer marino (un barco siempre tiene al menos uno).
     *
     * @param number       número que identifica al barco
     * @param location     posición inicial
     * @param firstSailor  primer marino de la tripulación, no puede ser nulo
     */
    public Ship(int number, Position location, Sailor firstSailor) {
        super(location);
        this.number = number;
        this.sailors = new ArrayList<>();
        this.sailors.add(Objects.requireNonNull(firstSailor, "El marino no puede ser nulo"));
    }

    /**
     * Consulta el número del barco.
     *
     * @return número que identifica al barco
     */
    public int getNumber() {
        return number;
    }

    /**
     * Consulta la tripulación.
     *
     * @return vista de solo lectura de los marinos del barco
     */
    public List<Sailor> getSailors() {
        return Collections.unmodifiableList(sailors);
    }

    /**
     * Agrega un marino a la tripulación.
     *
     * @param sailor marino a agregar, no puede ser nulo
     */
    public void addSailor(Sailor sailor) {
        sailors.add(Objects.requireNonNull(sailor, "El marino no puede ser nulo"));
    }

    /**
     * Un barco es débil si tiene menos de {@value #MIN_SAILORS} marinos. Los
     * marinos que ya se autodestruyeron no cuentan como tripulación.
     *
     * @return {@code true} si la tripulación activa es insuficiente
     */
    @Override
    public boolean isWeak() {
        return activeSailors() < MIN_SAILORS;
    }

    /**
     * Un barco no puede moverse si no tiene ningún marino activo.
     *
     * @return {@code true} si tiene al menos un marino activo
     */
    @Override
    public boolean canMove() {
        return activeSailors() > 0;
    }

    /**
     * Cuenta los marinos que no se han autodestruido.
     */
    private long activeSailors() {
        return sailors.stream()
                .filter(sailor -> !sailor.isDestroyed())
                .count();
    }

    /**
     * Entrega la instrucción solicitada por una cápsula que lo tiene como
     * nodriza.
     *
     * @return texto de la instrucción del barco
     */
    @Override
    public String giveInstruction() {
        return "Instrucción del barco " + number;
    }
}