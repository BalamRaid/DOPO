import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the cycle 2 additions to SlotMachine: lock/unlock,
 * swap, spin(wheel, steps) and spin(String[]). All tests run with the
 * machine in invisible mode, as required by the assignment.
 */
public class SlotMachineC2Test {

    private SlotMachine m;

    @Before
    public void setUp() {
        m = new SlotMachine();
    }

    private void appendWheels(int n) {
        for (int i = 0; i < n; i++) m.addWheel(Integer.MAX_VALUE);
    }

    private void appendSymbols(String... colors) {
        for (String c : colors) m.addSymbol(Integer.MAX_VALUE, c);
    }

    // ---- lock / unlock ----

    @Test
    public void lockShouldPreventSpinningThatWheel() {
        appendWheels(2);
        appendSymbols("red", "blue", "green");
        m.spin(new String[]{"red", "blue"});
        m.lock(1);
        String before = m.configuration()[0];
        m.spin(1);
        assertFalse(m.ok());
        assertEquals(before, m.configuration()[0]);
    }

    @Test
    public void unlockShouldAllowSpinningAgain() {
        appendWheels(1);
        appendSymbols("red", "blue");
        m.lock(1);
        m.unlock(1);
        m.spin(1);
        assertTrue(m.ok());
    }

    @Test
    public void lockShouldFailWhenThereAreNoWheels() {
        m.lock(1);
        assertFalse(m.ok());
    }

    // ---- swap ----

    @Test
    public void swapShouldExchangeVisibleSymbols() {
        appendWheels(2);
        appendSymbols("red", "blue");
        m.spin(new String[]{"red", "blue"});
        m.swap(1, 2);
        assertArrayEquals(new String[]{"blue", "red"}, m.configuration());
    }

    @Test
    public void swapWithSamePositionShouldSucceedAsNoOp() {
        appendWheels(2);
        appendSymbols("red", "blue");
        m.spin(new String[]{"red", "blue"});
        m.swap(1, 1);
        assertTrue(m.ok());
        assertArrayEquals(new String[]{"red", "blue"}, m.configuration());
    }

    @Test
    public void swapShouldFailWhenThereAreNoWheels() {
        m.swap(1, 2);
        assertFalse(m.ok());
    }

    // ---- spin(wheel, steps) ----

    @Test
    public void spinWithStepsShouldRotateWheelExactly() {
        appendWheels(1);
        appendSymbols("red", "blue", "green"); // wheel starts at index 0 = red
        m.spin(1, 2); // +2 -> index 2 = green
        assertTrue(m.ok());
        assertEquals("green", m.configuration()[0]);
    }

    @Test
    public void spinWithNegativeStepsShouldRotateBackward() {
        appendWheels(1);
        appendSymbols("red", "blue", "green");
        m.spin(1, -1); // floorMod(-1,3) = 2 -> green
        assertEquals("green", m.configuration()[0]);
    }

    @Test
    public void spinWithStepsShouldFailWhenWheelIsLocked() {
        appendWheels(1);
        appendSymbols("red", "blue");
        m.lock(1);
        m.spin(1, 1);
        assertFalse(m.ok());
    }

    @Test
    public void spinWithStepsShouldFailWhenThereAreNoSymbols() {
        appendWheels(1);
        m.spin(1, 1);
        assertFalse(m.ok());
    }

    // ---- spin(String[]) ----

    @Test
    public void spinArrayShouldSetExactConfiguration() {
        appendWheels(2);
        appendSymbols("red", "blue", "green");
        m.spin(new String[]{"green", "red"});
        assertTrue(m.ok());
        assertArrayEquals(new String[]{"green", "red"}, m.configuration());
    }

    @Test
    public void spinArrayShouldFailAndLeaveConfigurationUnchangedOnSizeMismatch() {
        appendWheels(2);
        appendSymbols("red", "blue");
        m.spin(new String[]{"red", "blue"});
        String[] before = m.configuration();
        m.spin(new String[]{"red"});
        assertFalse(m.ok());
        assertArrayEquals(before, m.configuration());
    }

    @Test
    public void spinArrayShouldFailAndLeaveConfigurationUnchangedOnInvalidColor() {
        appendWheels(2);
        appendSymbols("red", "blue");
        m.spin(new String[]{"red", "blue"});
        String[] before = m.configuration();
        m.spin(new String[]{"red", "chocolate"});
        assertFalse(m.ok());
        assertArrayEquals(before, m.configuration());
    }

    @Test
    public void spinArrayShouldSkipLockedWheels() {
        appendWheels(2);
        appendSymbols("red", "blue");
        m.spin(new String[]{"red", "blue"});
        m.lock(1);
        m.spin(new String[]{"blue", "red"});
        assertEquals("red", m.configuration()[0]);  // fija: no cambió
        assertEquals("red", m.configuration()[1]);  // no fija: sí cambió
    }

    @Test
    public void spinArrayShouldSucceedEvenIfAlreadyAtTargetConfiguration() {
        appendWheels(1);
        appendSymbols("red", "blue");
        m.spin(new String[]{"red"});
        m.spin(new String[]{"red"}); // ya estaba ahí
        assertTrue(m.ok());
    }
}