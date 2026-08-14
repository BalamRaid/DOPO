import java.util.ArrayList;

public class Flota {
    private String nombre;
    private Tablero tablero;
    private ArrayList<PortaAviones> portaAviones;
    private ArrayList<Avion> aviones;
    private ArrayList<Barco> barcos;
    private ArrayList<Marino> marinos;
    private final String codigo;

    public Flota(String nombre, Tablero tablero, String codigo){
        this.nombre = nombre;
        this.tablero = tablero;
        this.codigo = codigo;
        this.aviones = new ArrayList<Avion>();
        this.portaAviones = new ArrayList<PortaAviones>();
        this.barcos = new ArrayList<Barco>();
        this.marinos = new ArrayList<Marino>();
    }

    public String getCodigo(){
        return codigo;
    }

    public int alias(){
        ArrayList<Flota> totalFlotas = tablero.getFlotas();
        int contador = 0;
        for(Flota f : totalFlotas){
            if(f.nombre.equals(this.nombre)){
                contador += 1;
            }
        }
        return contador;
    }
}
