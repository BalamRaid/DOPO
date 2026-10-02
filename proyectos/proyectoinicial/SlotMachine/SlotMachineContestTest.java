import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Pruebas de unidad para {@link SlotMachineContest} (Ciclo 3).
 *
 * Cada prueba usa una {@link SlotMachine} en modo invisible como testing
 * tool, tal como exige el enunciado: se construye la máquina, se ejecuta la
 * estrategia del solver sobre ella y se comprueba el resultado mediante
 * {@code distinctSymbols()}.
 *
 * Las pruebas se diseñan respondiendo las dos preguntas del curso:
 * ¿qué debería hacer? y ¿qué no debería hacer?
 *
 * @version 1.0 (Ciclo 3)
 */
public class SlotMachineContestTest {

    /** Límite máximo de acciones permitido por el problema ICPC. */
    private static final int MAX_ACTIONS = 10000;

    /** Límite de pasos por acción permitido por el problema ICPC. */
    private static final int MAX_STEPS = 1_000_000_000;

    /** Tamaño mínimo permitido por el problema ICPC. */
    private static final int MIN_N = 3;

    /** Tamaño máximo permitido por el problema ICPC. */
    private static final int MAX_N = 50;

    /**
     * ¿Qué debería hacer? Con el tamaño mínimo (n = 3), solve debe dejar la
     * máquina en premio mayor: todas las ruedas mostrando el mismo símbolo,
     * es decir, un único símbolo distinto visible.
     */
    @Test
    public void solveShouldReachJackpotForThreeWheels() {
        SlotMachine machine = new SlotMachine(MIN_N);
        SlotMachineContest.solve(machine, MIN_N);
        assertEquals(1, machine.distinctSymbols());
    }

    /**
     * ¿Qué debería hacer? Con el tamaño máximo (n = 50), solve debe dejar la
     * máquina en premio mayor dentro del presupuesto de acciones.
     */
    @Test
    public void solveShouldReachJackpotForFiftyWheels() {
        SlotMachine machine = new SlotMachine(MAX_N);
        int[][] actions = SlotMachineContest.solve(machine, MAX_N);
        assertEquals(1, machine.distinctSymbols());
        assertTrue("No debe exceder " + MAX_ACTIONS + " acciones",
                actions.length <= MAX_ACTIONS);
    }

    /**
     * ¿Qué no debería hacer? solve no debe superar el límite de 10.000
     * acciones que impone el problema, ni siquiera en el peor tamaño.
     */
    @Test
    public void solveShouldNotExceedActionBudgetForFiftyWheels() {
        SlotMachine machine = new SlotMachine(MAX_N);
        int[][] actions = SlotMachineContest.solve(machine, MAX_N);
        assertTrue("acciones=" + actions.length, actions.length <= MAX_ACTIONS);
    }

    /**
     * ¿Qué debería hacer? Toda acción debe referenciar una rueda existente,
     * con índice basado en 1 dentro del rango [1, n].
     */
    @Test
    public void solveShouldReturnValidWheelIndices() {
        int n = 10;
        SlotMachine machine = new SlotMachine(n);
        int[][] actions = SlotMachineContest.solve(machine, n);
        assertTrue(actions.length > 0);
        for (int[] action : actions) {
            assertTrue("rueda fuera de rango: " + action[0],
                    action[0] >= 1 && action[0] <= n);
        }
    }

    /**
     * ¿Qué debería hacer? Cada giro debe respetar el rango de pasos permitido
     * por el problema (de -10^9 a 10^9).
     */
    @Test
    public void solveShouldReturnStepsWithinAllowedRange() {
        int n = 10;
        SlotMachine machine = new SlotMachine(n);
        int[][] actions = SlotMachineContest.solve(machine, n);
        for (int[] action : actions) {
            assertTrue("pasos fuera de rango: " + action[1],
                    Math.abs(action[1]) <= MAX_STEPS);
        }
    }

    /**
     * ¿Qué debería hacer? solve debe ganar para cualquier configuración
     * inicial válida, no solo para una en particular. Se ejecuta muchas veces
     * con máquinas aleatorias de distintos tamaños.
     */
    @Test
    public void solveShouldWinForManyRandomMachines() {
        for (int n = MIN_N; n <= 8; n++) {
            for (int attempt = 0; attempt < 30; attempt++) {
                SlotMachine machine = new SlotMachine(n);
                SlotMachineContest.solve(machine, n);
                assertEquals("n=" + n + " intento=" + attempt,
                        1, machine.distinctSymbols());
            }
        }
    }

    /**
     * ¿Qué debería hacer? Tras una solución exitosa, la máquina debe quedar
     * con la última operación marcada como exitosa (ok() == true).
     */
    @Test
    public void solveShouldLeaveMachineOk() {
        SlotMachine machine = new SlotMachine(5);
        SlotMachineContest.solve(machine, 5);
        assertTrue(machine.ok());
    }

    /**
     * ¿Qué no debería hacer? solve no debe lanzar excepciones con el tamaño
     * mínimo permitido y, además, debe alcanzar el premio mayor.
     */
    @Test
    public void solveShouldNotThrowForMinimumSize() {
        SlotMachine machine = new SlotMachine(MIN_N);
        SlotMachineContest.solve(machine, MIN_N);
        assertEquals(1, machine.distinctSymbols());
    }
}
