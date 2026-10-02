import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for SlotMachine behaviors that are NOT covered by
 * SlotMachineC2Test: symbol bookkeeping, placeSymbol, jackpot,
 * distinctSymbols, and failure paths of delWheel/delSymbol/spin().
 * All tests run with the machine in invisible mode.
 */
public class SlotMachineExtraTest {

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

    // ---- addSymbol ----

    @Test
    public void addSymbolShouldFailWhenColorIsInvalid() {
        m.addSymbol(1, "notacolor");
        assertFalse(m.ok());
        assertEquals(0, m.symbols().length);
    }

    @Test
    public void addSymbolShouldFailWhenColorIsDuplicated() {
        appendSymbols("red");
        m.addSymbol(1, "red");
        assertFalse(m.ok());
        assertEquals(1, m.symbols().length);
    }

    @Test
    public void addSymbolShouldKeepVisibleSymbolAfterInsertion() {
        appendWheels(1);
        appendSymbols("red", "blue");
        m.spin(new String[]{"blue"});
        m.addSymbol(1, "green"); // se inserta antes de "blue"
        assertTrue(m.ok());
        assertEquals("blue", m.configuration()[0]);
    }

    // ---- delSymbol ----

    @Test
    public void delSymbolShouldFailWhenColorDoesNotExist() {
        appendSymbols("red");
        m.delSymbol("blue");
        assertFalse(m.ok());
        assertEquals(1, m.symbols().length);
    }

    @Test
    public void delSymbolShouldKeepConfigurationConsistent() {
        appendWheels(1);
        appendSymbols("red", "blue", "green");
        m.spin(new String[]{"green"});
        m.delSymbol("red");
        assertTrue(m.ok());
        assertEquals("green", m.configuration()[0]);
    }

    // ---- delWheel ----

    @Test
    public void delWheelShouldFailWhenThereAreNoWheels() {
        m.delWheel(1);
        assertFalse(m.ok());
    }

    // ---- placeSymbol ----

    @Test
    public void placeSymbolShouldSetSymbolDirectly() {
        appendWheels(1);
        appendSymbols("red", "blue");
        m.placeSymbol(1, "blue");
        assertTrue(m.ok());
        assertEquals("blue", m.configuration()[0]);
    }

    @Test
    public void placeSymbolShouldFailWhenColorDoesNotExist() {
        appendWheels(1);
        appendSymbols("red");
        m.placeSymbol(1, "blue");
        assertFalse(m.ok());
        assertEquals("red", m.configuration()[0]);
    }

    // ---- swap ----

    @Test
    public void swapShouldFailWhenOneWheelIsLocked() {
        appendWheels(2);
        appendSymbols("red", "blue");
        m.spin(new String[]{"red", "blue"});
        m.lock(1);
        m.swap(1, 2);
        assertFalse(m.ok());
        assertArrayEquals(new String[]{"red", "blue"}, m.configuration());
    }

    // ---- spin() ----

    @Test
    public void spinAllShouldFailWhenAllWheelsAreLocked() {
        appendWheels(1);
        appendSymbols("red", "blue");
        m.lock(1);
        m.spin();
        assertFalse(m.ok());
    }

    @Test
    public void spinAllShouldFailWhenThereAreNoWheels() {
        m.spin();
        assertFalse(m.ok());
    }

    // ---- isJackpot / distinctSymbols ----

    @Test
    public void isJackpotShouldBeTrueOnlyWhenAllWheelsMatch() {
        appendWheels(2);
        appendSymbols("red", "blue");
        m.spin(new String[]{"red", "red"});
        assertTrue(m.isJackpot());
        m.spin(new String[]{"red", "blue"});
        assertFalse(m.isJackpot());
    }

    @Test
    public void distinctSymbolsShouldCountDistinctVisibleColors() {
        appendWheels(3);
        appendSymbols("red", "blue");
        m.spin(new String[]{"red", "red", "blue"});
        assertEquals(2, m.distinctSymbols());
    }
}
