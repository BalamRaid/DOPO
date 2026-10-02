import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Clase de pruebas de unidad compartida (creación colectiva) para
 * {@link SlotMachineContest}, correspondiente al Ciclo 3.
 *
 * Los nombres de los casos de prueba incluyen la identificación de los
 * autores: "GaQu" (iniciales de los primeros apellidos con la primera letra
 * del segundo, en orden alfabético).
 *
 * @version 1.0 (Ciclo 3)
 */
public class SlotMachineContestCTest {

    /** Límite máximo de acciones permitido por el problema ICPC. */
    private static final int MAX_ACTIONS = 10000;

    /**
     * ¿Qué debería hacer? Con tres ruedas, la solución compartida debe dejar
     * la máquina en premio mayor: un único símbolo distinto visible.
     */
    @Test
    public void solveGaQuShouldReachJackpotForThreeWheels() {
        SlotMachine machine = new SlotMachine(3);
        SlotMachineContest.solve(machine, 3);
        assertEquals(1, machine.distinctSymbols());
    }

    /**
     * ¿Qué no debería hacer? La solución compartida no debe usar más de
     * 10.000 acciones, ni siquiera con el tamaño máximo de la máquina.
     */
    @Test
    public void solveGaQuShouldNotExceedTenThousandActions() {
        SlotMachine machine = new SlotMachine(50);
        int[][] actions = SlotMachineContest.solve(machine, 50);
        assertTrue("acciones=" + actions.length, actions.length <= MAX_ACTIONS);
    }
}
