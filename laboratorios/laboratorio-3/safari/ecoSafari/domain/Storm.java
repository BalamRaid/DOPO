package domain;
import java.awt.Color;

/**
 * Representa una tormenta básica del EcoSafari. Su centro se desplaza un paso
 * por tic en diagonal hacia el noreste y, al salirse de la cuadrícula, reaparece
 * por el lado opuesto. Afecta un área de diámetro tres (su centro y sus ocho
 * vecinas). Destruye la entidad que encuentra en su centro. Su centro es negro.
 */
public class Storm implements Entity{
    private final EcoSafari habitat;
    private boolean hasActed;

    /**
     * Crea una tormenta en la posición indicada.
     * @param habitat el EcoSafari donde vive la tormenta
     * @param row la fila inicial
     * @param column la columna inicial
     */
    public Storm(EcoSafari habitat, int row, int column){
        this.habitat = habitat;
        habitat.set((Entity)this, row, column);
        hasActed = false;
    }

    public EcoSafari getHabitat(){
        return habitat;
    }

    public final Color getColor(){
        return Color.BLACK;
    }

    @Override
    public int getAffectedRadius(){
        return 1;
    }

    /**
     * Desplaza la tormenta de forma circular: si la nueva posición queda fuera
     * de la cuadrícula, la tormenta reaparece por el lado opuesto. La entidad
     * que ocupaba la celda destino queda destruida.
     * @param deltaRows desplazamiento en filas
     * @param deltaColumns desplazamiento en columnas
     * @return true si la tormenta estaba en el EcoSafari y se desplazó
     */
    @Override
    public boolean move(int deltaRows, int deltaColumns){
        int[] position = habitat.find(this);
        boolean ok = false;
        if (position != null){
            int size = habitat.getSize();
            int newRow = Math.floorMod(position[0] + deltaRows, size);
            int newColumn = Math.floorMod(position[1] + deltaColumns, size);
            habitat.set(null, position[0], position[1]);
            habitat.set(this, newRow, newColumn);
            ok = true;
        }
        return ok;
    }

    public void tic(){
        if (! hasActed){
            move(-1, 1);
            hasActed = true;
        }
    }

    public void tac(){
        hasActed = false;
    }
}