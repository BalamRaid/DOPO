import java.util.ArrayList;
import java.util.List;

/**
 * Solución del Problema I - Slot Machine de ICPC 2025.
 *
 * La solución trabaja únicamente con la información permitida por
 * SlotMachine:
 *
 * - SlotMachine(n)
 * - spin(wheel, steps)
 * - distinctSymbols()
 *
 * La máquina permanece invisible durante solve().
 * Durante simulate() se hace visible para poder observar el proceso.
 */
public class SlotMachineContest {

    /**
     * Resuelve el problema de la tragamonedas.
     *
     * @param n número de ruedas y símbolos
     * @return arreglo de acciones realizadas sobre la máquina
     */
    public static int[][] solve(int n) {
        SlotMachine m = new SlotMachine(n);

        return solveMachine(m, n);
    }

    /**
     * Ejecuta visualmente la solución sobre una máquina.
     *
     * @param n número de ruedas y símbolos
     */
    public static void simulate(int n) {
        SlotMachine m = new SlotMachine(n);

        m.makeVisible();

        solveMachine(m, n);
    }

    /**
     * Ejecuta la estrategia completa sobre la máquina.
     *
     * La estrategia tiene tres fases:
     *
     * 1. Hacer que todas las ruedas muestren símbolos distintos.
     * 2. Encontrar el orden relativo de las ruedas.
     * 3. Llevar todas las ruedas al mismo símbolo.
     */
    private static int[][] solveMachine(SlotMachine m, int n) {

        List<int[]> actions = new ArrayList<>();

        /*
         * ---------------------------------------------------------
         * FASE 1
         * ---------------------------------------------------------
         *
         * Dejamos todas las ruedas mostrando símbolos diferentes.
         *
         * Para cada rueda:
         *   - probamos todas sus posiciones;
         *   - observamos distinctSymbols();
         *   - conservamos la posición que produzca el máximo.
         *
         * Al terminar esta fase:
         *
         *      distinctSymbols() == n
         *
         * es decir, todas las ruedas muestran símbolos diferentes.
         */

        for (int wheel = 1; wheel <= n; wheel++) {

            int[] readings = new int[n];

            /*
             * Guardamos la posición desde la que comienza el recorrido.
             *
             * Esta posición es solo para nuestro propio control.
             * No estamos leyendo información privada de SlotMachine.
             */
            int currentPosition = 0;

            /*
             * La primera posición ya está visible.
             */
            readings[0] = m.distinctSymbols();

            /*
             * Recorremos las otras n-1 posiciones.
             */
            for (int position = 1; position < n; position++) {

                m.spin(wheel, 1);
                actions.add(new int[]{wheel, 1});

                currentPosition++;

                readings[position] = m.distinctSymbols();
            }

            /*
             * Buscamos la posición que produjo el mayor número
             * de símbolos distintos.
             */
            int bestPosition = 0;

            for (int position = 1; position < n; position++) {

                if (readings[position] > readings[bestPosition]) {
                    bestPosition = position;
                }
            }

            /*
             * Después de los n-1 giros estamos en la última
             * posición del recorrido.
             *
             * Volvemos directamente a la mejor posición.
             */
            int delta = bestPosition - currentPosition;

            delta = normalizeRotation(delta, n);

            if (delta != 0) {
                m.spin(wheel, delta);
                actions.add(new int[]{wheel, delta});
            }
        }

        /*
         * En este punto todas las ruedas deben mostrar símbolos
         * diferentes.
         */

        /*
         * ---------------------------------------------------------
         * FASE 2
         * ---------------------------------------------------------
         *
         * Descubrimos qué rueda está inmediatamente adelante
         * de cada rueda.
         *
         * Supongamos que tenemos:
         *
         *      rueda i -> símbolo A
         *      rueda j -> símbolo B
         *
         * y B es justamente el siguiente símbolo de A.
         *
         * Si hacemos:
         *
         *      i +1
         *      j -1
         *
         * ambas vuelven a mostrar A/B intercambiados y seguimos
         * teniendo n símbolos distintos.
         *
         * Para cualquier otra rueda j esto NO ocurre.
         *
         * next[i] = j
         *
         * significa que j está inmediatamente adelante de i.
         */

        int[] next = new int[n + 1];

        for (int wheel = 1; wheel <= n; wheel++) {

            /*
             * Avanzamos la rueda actual una posición.
             */
            m.spin(wheel, 1);
            actions.add(new int[]{wheel, 1});

            int successor = -1;

            /*
             * Probamos las demás ruedas.
             */
            for (int candidate = 1; candidate <= n; candidate++) {

                if (candidate == wheel) {
                    continue;
                }

                /*
                 * Retrocedemos la candidata una posición.
                 */
                m.spin(candidate, -1);
                actions.add(new int[]{candidate, -1});

                /*
                 * Si volvemos a tener los n símbolos distintos,
                 * candidate era exactamente la rueda siguiente
                 * de wheel.
                 */
                if (m.distinctSymbols() == n) {

                    successor = candidate;

                    /*
                     * Deshacemos inmediatamente el movimiento
                     * de candidate.
                     */
                    m.spin(candidate, 1);
                    actions.add(new int[]{candidate, 1});

                    break;
                }

                /*
                 * No era la candidata correcta.
                 * Restauramos su posición original.
                 */
                m.spin(candidate, 1);
                actions.add(new int[]{candidate, 1});
            }

            /*
             * Restauramos también la rueda que habíamos avanzado.
             *
             * La máquina vuelve a la configuración con todos
             * los símbolos diferentes.
             */
            m.spin(wheel, -1);
            actions.add(new int[]{wheel, -1});

            next[wheel] = successor;
        }

        /*
         * ---------------------------------------------------------
         * FASE 3
         * ---------------------------------------------------------
         *
         * Ahora tenemos un ciclo:
         *
         *      rueda 1 -> rueda X -> rueda Y -> ...
         *
         * donde cada flecha significa:
         *
         * "esta rueda muestra el siguiente símbolo de la anterior".
         *
         * Si la rueda inicial muestra A:
         *
         *      rueda 1 = A
         *      siguiente = B
         *      siguiente = C
         *      siguiente = D
         *      ...
         *
         * Entonces:
         *
         *      B necesita -1
         *      C necesita -2
         *      D necesita -3
         *      ...
         *
         * para que todas terminen mostrando A.
         */

        int currentWheel = 1;

        for (int distance = 1; distance < n; distance++) {

            currentWheel = next[currentWheel];

            /*
             * Esta rueda está 'distance' posiciones adelante
             * de la rueda 1.
             */
            m.spin(currentWheel, -distance);
            actions.add(new int[]{currentWheel, -distance});
        }

        /*
         * Convertimos la lista de acciones al formato exigido
         * por solve().
         */
        return actions.toArray(new int[0][]);
    }

    /**
     * Normaliza una rotación para utilizar el recorrido más corto
     * alrededor de la rueda.
     *
     * Por ejemplo, con n = 10:
     *
     *      +9 equivale a -1
     *      -9 equivale a +1
     *
     * Esto no cambia la solución; solamente evita giros
     * innecesariamente largos durante simulate().
     */
    private static int normalizeRotation(int delta, int n) {

        delta %= n;

        if (delta > n / 2) {
            delta -= n;
        }

        if (delta < -n / 2) {
            delta += n;
        }

        return delta;
    }
}