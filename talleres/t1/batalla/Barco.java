import java.util.Collection;
import java.util.ArrayList;

public class Barco {
    private int numero;
    private Posicion ubicacion;
    private Collection<Marino> marinos;
    public static final int TRIPULANTES_MIN = 4;
    private static int puntaje;

    public Barco(int numero, Posicion ubicacion){
        this.numero = numero;
        this.ubicacion = ubicacion;
        this.marinos = new ArrayList<Marino>();

    }
    
    public static int getTripulantesMin(){
        return  TRIPULANTES_MIN;
    }
    
    public static int getPuntaje(){
        return puntaje;
    }
    
    public static void setPuntaje(int nuevoPuntaje){
        puntaje = nuevoPuntaje;
    }
    
    public Posicion getUbicacion() {
        return ubicacion;
    }
}
