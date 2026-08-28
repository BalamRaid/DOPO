public class Avion {
    private String placa;
    private boolean enAire;
    private Posicion ubicacion;
    private Marino piloto;
    private Marino copiloto;
    public static final int TRIPULANTES_MIN = 2;
    private static int puntaje;

    public Avion(String placa, boolean enAire, Posicion ubicacion, Marino piloto){
        this.placa = placa;
        this.enAire = enAire;
        this.ubicacion = ubicacion;
        this.piloto = piloto;
    }

    public static int getTripulantesMin(){
        return  TRIPULANTES_MIN;
    }
    
    public static int getPuntaje(){
        return  puntaje;
    }
    
    public static void setPuntaje(int nuevoPuntaje){
        puntaje = nuevoPuntaje;
    }
    
    public String getPlaca() {
        return placa;
    }

    public boolean isEnAire() {
        return enAire;
    }
    
    public Posicion getUbicacion() {
        return ubicacion;
    }
}
