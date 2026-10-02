/**
 * Pruebas de aceptación del ciclo 3 (SlotMachine + SlotMachineContest).
 * Se ejecutan en BlueJ: clic derecho sobre la clase -> void escenarioX().
 * No requieren JUnit: lanzan AssertionError si algo falla.
 */
public class SlotMachineContestAcceptance {

    public static void escenario1() {
        // Escenario 1: solve(n) debe devolver un plan con el formato
        // exigido por el problema (i j) y dentro del límite de 10.000.
        int[] ns = {3, 8, 25, 50};
        for (int n : ns) {
            int[][] plan = SlotMachineContest.solve(n);
            verificar(plan != null && plan.length > 0,
                      "El plan no puede ser vacío");
            verificar(plan.length <= 10000,
                      "Se excedió el límite de 10.000 acciones (n=" + n + ")");
            for (int[] accion : plan) {
                verificar(accion.length == 2, "Cada acción debe ser {rueda, pasos}");
                int rueda = accion[0];
                int pasos = accion[1];
                verificar(rueda >= 1 && rueda <= n, "Rueda fuera de rango");
                verificar(Math.abs(pasos) <= 1000000000L, "|pasos| excede 10^9");
            }
            System.out.println("E1 OK: n=" + n + " -> " + plan.length + " acciones");
        }
    }

    public static void escenario2() {
        // Escenario 2: contrato del toolkit de testing.
        // distinctSymbols() y spin(wheel, steps) determinan los cambios.
        SlotMachine m = new SlotMachine(4);
        m.makeInvisible();  // la máquina debe funcionar invisible
        String[] inicial = m.configuration();
        verificar(inicial.length == 4, "Deben haber 4 símbolos visibles");

        int antes = m.distinctSymbols();               // k inicial
        verificar(antes >= 2,                            // no arranca en jackpot
                  "La configuración inicial no debe ser todas iguales");

        m.spin(1, 1);
        int despues = m.distinctSymbols();
        verificar(despues >= 1 && despues <= 4, "k siempre en [1, n]");

        // forzamos la solución con la configuración objetivo? No: el punto es
        // que distinctSymbols() == 1 <=> jackpot ganador
        System.out.println("E2 OK: distinctSymbols inicial=" + antes +
                           ", tras spin=" + despues);
    }

    public static void escenario3() {
        // Escenario 3 (visual): la solución se puede simular.
        // La máquina nace invisible en solve y visible en simulate.
        SlotMachineContest.simulate(5);
        // Evidencia visual: la tragamonedas gira y termina con las 5
        // ruedas mostrando el mismo símbolo (jackpot).
    }

    private static void verificar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError("FALLO: " + mensaje);
        }
    }
}