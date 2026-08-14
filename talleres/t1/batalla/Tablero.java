import java.util.ArrayList;

public class Tablero {
    private ArrayList<Flota> flotas;
    public static final int LIMITE_MIN = -100;
    public static final int LIMINE_MAX = 100;

    public Tablero(){
        flotas = new ArrayList<Flota>();
    }

    public ArrayList<Flota> getFlotas() {
        return flotas;
    }

    public void agregarFlota(Flota f){
        flotas.add(f);
    }
}
