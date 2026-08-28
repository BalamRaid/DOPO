import java.util.ArrayList;

public class PortaAviones {
    private int numero;
    private int capacidad;
    private Posicion ubicacion;
    private ArrayList<Marino> marinos;
    private ArrayList<Avion> aviones;
    public static final int TRIPULANTES_MIN = 5;
    private static int puntaje;

    public PortaAviones(int numero, int capacidad, Posicion ubicacion){
        this.numero = numero;
        this.capacidad = capacidad;
        this.ubicacion = ubicacion;
        this.puntaje = puntaje;
        this.marinos = new ArrayList<Marino>();
        this.aviones = new ArrayList<Avion>();
    }

    public static int getTripulantesMin(){
        return  TRIPULANTES_MIN;
    }
        
    public static int getPuntaje(){
        return  puntaje;
    }
        
    public int getCapacidad() {
        return capacidad;
    }

    public ArrayList<Avion> getAviones() {
        return aviones;
    }
        
    public static void setPuntaje(int nuevoPuntaje){
        puntaje = nuevoPuntaje;
    }
    
    public Posicion getUbicacion() {
        return ubicacion;
    }
    
}
