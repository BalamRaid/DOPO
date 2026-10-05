import java.awt.Shape;

/**
 * Representa una única rueda de la máquina tragamonedas. Una rueda no
 * posee sus propios símbolos: la secuencia completa vive en SlotMachine
 * y es compartida por todas las ruedas. Una Wheel únicamente recuerda
 * qué índice de esa secuencia compartida está mostrando actualmente a
 * través de su ventana.
 *
 * <p>Esta clase es la base de la jerarquía del Ciclo 4 (tipo {@code normal}).
 * Las subclases redefinen {@link #getType()}, los permisos
 * ({@link #canBeLocked()}, {@link #canBeSwapped()}, {@link #canBeDeleted()}),
 * el comportamiento de copia ({@link #copiesLeft()}) y la decoración visual
 * ({@link #getMarkerColor()}, {@link #getMarkerShape()}).
 *
 * @version 3.0 (Ciclo 4 M1)
 */
public class Wheel {
    private int visibleIndex; // 0-based index into SlotMachine's symbol list
    private boolean locked;

    /**
     * Crea una rueda normal en la posición 0 y sin bloqueo.
     */
    public Wheel() {
        visibleIndex = 0;
        locked = false;
    }

    /**
     * Obtiene el índice visible actual dentro de la lista compartida de símbolos.
     *
     * @return índice visible actual (base 0).
     */
    public int getVisibleIndex() {
        return visibleIndex;
    }

    /**
     * Indica si la rueda está fija (bloqueada).
     *
     * @return true si la rueda está fija; false en caso contrario.
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Fija o libera la rueda.
     *
     * @param locked true para fijar la rueda; false para liberarla.
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * Obtiene el tipo de la rueda.
     *
     * @return nombre del tipo en minúsculas; {@code "normal"} en esta clase base.
     */
    public String getType() {
        return "normal";
    }

    /**
     * Indica si la rueda acepta ser fijada con {@code lock}.
     *
     * @return true si puede fijarse; false si lo veta (por ejemplo, rebel).
     */
    public boolean canBeLocked() {
        return true;
    }

    /**
     * Indica si la rueda acepta ser intercambiada con {@code swap}.
     *
     * @return true si puede intercambiarse; false si lo veta.
     */
    public boolean canBeSwapped() {
        return true;
    }

    /**
     * Indica si la rueda acepta ser eliminada con {@code delWheel}.
     *
     * @return true si puede eliminarse; false si lo veta.
     */
    public boolean canBeDeleted() {
        return true;
    }

    /**
     * Indica si al girar esta rueda copia el estado de la vecina izquierda
     * en lugar de rotar.
     *
     * @return true si copia a la izquierda (lefty); false si rota normal.
     */
    public boolean copiesLeft() {
        return false;
    }

    /**
     * Obtiene el color del marcador visual que distingue el tipo de rueda.
     *
     * @return nombre de color CSS del marcador; null si no lleva marcador (normal).
     */
    public String getMarkerColor() {
        return null;
    }

    /**
     * Obtiene la figura del marcador visual que distingue el tipo de rueda,
     * centrada en (0,0). Es una insignia pequeña de esquina, no un fondo,
     * para no tapar ni confundirse con el símbolo central.
     *
     * @return figura del marcador; null si no lleva marcador (normal).
     */
    public Shape getMarkerShape() {
        return null;
    }

    /**
     * Desplazamiento horizontal de la insignia respecto al centro de la rueda.
     *
     * @return píxeles en X desde el centro; 0 en normal.
     */
    public int getMarkerDx() {
        return 0;
    }

    /**
     * Desplazamiento vertical de la insignia respecto al centro de la rueda.
     *
     * @return píxeles en Y desde el centro; 0 en normal.
     */
    public int getMarkerDy() {
        return 0;
    }

    /**
     * Desplaza la ventana visible el número de pasos indicado, dando la
     * vuelta (wrap around) sobre la lista compartida de símbolos (de
     * tamaño totalSymbols).
     *
     * @param steps cantidad de pasos con signo (negativo gira en sentido contrario).
     * @param totalSymbols tamaño de la lista compartida de símbolos; si es 0 no hace nada.
     */
    public void rotate(int steps, int totalSymbols) {
        if (totalSymbols == 0) return;
        visibleIndex = Math.floorMod(visibleIndex + steps, totalSymbols);
    }

    /**
     * Establece directamente qué índice de la lista compartida de símbolos
     * está visible (usado por placeSymbol).
     *
     * @param index nuevo índice visible (base 0) dentro de la lista compartida.
     */
    public void setVisibleIndex(int index) {
        visibleIndex = index;
    }

    /**
     * Ajusta el índice visible de esta rueda después de que se insertó un
     * símbolo en la posición insertedAt de la lista compartida, de modo que
     * la rueda siga mostrando el mismo símbolo que mostraba antes de la
     * inserción.
     *
     * @param insertedAt posición (base 0) donde se insertó el símbolo.
     */
    public void adjustForInsertion(int insertedAt) {
        if (insertedAt <= visibleIndex) {
            visibleIndex++;
        }
    }

    /**
     * Ajusta el índice visible de esta rueda después de que se eliminó el
     * símbolo en la posición removedAt de la lista compartida (ahora de
     * tamaño newTotal). Si esta rueda mostraba el símbolo eliminado, pasa
     * al siguiente símbolo disponible (dando la vuelta si es necesario).
     *
     * @param removedAt posición (base 0) del símbolo eliminado.
     * @param newTotal nuevo tamaño de la lista compartida tras eliminar.
     */
    public void adjustForRemoval(int removedAt, int newTotal) {
        if (newTotal == 0) {
            visibleIndex = 0;
        } else if (visibleIndex == removedAt) {
            visibleIndex = Math.floorMod(visibleIndex, newTotal);
        } else if (visibleIndex > removedAt) {
            visibleIndex--;
        }
    }
}
