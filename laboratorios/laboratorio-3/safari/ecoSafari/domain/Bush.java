package domain;
import java.awt.Color;

/**
 * Representa un arbusto del EcoSafari. Es joven (verde) durante sus primeros
 * 4 tics de vida, y envejece (amarillo) a partir del cuarto tic. Retoña una
 * única vez, en el primer tic en que su edad es mayor o igual a 2, en la
 * celda vecina vacía de mayor prioridad (norte, sur, este, oeste, en ese orden).
 * Desaparece si queda vecino a un elefante.
 */
public class Bush extends Organism implements Entity{
    private final EcoSafari habitat;
    private int edad;
    private boolean yaRetono;

    /** Desplazamientos de las 4 direcciones cardinales, en orden de prioridad. */
    private static final int[][] DIRECCIONES = { {-1,0}, {1,0}, {0,1}, {0,-1} };

    public Bush(EcoSafari habitat, int row, int column){
        this.habitat = habitat;
        habitat.set((Entity)this, row, column);
        edad = 0;
        yaRetono = false;
    }

    public EcoSafari getHabitat(){
        return habitat;
    }

    public final Color getColor(){
        return (edad < 4 ? Color.GREEN : Color.YELLOW);
    }

    public void tic(){
        edad++;
        int[] posicion = getHabitat().find(this);
        if (posicion != null){
            if ((! yaRetono) && (edad >= 2)){
                retonar(posicion[0], posicion[1]);
            }
            if (hayElefanteVecino(posicion[0], posicion[1])){
                disappear();
            }
        }
    }

    /**
     * Crea un nuevo Bush en la primera celda vecina vacía disponible,
     * siguiendo el orden de prioridad norte, sur, este, oeste.
     * @param fila fila actual del arbusto
     * @param columna columna actual del arbusto
     */
    private void retonar(int fila, int columna){
        for (int[] direccion : DIRECCIONES){
            int filaVecina = fila + direccion[0];
            int columnaVecina = columna + direccion[1];
            if (getHabitat().isInside(filaVecina, columnaVecina) &&
                getHabitat().get(filaVecina, columnaVecina) == null){
                new Bush(getHabitat(), filaVecina, columnaVecina);
                yaRetono = true;
                return;
            }
        }
    }

    /**
     * Determina si hay un elefante en alguna de las 4 celdas vecinas cardinales.
     */
    private boolean hayElefanteVecino(int fila, int columna){
        for (int[] direccion : DIRECCIONES){
            Entity vecino = getHabitat().get(fila + direccion[0], columna + direccion[1]);
            if (vecino instanceof Elephant){
                return true;
            }
        }
        return false;
    }
}