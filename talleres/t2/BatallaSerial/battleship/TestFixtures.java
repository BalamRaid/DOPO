package battleship;

/**
 * Fábrica de objetos de prueba compartida por todas las clases de pruebas.
 *
 * <p>Centraliza la construcción de barcos, portaaviones, aviones y cápsulas
 * para que cada prueba exprese solo el escenario que verifica y no el código
 * repetitivo de preparación.</p>
 *
 * @author DOPO
 * @version 1.0
 */
final class TestFixtures {

    /** Posición de referencia usada por defecto en las pruebas. */
    static final Position HERE = new Position(20, 30);

    private TestFixtures() {
        // Clase de utilidades: no se instancia
    }

    /**
     * Crea un marino de rango 1.
     *
     * @param name nombre del marino
     * @return marino nuevo
     */
    static Sailor sailor(String name) {
        return new Sailor(name, 1);
    }

    /**
     * Crea un barco con la cantidad de marinos indicada (mínimo uno).
     *
     * @param number   número del barco
     * @param location posición inicial
     * @param crewSize cantidad de marinos de la tripulación
     * @return barco con su tripulación
     */
    static Ship shipWithCrew(int number, Position location, int crewSize) {
        Ship ship = new Ship(number, location, sailor("marino-" + number + "-1"));
        for (int i = 2; i <= crewSize; i++) {
            ship.addSailor(sailor("marino-" + number + "-" + i));
        }
        return ship;
    }

    /**
     * Crea un portaaviones sin aviones, con la tripulación indicada.
     *
     * @param number   número del portaaviones
     * @param location posición inicial
     * @param crewSize cantidad de marinos (mínimo uno)
     * @param capacity capacidad de aviones
     * @return portaaviones con su tripulación
     */
    static AircraftCarrier carrierWithCrew(int number, Position location, int crewSize, int capacity) {
        AircraftCarrier carrier =
                new AircraftCarrier(number, location, sailor("marino-" + number + "-1"), capacity);
        for (int i = 2; i <= crewSize; i++) {
            carrier.addSailor(sailor("marino-" + number + "-" + i));
        }
        return carrier;
    }

    /**
     * Crea un avión, con el estado de vuelo indicado.
     *
     * @param plate    placa del avión
     * @param location posición inicial
     * @param pilot    piloto principal, o {@code null}
     * @param inAir    {@code true} si el avión está en el aire
     * @return avión nuevo
     */
    static Plane plane(String plate, Position location, Sailor pilot, boolean inAir) {
        Plane plane = new Plane(plate, location, pilot);
        plane.setInAir(inAir);
        return plane;
    }

    /**
     * Crea un barco y lo registra en la flota junto con toda su tripulación.
     *
     * @param fleet    flota que recibe el barco y sus marinos
     * @param number   número del barco
     * @param location posición inicial
     * @param crewSize cantidad de marinos
     * @return barco agregado a la flota
     */
    static Ship addShip(Fleet fleet, int number, Position location, int crewSize) {
        Ship ship = shipWithCrew(number, location, crewSize);
        fleet.addMachine(ship);
        registerCrew(fleet, ship);
        return ship;
    }

    /**
     * Registra en la flota a todos los marinos de un barco.
     *
     * @param fleet flota que recibe a los marinos
     * @param ship  barco cuya tripulación se registra
     */
    static void registerCrew(Fleet fleet, Ship ship) {
        for (Sailor sailor : ship.getSailors()) {
            fleet.addSailor(sailor);
        }
    }

    /**
     * Crea un avión y lo agrega a las máquinas de la flota. No registra al
     * piloto como marino de la flota: cada prueba decide si lo hace.
     *
     * @param fleet    flota que recibe el avión
     * @param plate    placa del avión
     * @param location posición inicial
     * @param pilot    piloto principal, o {@code null}
     * @param inAir    {@code true} si el avión está en el aire
     * @return avión agregado a la flota
     */
    static Plane addPlane(Fleet fleet, String plate, Position location, Sailor pilot, boolean inAir) {
        Plane plane = plane(plate, location, pilot, inAir);
        fleet.addMachine(plane);
        return plane;
    }

    /**
     * Crea una cápsula y la agrega a las máquinas de la flota.
     *
     * @param fleet    flota que recibe la cápsula
     * @param location posición inicial
     * @param mother   nodriza de la cápsula
     * @return cápsula agregada a la flota
     */
    static Capsule addCapsule(Fleet fleet, Position location, Mothership mother) {
        Capsule capsule = new Capsule(location, mother);
        fleet.addMachine(capsule);
        return capsule;
    }
}
