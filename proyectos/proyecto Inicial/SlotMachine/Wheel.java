/**
 * Representa una única rueda de la máquina tragamonedas. Una rueda no
 * posee sus propios símbolos: la secuencia completa vive en SlotMachine
 * y es compartida por todas las ruedas. Una Wheel únicamente recuerda
 * qué índice de esa secuencia compartida está mostrando actualmente a
 * través de su ventana.
 */
public class Wheel {
    private int visibleIndex; // 0-based index into SlotMachine's symbol list
    private boolean locked;
    
    public Wheel() {
        visibleIndex = 0;
        locked = false;
    }

    public int getVisibleIndex() {
        return visibleIndex;
    }

    public boolean isLocked() {
        return locked;
    }
    
    public void setLocked(boolean locked) {
        this.locked = locked;
    }
    
    /**
     * Desplaza la ventana visible el número de pasos indicado, dando la
     * vuelta (wrap around) sobre la lista compartida de símbolos (de
     * tamaño totalSymbols).
     */
    public void rotate(int steps, int totalSymbols) {
        if (totalSymbols == 0) return;
        visibleIndex = Math.floorMod(visibleIndex + steps, totalSymbols);
    }

    /**
     * Establece directamente qué índice de la lista compartida de símbolos
     * está visible (usado por placeSymbol).
     */
    public void setVisibleIndex(int index) {
        visibleIndex = index;
    }
    
    /**
     * Ajusta el índice visible de esta rueda después de que se insertó un
     * símbolo en la posición insertedAt de la lista compartida, de modo que
     * la rueda siga mostrando el mismo símbolo que mostraba antes de la
     * inserción.
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