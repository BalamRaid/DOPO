import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Pruebas de unidad del Ciclo 4 (ruedas y símbolos con tipos).
 *
 * <p>Cubre Reqs 16-19: {@code normal/lefty/rebel} y
 * {@code normal/ephemeral/shy/giant} (giant = tipo nuevo Req19).
 * Todas corren en modo invisible, como exige la entrega.
 *
 * <p>Incluye los dos casos colectivos del wiki (identificación
 * {@code GaQu}) directamente en esta clase por decisión del equipo;
 * para entrega estricta U basta extraerlos a {@code SlotMachineCC4Test}.
 * Cada prueba responde: ¿qué debería hacer? / ¿qué no debería hacer?
 *
 * @version 1.0 (Ciclo 4 M2)
 */
public class SlotMachineC4Test {

    private SlotMachine m;

    @Before
    public void setUp() {
        m = new SlotMachine();
    }

    private void appendWheel(String type) {
        m.addWheel(Integer.MAX_VALUE, type);
    }

    private void appendSymbol(String color, String type) {
        m.addSymbol(Integer.MAX_VALUE, color, type);
    }

    // ---- creación de ruedas ----

    /**
     * ¿Qué debería hacer? addWheel con tipo debe crear la rueda pedida
     * y wheelType debe reportarla. No rompe compatibilidad con normal.
     */
    @Test
    public void addWheelShouldCreateTypedWheels() {
        m.addWheel(1, "normal");
        m.addWheel(2, "lefty");
        m.addWheel(3, "REBEL ");
        assertTrue(m.ok());
        assertEquals("normal", m.wheelType(1));
        assertEquals("lefty", m.wheelType(2));
        assertEquals("rebel", m.wheelType(3));
    }

    /**
     * ¿Qué no debería hacer? Un tipo desconocido debe fallar sin añadir nada.
     */
    @Test
    public void addWheelShouldFailOnUnknownType() {
        m.addWheel(1, "normal");
        m.addWheel(2, "turbo");
        assertFalse(m.ok());
        assertEquals(1, m.configuration().length);
    }

    // ---- rebel: vetos ----

    /**
     * ¿Qué no debería hacer? lock sobre rebel debe fallar y la rueda
     * debe seguir girando (nunca queda fija).
     */
    @Test
    public void lockRebelShouldFailAndStaySpinnable() {
        appendWheel("rebel");
        appendSymbol("red", "normal");
        appendSymbol("blue", "normal");
        m.lock(1);
        assertFalse(m.ok());
        m.spin(1, 1);
        assertTrue(m.ok());
    }

    /**
     * ¿Qué no debería hacer? swap con rebel debe fallar sin cambios.
     */
    @Test
    public void swapWithRebelShouldFailUnchanged() {
        appendWheel("normal");
        appendWheel("rebel");
        appendSymbol("red", "normal");
        appendSymbol("blue", "normal");
        m.spin(new String[]{"red", "blue"});
        m.swap(1, 2);
        assertFalse(m.ok());
        assertArrayEquals(new String[]{"red", "blue"}, m.configuration());
    }

    /**
     * ¿Qué no debería hacer? delWheel sobre rebel debe fallar sin eliminar.
     */
    @Test
    public void delRebelShouldFailWithoutRemoving() {
        appendWheel("normal");
        appendWheel("rebel");
        appendSymbol("red", "normal");
        m.delWheel(2);
        assertFalse(m.ok());
        assertEquals(2, m.configuration().length);
        assertEquals("rebel", m.wheelType(2));
    }

    // ---- lefty: copia ----

    /**
     * ¿Qué debería hacer? spin sobre lefty copia el estado final de la
     * izquierda en lugar de rotar (aquí: rueda2 copia a rueda1).
     */
    @Test
    public void leftyShouldCopyLeftOnSingleSpin() {
        appendWheel("normal");
        appendWheel("lefty");
        appendSymbol("red", "normal");
        appendSymbol("blue", "normal");
        appendSymbol("green", "normal");
        m.spin(new String[]{"red", "blue"});
        m.spin(2, 5);
        assertTrue(m.ok());
        assertEquals("red", m.configuration()[1]);
    }

    /**
     * ¿Qué debería hacer? lefty en primera posición (sin vecina) gira normal.
     */
    @Test
    public void leftyFirstWheelShouldRotateNormal() {
        appendWheel("lefty");
        appendSymbol("red", "normal");
        appendSymbol("blue", "normal");
        appendSymbol("green", "normal");
        m.spin(1, 2);
        assertTrue(m.ok());
        assertEquals("green", m.configuration()[0]);
    }

    /**
     * ¿Qué debería hacer? En spin() global la lefty termina igual a la
     * izquierda final (copia post-giro, funciona con vecina rebel).
     */
    @Test
    public void leftyShouldCopyFinalAfterGlobalSpin() {
        appendWheel("rebel");
        appendWheel("lefty");
        appendSymbol("red", "normal");
        appendSymbol("blue", "normal");
        appendSymbol("green", "normal");
        m.spin();
        assertTrue(m.ok());
        assertEquals(m.configuration()[0], m.configuration()[1]);
    }

    /**
     * ¿Qué debería hacer? spin(String[]) es asignación directa y no copia:
     * permite montar la máquina de forma determinista aun con lefty.
     */
    @Test
    public void spinArrayShouldBypassLeftyCopy() {
        appendWheel("normal");
        appendWheel("lefty");
        appendSymbol("red", "normal");
        appendSymbol("blue", "normal");
        m.spin(new String[]{"red", "blue"});
        assertTrue(m.ok());
        assertArrayEquals(new String[]{"red", "blue"}, m.configuration());
    }

    // ---- creación de símbolos ----

    /**
     * ¿Qué debería hacer? addSymbol con tipo crea el símbolo pedido.
     */
    @Test
    public void addSymbolShouldCreateTypedSymbols() {
        m.addSymbol(1, "red", "normal");
        m.addSymbol(2, "green", "ephemeral");
        m.addSymbol(3, "pink", "shy");
        m.addSymbol(4, "lime", "giant");
        assertTrue(m.ok());
        assertEquals("normal", m.symbolType("red"));
        assertEquals("ephemeral", m.symbolType("green"));
        assertEquals("shy", m.symbolType("pink"));
        assertEquals("giant", m.symbolType("lime"));
    }

    /**
     * ¿Qué no debería hacer? Tipo de símbolo desconocido falla sin añadir.
     */
    @Test
    public void addSymbolShouldFailOnUnknownType() {
        m.addSymbol(1, "red", "magico");
        assertFalse(m.ok());
        assertEquals(0, m.symbols().length);
    }

    // ---- ephemeral / giant: escala ----

    /**
     * ¿Qué debería hacer? Cada giro decrementa ephemeral hasta punto (0.15) y no baja más.
     */
    @Test
    public void ephemeralShouldShrinkToPointOnSpins() {
        appendWheel("normal");
        appendSymbol("red", "normal");
        appendSymbol("green", "ephemeral");
        assertEquals(1.0, m.symbolScale("green"), 1e-9);
        m.spin(1, 1);
        assertEquals(0.85, m.symbolScale("green"), 1e-9);
        for (int i = 0; i < 10; i++) m.spin(1, 1);
        assertEquals(0.15, m.symbolScale("green"), 1e-9);
    }

    /**
     * ¿Qué debería hacer? Cada giro incrementa giant hasta 2.0 y no sube más.
     */
    @Test
    public void giantShouldGrowToMaxOnSpins() {
        appendWheel("normal");
        appendSymbol("red", "normal");
        appendSymbol("lime", "giant");
        m.spin(1, 1);
        assertEquals(1.15, m.symbolScale("lime"), 1e-9);
        for (int i = 0; i < 10; i++) m.spin(1, 1);
        assertEquals(2.0, m.symbolScale("lime"), 1e-9);
    }

    /**
     * ¿Qué no debería hacer? La asignación directa no cambia escalas (sin girar),
     * pero sí cuenta como selección para shy.
     */
    @Test
    public void spinArrayShouldNotChangeScales() {
        appendWheel("normal");
        appendSymbol("red", "normal");
        appendSymbol("green", "ephemeral");
        m.spin(new String[]{"red"});
        assertEquals(1.0, m.symbolScale("green"), 1e-9);
    }

    // ---- shy: alternancia ----

    /**
     * ¿Qué debería hacer? placeSymbol sobre shy alterna visible/fantasma,
     * pero la lógica (configuration) sigue devolviendo su color.
     */
    @Test
    public void shyShouldToggleOnPlaceKeepingLogic() {
        appendWheel("normal");
        appendSymbol("red", "normal");
        appendSymbol("pink", "shy");
        assertTrue(m.isSymbolVisible("pink"));
        m.placeSymbol(1, "pink");
        assertFalse(m.isSymbolVisible("pink"));
        assertEquals("pink", m.configuration()[0]);
        m.placeSymbol(1, "pink");
        assertTrue(m.isSymbolVisible("pink"));
    }

    /**
     * ¿Qué debería hacer? Aterrizar por giro sobre shy también alterna.
     * ¿Qué no debería hacer? Fantasma sigue contando para distinct/jackpot.
     */
    @Test
    public void shyShouldToggleOnSpinKeepingCounts() {
        appendWheel("normal");
        appendSymbol("red", "normal");
        appendSymbol("pink", "shy");
        m.spin(new String[]{"red"});
        m.spin(1, 1);
        assertEquals("pink", m.configuration()[0]);
        assertFalse(m.isSymbolVisible("pink"));
        assertEquals(1, m.distinctSymbols());
        assertTrue(m.isJackpot());
    }

    // ---- colectivos wiki GaQu (ex-CC4Test) ----

    /**
     * [Wiki GaQu] ¿Qué debería hacer? lefty copia a vecina rebel tras giro único.
     */
    @Test
    public void leftyGaQuShouldCopyRebelNeighbour() {
        appendWheel("rebel");
        appendWheel("lefty");
        appendSymbol("red", "normal");
        appendSymbol("blue", "normal");
        m.spin(new String[]{"red", "blue"});
        m.spin(1, 1);
        String leftFinal = m.configuration()[0];
        m.spin(2, 3);
        assertTrue(m.ok());
        assertEquals(leftFinal, m.configuration()[1]);
    }

    /**
     * [Wiki GaQu] ¿Qué no debería hacer? ephemeral nunca baja de punto ni falla.
     */
    @Test
    public void ephemeralGaQuShouldNotGoBelowPoint() {
        appendWheel("normal");
        appendSymbol("red", "normal");
        appendSymbol("green", "ephemeral");
        for (int i = 0; i < 20; i++) m.spin(1, 1);
        assertTrue(m.ok());
        assertEquals(0.15, m.symbolScale("green"), 1e-9);
    }
}
