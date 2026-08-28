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
    
    public ArrayList<Avion> getAviones() {
        return aviones;
    }
    
    public ArrayList<Barco> getBarcos() {
        return barcos;
    }

    public ArrayList<PortaAviones> getPortaAviones() {
        return portaAviones;
    }
    
    /**
     * Consulta el número de flotas que tienen su mismo nombre.
     *
     * @return numero de flotas con el mismo nombre
     */
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
    
    /**
     * Calcula la disponibilidad total de los portaaviones de la flota.
     *
     * @return numero de aviones adicionales que podrían cargarse a los portaaviones
     */
    public int disponibilidadEnPortaAviones() {
        int disponibilidadTotal = 0;

        for (PortaAviones p : this.portaAviones) {
            disponibilidadTotal += p.getCapacidad() - p.getAviones().size();
        }

        return disponibilidadTotal;
    }
    
    
    
    /**
     * Consulta la placa de los aviones enemigos que están en el aire.
     *
     * @return la placa de los aviones enemigos que están en el aire
     */
    public ArrayList<String> enAire(){
        ArrayList<String> placasEnAire = new ArrayList<String>();
        ArrayList<Flota> totalFlotas = tablero.getFlotas();

        for(Flota f : totalFlotas){
            if(f != this){
                for(Avion a : f.getAviones()){
                    if(a.isEnAire()){
                        placasEnAire.add(a.getPlaca());
                    }
                }
            }
        }

        return placasEnAire;
    }
    
    /**
     * Verifica si la posición coincide con la ubicación dada.
     *
     * @param ubicacion la ubicación a comparar
     * @param longitud longitud a comparar
     * @param latitud latitud a comparar
     * @return si la ubicación coincide con la longitud y latitud dadas
     */
    private boolean coincide(Posicion ubicacion, int longitud, int latitud) {
        return ubicacion.getLongitud() == longitud && ubicacion.getLatitud() == latitud;
    }

    /**
     * Verifica si la ubicación para un ataque en agua es adecuado
     * (destruye elementos enemigos sin ocasionar bajas propias.
     * Los aviones que están volando no se afectan.)
     *
     * @param longitud longitud de la explosion
     * @param latitud latitud de la explosion
     * @return si el ataque es adecuado
     */
    public boolean esBuenAtaque(int longitud, int latitud) {
        // Verificar que no haya bajas propias
        for (Avion a : this.aviones) {
            if (!a.isEnAire() && coincide(a.getUbicacion(), longitud, latitud)) {
                return false;
            }
        }
        for (Barco b : this.barcos) {
            if (coincide(b.getUbicacion(), longitud, latitud)) {
                return false;
            }   
        }
        for (PortaAviones p : this.portaAviones) {
            if (coincide(p.getUbicacion(), longitud, latitud)) {
                return false;
            }
        }

        // Verificar que destruya al menos un elemento enemigo
        ArrayList<Flota> totalFlotas = tablero.getFlotas();
        for (Flota f : totalFlotas) {
            if (f != this) {
                for (Avion a : f.getAviones()) {
                    if (!a.isEnAire() && coincide(a.getUbicacion(), longitud, latitud)) {
                        return true;
                    }
                }
                for (Barco b : f.getBarcos()) {
                    if (coincide(b.getUbicacion(), longitud, latitud)) {
                        return true;
                    }
                }
                for (PortaAviones p : f.getPortaAviones()) {
                    if (coincide(p.getUbicacion(), longitud, latitud)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
    
    /**
     * Mueve todos los barcos la distancia definida, si es posible.
     *
     * @param deltaLongitud avance en longitud
     * @param deltaLatitud avance en latitud
     */
    public void muevase(int deltaLongitud, int deltaLatitud) {
        for (Barco b : this.barcos) {
            Posicion ubicacion = b.getUbicacion();
            int nuevaLongitud = ubicacion.getLongitud() + deltaLongitud;
            int nuevaLatitud = ubicacion.getLatitud() + deltaLatitud;

            boolean dentroDeRango = nuevaLongitud >= Tablero.LIMITE_MIN
                    && nuevaLongitud <= Tablero.LIMITE_MAX
                    && nuevaLatitud >= Tablero.LIMITE_MIN
                    && nuevaLatitud <= Tablero.LIMITE_MAX;

            if (dentroDeRango) {
                ubicacion.setLongitud(nuevaLongitud);
                ubicacion.setLatitud(nuevaLatitud);
            }
        }
    }
    
    /**
     * Consulta el numero de maquinas que tiene la flota.
     *
     * @return numero de maquinas de la flota
     */
    public int numeroMaquinas() {
        return this.aviones.size() + this.barcos.size() + this.portaAviones.size();
    }
    
    /**
     * Consulta si cuenta con suficientes marinos para conducir sus máquinas.
     * Un portaaviones requiere 5 marinos; un barco, 4; y un avión 2.
     *
     * @return si hay suficientes marinos
     */
    public boolean suficientesMarinos() {
        int marinosNecesarios = this.portaAviones.size() * PortaAviones.TRIPULANTES_MIN
                + this.barcos.size() * Barco.TRIPULANTES_MIN
                + this.aviones.size() * Avion.TRIPULANTES_MIN;

        return this.marinos.size() >= marinosNecesarios;
    }
    
    /**
     * Consulta las máquinas que pueden afectarse por una explosion en agua.
     *
     * @param longitud longitud de la explosion
     * @param latitud latitud de la explosion
     * @return las máquinas que serían destruidas por la explosión
     */
    public ArrayList<Object> seranDestruidas(int longitud, int latitud) {
        ArrayList<Object> destruidas = new ArrayList<Object>();
        ArrayList<Flota> totalFlotas = tablero.getFlotas();

        for (Flota f : totalFlotas) {
            for (Avion a : f.getAviones()) {
                if (!a.isEnAire() && coincide(a.getUbicacion(), longitud, latitud)) {
                    destruidas.add(a);
                }
            }
            for (Barco b : f.getBarcos()) {
                if (coincide(b.getUbicacion(), longitud, latitud)) {
                    destruidas.add(b);
                }
            }
            for (PortaAviones p : f.getPortaAviones()) {
                if (coincide(p.getUbicacion(), longitud, latitud)) {
                    destruidas.add(p);
                }
            }
        }

        return destruidas;
    }
    
    /**
     * Consulta si puede confundir sus aviones con aviones enemigos
     * considerando las placas.
     *
     * @return si hay problema en aire
     */
    public boolean problemaEnAire() {
        ArrayList<String> placasEnemigasEnAire = this.enAire();

        for (Avion a : this.aviones) {
            if (a.isEnAire() && placasEnemigasEnAire.contains(a.getPlaca())) {
                return true;
            }
        }

        return false;
    }
    
}
