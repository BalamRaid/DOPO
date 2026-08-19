import javax.swing.JOptionPane;

/**
 * Mini-aplicación del juego RobotMaze. Gestiona un laberinto de tamaño
 * configurable, con entrada, salida y paredes, y controla a un Robot
 * que se mueve dentro de él consumiendo vidas al chocar.
 *
 * @author Kevin
 * @version 1.0
 */
public class RobotMaze {
    private static final int CELL_SIZE = 40;

    private int size;
    private boolean[][] walls;
    private Rectangle[][] wallShapes;
    private int entryX, entryY;
    private int exitX, exitY;
    private Robot robot;
    private int lives;
    private boolean gameStarted;
    private boolean gameEnded;

    /**
     * Crea un laberinto cuadrado de size x size casillas, ubicando la
     * entrada en una posición aleatoria y la salida en la cara opuesta.
     *
     * @param size tamaño del laberinto (size x size casillas)
     */
    public RobotMaze(int size) {
        this.size = size;
        walls = new boolean[size][size];
        wallShapes = new Rectangle[size][size];
        gameStarted = false;
        gameEnded = false;
        placeEntryAndExit();
        drawEntryExit();
    }

    private void placeEntryAndExit() {
        int side = (int) (Math.random() * 4);
        int coord = (int) (Math.random() * size);
        switch (side) {
            case 0: entryX = coord; entryY = 0; exitX = size - 1 - coord; exitY = size - 1; break;
            case 1: entryX = coord; entryY = size - 1; exitX = size - 1 - coord; exitY = 0; break;
            case 2: entryX = size - 1; entryY = coord; exitX = 0; exitY = size - 1 - coord; break;
            default: entryX = 0; entryY = coord; exitX = size - 1; exitY = size - 1 - coord; break;
        }
    }

    private void drawEntryExit() {
        Circle entryMarker = new Circle();
        entryMarker.changeSize(15);
        entryMarker.changeColor("green");
        entryMarker.moveHorizontal(entryX * CELL_SIZE - 20 + 12);
        entryMarker.moveVertical(entryY * CELL_SIZE - 15 + 12);
        entryMarker.makeVisible();

        Circle exitMarker = new Circle();
        exitMarker.changeSize(15);
        exitMarker.changeColor("blue");
        exitMarker.moveHorizontal(exitX * CELL_SIZE - 20 + 12);
        exitMarker.moveVertical(exitY * CELL_SIZE - 15 + 12);
        exitMarker.makeVisible();
    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && x < size && y >= 0 && y < size;
    }

    /**
     * Agrega una pared en la casilla (x, y). Solo se permite antes de
     * iniciar el juego y en casillas distintas a la entrada o salida.
     *
     * @param x columna de la pared
     * @param y fila de la pared
     */
    public void addWall(int x, int y) {
        if (gameStarted) {
            JOptionPane.showMessageDialog(null,
                "No se pueden agregar paredes después de iniciar el juego.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!inBounds(x, y) || (x == entryX && y == entryY) || (x == exitX && y == exitY)) {
            JOptionPane.showMessageDialog(null,
                "Posición inválida para colocar una pared.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        walls[x][y] = true;
        Rectangle wallShape = new Rectangle();
        wallShape.changeSize(CELL_SIZE, CELL_SIZE);
        wallShape.changeColor("black");
        wallShape.moveHorizontal(x * CELL_SIZE - 70);
        wallShape.moveVertical(y * CELL_SIZE - 15);
        wallShape.makeVisible();
        wallShapes[x][y] = wallShape;
    }

    /**
     * Inicia el juego: ubica al robot en la entrada con 10 puntos de vida.
     */
    public void startGame() {
        if (gameStarted) {
            JOptionPane.showMessageDialog(null, "El juego ya fue iniciado.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        gameStarted = true;
        gameEnded = false;
        lives = 10;
        robot = new Robot(entryX, entryY);
        robot.makeVisible();
    }

    /**
     * Mueve el robot steps casillas en su dirección actual. Si choca
     * contra una pared o un borde, pierde 1 punto de vida.
     *
     * @param steps número de casillas a avanzar
     */
    public void moveRobot(int steps) {
        if (!gameStarted || gameEnded) {
            JOptionPane.showMessageDialog(null, "El juego no está activo.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int[] current = robot.coordinates();
        int targetX = current[0], targetY = current[1];
        switch (robot.direction()) {
            case 'N': targetY -= steps; break;
            case 'S': targetY += steps; break;
            case 'E': targetX += steps; break;
            case 'W': targetX -= steps; break;
        }

        if (!inBounds(targetX, targetY) || walls[targetX][targetY]) {
            lives--;
            if (inBounds(targetX, targetY) && wallShapes[targetX][targetY] != null) {
                wallShapes[targetX][targetY].changeColor("red");
            }
            robot.markMoveFailed();
            if (lives <= 0) {
                robot.markDead();
            }
        } else {
            robot.move(steps);
        }
        isGameOver();
    }

    /**
     * Cambia la dirección hacia la que mira el robot.
     *
     * @param direction nueva dirección: 'N', 'S', 'E' o 'W'
     */
    public void turnRobot(char direction) {
        if (!gameStarted || gameEnded) {
            JOptionPane.showMessageDialog(null, "El juego no está activo.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        robot.turn(direction);
    }

    /**
     * Retorna la cantidad de vidas disponibles del robot.
     *
     * @return vidas restantes
     */
    public int getLives() {
        return lives;
    }

    /**
     * Indica si el juego terminó, ya sea porque el robot llegó a la
     * salida o porque se quedó sin vida.
     *
     * @return true si el juego terminó, false si sigue activo
     */
    public boolean isGameOver() {
        if (!gameStarted) return false;
        int[] pos = robot.coordinates();
        boolean reachedExit = pos[0] == exitX && pos[1] == exitY;
        boolean outOfLives = lives <= 0;
        if (reachedExit || outOfLives) {
            gameEnded = true;
        }
        return gameEnded;
    }

    /**
     * Termina el juego manualmente, sin importar su estado actual.
     */
    public void endGame() {
        gameEnded = true;
        JOptionPane.showMessageDialog(null, "Juego terminado.",
            "RobotMaze", JOptionPane.INFORMATION_MESSAGE);
    }
}