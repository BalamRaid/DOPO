/**
 * Representa un robot que se desplaza dentro de un laberinto (RobotMaze).
 * El robot se compone visualmente de tres figuras del paquete shapes
 * (un cuerpo circular y una antena formada por un rectángulo y un círculo),
 * que se mueven de forma sincronizada para representar su posición y estado.
 *
 * La posición del robot se maneja en dos sistemas equivalentes: coordenadas
 * lógicas (xPosition, yPosition), usadas por el laberinto, y posición en
 * píxeles, usada por las figuras de shapes, convertidas mediante CELL_SIZE.
 *
 */
public class Robot{
    private static final int CELL_SIZE= 40;
    private int xPosition;
    private int yPosition;
    private char direction;
    private boolean isVisible;
    private Circle body;
    private Rectangle antenna;
    private Circle antennaTip;
    private boolean lastMoveOK;
    
    /**
     * Crea un robot ubicado en la casilla lógica (x, y) del laberinto,
     * orientado inicialmente hacia el norte y no visible.
     *
     * @param x posición lógica horizontal inicial (columna del laberinto)
     * @param y posición lógica vertical inicial (fila del laberinto)
     */
    public Robot(int x, int y){
        xPosition = x;
        yPosition = y;
        direction = 'N';
        isVisible = false;
        lastMoveOK = true;
        body = new Circle();
        antenna = new Rectangle();
        antennaTip = new Circle();
        body.changeSize(30);
        antenna.changeSize(20, 6);
        antennaTip.changeSize(10);
        positionParts();
    }

    /**
     * Ubica las tres figuras que componen al robot en la posición de
     * píxeles correspondiente a (xPosition, yPosition), a partir de la
     * posición por defecto en la que cada figura de shapes es creada.
     */
    private void positionParts() {
        int targetX = xPosition * CELL_SIZE;
        int targetY = yPosition * CELL_SIZE;
        // Circle() nace en (20,15) -> lo llevamos a (targetX, targetY)
        body.moveHorizontal(targetX - 20);
        body.moveVertical(targetY - 15);
        // Rectangle() nace en (70,15) -> antena centrada sobre el cuerpo, un poco arriba
        antenna.moveHorizontal((targetX + 12) - 70);
        antenna.moveVertical((targetY - 15) - 15);
        // antennaTip: punta encima de la antena
        antennaTip.moveHorizontal((targetX + 10) - 20);
        antennaTip.moveVertical((targetY - 25) - 15);
    }
    
    /**
     * Retorna la posición lógica actual del robot en el laberinto.
     *
     * @return un arreglo de dos posiciones {x, y} con las coordenadas actuales
     */
    public int[] coordinates() {
        return new int[] {xPosition, yPosition};
    }

    /**
     * Retorna la dirección hacia la que actualmente mira el robot.
     *
     * @return la dirección actual: 'N', 'S', 'E' o 'W'
     */
    public char direction() {
        return direction;
    }
    
    /**
     * Mueve el robot step casillas en la dirección hacia la que está
     * orientado. Un valor negativo mueve al robot en sentido contrario
     * a su dirección actual.
     *
     * @param step número de casillas a avanzar (puede ser negativo)
     */
    public void move(int step) {
        int pixelDelta = step * CELL_SIZE;
        switch (direction) {
            case 'N':
                yPosition -= step;
                moveAllVertical(-pixelDelta);
                break;
            case 'S':
                yPosition += step;
                moveAllVertical(pixelDelta);
                break;
            case 'E':
                xPosition += step;
                moveAllHorizontal(pixelDelta);
                break;
            case 'W':
                xPosition -= step;
                moveAllHorizontal(-pixelDelta);
                break;
        }
        lastMoveOK = true;
    }

    /**
     * Desplaza horizontalmente, en píxeles, las tres figuras del robot
     * de forma sincronizada.
     *
     * @param delta desplazamiento horizontal en píxeles
     */
    private void moveAllHorizontal(int delta) {
        body.moveHorizontal(delta);
        antenna.moveHorizontal(delta);
        antennaTip.moveHorizontal(delta);
    }

    /**
     * Desplaza verticalmente, en píxeles, las tres figuras del robot
     * de forma sincronizada.
     *
     * @param delta desplazamiento vertical en píxeles
     */
    private void moveAllVertical(int delta) {
        body.moveVertical(delta);
        antenna.moveVertical(delta);
        antennaTip.moveVertical(delta);
    }
    
    /**
     * Cambia la dirección hacia la que mira el robot.
     *
     * @param newDirection nueva dirección: 'N', 'S', 'E' o 'W'
     */
    public void turn(char newDirection) {
        direction = newDirection;
    }

    /**
     * Indica si el último movimiento solicitado se pudo realizar.
     *
     * @return true si el último movimiento fue exitoso, false si no
     */
    public boolean isOK() {
        return lastMoveOK;
    }
    
    /**
     * Hace visible al robot en el canvas, mostrando sus tres figuras.
     */
    public void makeVisible() {
        isVisible = true;
        body.makeVisible();
        antenna.makeVisible();
        antennaTip.makeVisible();
    }

    /**
     * Oculta al robot del canvas, ocultando sus tres figuras.
     */
    public void makeInvisible() {
        isVisible = false;
        body.makeInvisible();
        antenna.makeInvisible();
        antennaTip.makeInvisible();
    }
    
    // Agregar estos dos métodos a Robot.java

    /**
     * Marca el último movimiento como fallido (usado por RobotMaze
     * cuando detecta que el robot chocó contra una pared o el borde).
     */
    public void markMoveFailed() {
        lastMoveOK = false;
    }

    /**
     * Cambia la apariencia del robot para indicar que se quedó sin vida.
     */
    public void markDead() {
        body.changeColor("gray");
        antenna.changeColor("gray");
        antennaTip.changeColor("gray");
    }
}