/**
 * Simula una máquina tragamonedas con un número configurable de ruedas y
 * símbolos, inspirada en el Problema I ("Slot Machine") de las Finales
 * Mundiales de ICPC 2025.
 * Una SlotMachine contiene una única secuencia compartida de símbolos
 * (identificados mediante nombres de colores CSS estándar) utilizada por
 * todas las ruedas. Cada rueda no posee sus propios símbolos, sino que
 * únicamente recuerda qué posición de esa secuencia compartida está
 * mostrando actualmente. Las ruedas y los símbolos pueden añadirse o
 * eliminarse dinámicamente, las ruedas pueden girarse aleatoriamente,
 * girarse un número exacto de posiciones, fijarse (lock) para impedir que
 * giren, intercambiarse de posición entre sí, o configurarse directamente
 * —de forma individual o en bloque— para mostrar un símbolo específico. La
 * máquina puede indicar si todas las ruedas muestran actualmente el mismo
 * símbolo (premio mayor).
 * La máquina puede funcionar en modo visible, dibujándose a sí misma y a
 * sus símbolos sobre un {@link Canvas}, o en modo invisible, en el cual
 * toda la lógica continúa funcionando, pero no se realiza ningún dibujo
 * ni se muestran cuadros de diálogo de error. El resultado de la última
 * operación realizada sobre la máquina puede consultarse en cualquier
 * momento mediante {@link #ok()}, sin depender de excepciones.
 *
 * @version 2.0 (Ciclo 2)
 */

import java.util.ArrayList;
import java.util.List;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

public class SlotMachine {
    private List<Wheel> wheels;
    private boolean visible;
    private boolean lastOk;
    private List<Symbol> symbols;
    private java.util.Random random;
    private Canvas canvas;
    private static final int SPIN_STEP_DELAY_MS = 220;

    /**
     * Creates a slot machine with no wheels, invisible by default.
     */
    public SlotMachine() {
        wheels = new ArrayList<>();
        symbols = new ArrayList<>();
        random = new java.util.Random();
        visible = false;
        lastOk = true;
    }

    /**
    * Añade una nueva rueda vacía en la posición indicada (basada en 1).
    * Si la posición está fuera de rango, se ajusta a la posición válida más cercana.
    */
    public void addWheel(int pos) {
        int clamped = clamp(pos, 1, wheels.size() + 1);
        wheels.add(clamped - 1, new Wheel());
        lastOk = true;
        refresh();
    }

    /**
    * Elimina la rueda de la posición indicada (basada en 1).
    * La operación falla (ok() == false) si no hay ruedas para eliminar.
    */

    public void delWheel(int pos) {
        if (wheels.isEmpty()) {
            fail("No hay ruedas para eliminar.");
            return;
        }
        int clamped = clamp(pos, 1, wheels.size());
        Wheel removed = wheels.remove(clamped - 1);
        if (visible && canvas != null) {
            canvas.erase(removed);
        }
        lastOk = true;
        refresh();
    }

    /**
    * Añade un nuevo símbolo del color indicado en la posición especificada (basada en 1).
    * Si la posición está fuera de rango, se ajusta a la posición válida más cercana.
    * La operación falla si el color no es un color CSS válido o si ya existe
    * un símbolo con ese color.
    */
    public void addSymbol(int pos, String color) {
        if (!CssColors.isValid(color)) {
            fail("'" + color + "' no es un color CSS válido.");
            return;
        }
        if (colorExists(color)) {
            fail("Ya existe un símbolo con el color '" + color + "'.");
            return;
        }
        int previousSize = symbols.size();
        int clamped = clamp(pos, 1, symbols.size() + 1);
        symbols.add(clamped - 1, new Symbol(color));
        if (previousSize > 0) {
            for (Wheel w : wheels) {
                w.adjustForInsertion(clamped - 1);
            }
        }
        lastOk = true;
        refresh();
    }

    /**
    * Elimina el símbolo que tiene el color indicado.
    * La operación falla si no existe un símbolo con ese color.
    */

    public void delSymbol(String color) {
        int index = indexOfColor(color);
        if (index == -1) {
            fail("No existe un símbolo con el color '" + color + "'.");
            return;
        }
        symbols.remove(index);
        for (Wheel w : wheels) {
            w.adjustForRemoval(index, symbols.size());
        }
        lastOk = true;
        refresh();
    }
    
    /**
    * Establece la rueda en la posición indicada (basada en 1 y ajustada al rango válido)
    * para que muestre directamente el símbolo del color indicado, sin girar.
    * La operación falla (ok() == false) si no hay ruedas o si el color no existe
    * en la secuencia compartida de símbolos.
    */
    public void placeSymbol(int wheel, String symbol) {
        if (wheels.isEmpty()) {
            fail("No hay ruedas.");
            return;
        }
        int index = indexOfColor(symbol);
        if (index == -1) {
            fail("No existe el símbolo '" + symbol + "'.");
            return;
        }
        int clamped = clamp(wheel, 1, wheels.size());
        wheels.get(clamped - 1).setVisibleIndex(index);
        lastOk = true;
        refresh();
    }

    /**
     * Gira la rueda en la posición indicada (basada en 1 y ajustada al rango
     * válido) hasta un símbolo aleatorio de la secuencia compartida de
     * símbolos. Falla si no hay símbolos cargados o si la rueda está fija
     * (locked).
     */
    public void spin(int wheel) {
        if (symbols.isEmpty()) {
            fail("No se puede girar: no hay símbolos cargados.");
            return;
        }
        Wheel target = spinnableWheelAt(wheel);
        if (target == null) return;  // fail() ya se llamó adentro de spinnableWheelAt
        int steps = random.nextInt(symbols.size()) + 1;
        animatedRotate(target, steps);
        lastOk = true;
        refresh();
    }

    /**
     * Gira cada rueda de forma independiente hasta un símbolo aleatorio. Las
     * ruedas fijas (locked) se omiten y conservan su símbolo actual. Falla
     * si no hay ruedas, si no hay símbolos cargados, o si todas las ruedas
     * están fijas.
     */
    public void spin() {
        if (wheels.isEmpty()) {
            fail("No hay ruedas para girar.");
            return;
        }
        if (symbols.isEmpty()) {
            fail("No se puede girar: no hay símbolos cargados.");
            return;
        }
        java.util.List<Wheel> spinning = new java.util.ArrayList<>();
        java.util.List<Integer> steps = new java.util.ArrayList<>();
        for (Wheel w : wheels) {
            if (!w.isLocked()) {
                spinning.add(w);
                steps.add(random.nextInt(symbols.size()) + 1);
            }
        }
        if (spinning.isEmpty()) {
            fail("Todas las ruedas están fijas; no hay nada que girar.");
            return;
        }
        animatedMultiRotate(spinning, steps);
        lastOk = true;
        refresh();
    }
    
    /**
    * Comprueba si existe un símbolo con el color indicado.
    *
    * @param color color que se desea comprobar.
    * @return true si existe un símbolo con ese color; false en caso contrario.
    */
    private boolean colorExists(String color) {
        return indexOfColor(color) != -1;
    }
    
    /**
    * Busca el índice del símbolo que tiene el color indicado.
    *
    * @param color color del símbolo que se desea buscar.
    * @return el índice del símbolo si existe; -1 si no se encuentra.
    */
    private int indexOfColor(String color) {
        for (int i = 0; i < symbols.size(); i++) {
            if (symbols.get(i).getColor().equals(color)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
    * Devuelve los colores de todos los símbolos de la secuencia compartida,
    * en orden, comenzando desde la posición 1.
    */
    public String[] symbols() {
        String[] result = new String[symbols.size()];
        for (int i = 0; i < symbols.size(); i++) {
            result[i] = symbols.get(i).getColor();
        }
        return result;
    }
    
    /**
    * Devuelve el número de colores distintos que están actualmente visibles en todas
    * las ruedas. Las ruedas que no tienen ningún símbolo visible (null) no se cuentan.
    */
    public int distinctSymbols() {
        String[] config = configuration();
        java.util.Set<String> distinct = new java.util.HashSet<>();
        for (String color : config) {
            if (color != null) {
                distinct.add(color);
            }
        }
        return distinct.size();
    }

    /**
    * Devuelve true si la máquina tiene al menos una rueda y todas las ruedas
    * muestran el mismo símbolo que no es null.
    */
    public boolean isJackpot() {
        String[] config = configuration();
        if (config.length == 0) {
            return false;
        }
        String first = config[0];
        if (first == null) {
            return false;
        }
        for (String color : config) {
            if (color == null || !color.equals(first)) {
                return false;
            }
        }
        return true;
    }
    
    /**
    * Devuelve los colores actualmente visibles en cada rueda, de izquierda a derecha.
    */
    public String[] configuration() {
        String[] result = new String[wheels.size()];
        for (int i = 0; i < wheels.size(); i++) {
            result[i] = visibleColorOf(wheels.get(i));
        }
        return result;
    }

    /**
    * Obtiene el color actualmente visible en la rueda indicada.
    *
    * @param wheel rueda de la que se desea obtener el color visible.
    * @return el color visible de la rueda; null si no hay símbolos.
    */
    private String visibleColorOf(Wheel wheel) {
        if (symbols.isEmpty()) {
            return null;
        }
        return symbols.get(wheel.getVisibleIndex()).getColor();
    }
    
    /**
    * Ajusta una posición basada en 1 para que se encuentre entre el valor mínimo y máximo.
    */
    private int clamp(int pos, int min, int max) {
        if (pos < min) return min;
        if (pos > max) return max;
        return pos;
    }

    /**
     * Hace visible la máquina, creando (si aún no existe) el {@link Canvas}
     * compartido y dibujando en él el estado actual de la máquina.
     */
    public void makeVisible() {
        canvas = Canvas.getCanvas();
        visible = true;
        lastOk = true;
        refresh();
    }

    /**
     * Oculta la máquina, eliminando del lienzo las ruedas dibujadas antes
     * de ocultar la ventana del {@link Canvas}.
     */
    public void makeInvisible() {
        if (canvas != null) {
            for (Wheel w : wheels) {
                canvas.erase(w);
            }
            canvas.setVisible(false);
        }
        visible = false;
        lastOk = true;
    }

    /**

    * Actualiza la representación visual de la máquina en el lienzo.
    * Ajusta el tamaño del lienzo según el número de ruedas y muestra el efecto
    * visual de premio mayor cuando todas las ruedas muestran el mismo símbolo.
    * También dibuja o elimina los símbolos visibles de cada rueda.
    */
    private void refresh() {
        if (!visible || canvas == null) return;
        int width = Math.max(120, wheels.size() * 70 + 20);
        canvas.resize(width, 200);

        if (isJackpot()) {
            canvas.draw("jackpotGlow", "gold",
                new java.awt.geom.Rectangle2D.Double(5, 5, width - 10, 190));
        } else {
            canvas.erase("jackpotGlow");
        }

        for (Wheel w : wheels) {
            String color = visibleColorOf(w);
            if (color == null) {
                canvas.erase(w);
                continue;
            }
            Shape shape = SymbolShapeCatalog.shapeFor(color);
            int index = wheels.indexOf(w);
            AffineTransform t = AffineTransform.getTranslateInstance(60 + index * 70, 100);
            canvas.draw(w, color, t.createTransformedShape(shape));
        }
    }
    
    /**
     * Cierra la máquina: si está visible, la oculta primero. La operación
     * siempre queda registrada como exitosa.
     */
    public void exit() {
        if (visible) {
            makeInvisible();
        }
        lastOk = true;
    }
    
    /**
     * Indica si la última operación realizada sobre la máquina fue exitosa.
     *
     * @return true si la última operación tuvo éxito; false si falló.
     */
    public boolean ok() {
        return lastOk;
    }
    
    /**
    * Marca la última operación como fallida y, si la máquina es visible,
    * muestra el error al usuario mediante un JOptionPane.
    */
    private void fail(String message) {
        lastOk = false;
        if (visible) {
            javax.swing.JOptionPane.showMessageDialog(null, message);
        }
    }
    
    /**
     * Fija (lock) la rueda en la posición indicada (basada en 1 y ajustada
     * al rango válido), impidiendo que sea girada hasta liberarse con
     * {@link #unlock(int)}. Falla si no hay ruedas.
     */
    public void lock(int wheel) {
        if (wheels.isEmpty()) {
            fail("No hay ruedas para fijar.");
            return;
        }
        int clamped = clamp(wheel, 1, wheels.size());
        wheels.get(clamped - 1).setLocked(true);
        lastOk = true;
        refresh();
    }
    
    /**
     * Libera (unlock) la rueda en la posición indicada (basada en 1 y
     * ajustada al rango válido), permitiendo que vuelva a girarse. Falla si
     * no hay ruedas.
     */
    public void unlock(int wheel) {
        if (wheels.isEmpty()) {
            fail("No hay ruedas para soltar.");
            return;
        }
        int clamped = clamp(wheel, 1, wheels.size());
        wheels.get(clamped - 1).setLocked(false);
        lastOk = true;
        refresh();
    }
    
    /**
     * Intercambia los símbolos visibles entre las dos ruedas indicadas
     * (posiciones basadas en 1 y ajustadas al rango válido). Falla si no hay
     * ruedas, o si alguna de las dos ruedas está fija (locked); en ese caso
     * ninguna de las dos cambia.
     */
    public void swap(int wheel1, int wheel2) {
        if (wheels.isEmpty()) {
            fail("No hay ruedas para intercambiar.");
            return;
        }
        int c1 = clamp(wheel1, 1, wheels.size());
        int c2 = clamp(wheel2, 1, wheels.size());
        if (wheels.get(c1 - 1).isLocked() || wheels.get(c2 - 1).isLocked()) {
            fail("No se puede intercambiar: una de las ruedas está fija.");
            return;
        }
        java.util.Collections.swap(wheels, c1 - 1, c2 - 1);
        lastOk = true;
        refresh();
    }
    
    /**
     * Devuelve la rueda en la posición indicada (basada en 1 y ajustada al
     * rango válido) si existe y no está fija; en caso contrario invoca
     * fail() y retorna null.
     */
    private Wheel spinnableWheelAt(int pos) {
        if (wheels.isEmpty()) {
            fail("No hay ruedas para girar.");
            return null;
        }
        int clamped = clamp(pos, 1, wheels.size());
        Wheel target = wheels.get(clamped - 1);
        if (target.isLocked()) {
            fail("La rueda " + clamped + " está fija; suéltela antes de girar.");
            return null;
        }
        return target;
    }
    
    /**
     * Gira la rueda en la posición indicada (basada en 1 y ajustada al
     * rango válido) exactamente el número de pasos indicado, de forma
     * determinística (sin aleatoriedad). Un número de pasos negativo gira
     * en sentido contrario. Falla si no hay símbolos o si la rueda está
     * fija.
     */
    public void spin(int wheel, int steps) {
        if (symbols.isEmpty()) {
            fail("No se puede girar: no hay símbolos cargados.");
            return;
        }
        Wheel target = spinnableWheelAt(wheel);
        if (target == null) return;
        animatedRotate(target, steps);
        lastOk = true;
        refresh();
    }
    
    /**
     * Deja la máquina directamente en una configuración dada, sin girar.
     * El arreglo debe contener exactamente un color por cada rueda, en el
     * mismo orden de izquierda a derecha que retorna {@link #configuration()}.
     * <p>
     * Las ruedas fijas (locked) se saltan y conservan el símbolo que ya
     * estaban mostrando, sin importar lo que pida la posición correspondiente
     * del arreglo.
     * <p>
     * Esta operación es todo o nada: cada color del arreglo se valida antes
     * de tocar cualquier rueda, así que si algún color no existe en la lista
     * de símbolos de la máquina, o el tamaño del arreglo no coincide con el
     * número de ruedas, la máquina queda completamente sin cambios y
     * {@link #ok()} retorna false.
     *
     * @param setSymbols el color deseado para cada rueda, de izquierda a derecha
     */
    public void spin(String[] setSymbols) {
        if (wheels.isEmpty()) {
            fail("No hay ruedas para configurar.");
            return;
        }
        if (setSymbols == null || setSymbols.length != wheels.size()) {
            fail("El arreglo debe tener exactamente " + wheels.size() + " colores.");
            return;
        }

        int[] indices = new int[setSymbols.length];
        for (int i = 0; i < setSymbols.length; i++) {
            int idx = indexOfColor(setSymbols[i]);
            if (idx == -1) {
                fail("El color '" + setSymbols[i] + "' no existe en la máquina.");
                return;
            }
            indices[i] = idx;
        }

        java.util.List<Wheel> unlocked = new java.util.ArrayList<>();
        for (Wheel w : wheels) {
            if (!w.isLocked()) unlocked.add(w);
        }
        if (unlocked.isEmpty()) {
            fail("Todas las ruedas están fijas; no se aplicó ninguna configuración.");
            return;
        }

        java.util.List<Wheel> spinning = new java.util.ArrayList<>();
        java.util.List<Integer> steps = new java.util.ArrayList<>();
        for (int i = 0; i < wheels.size(); i++) {
            Wheel w = wheels.get(i);
            if (!w.isLocked()) {
                int stepsNeeded = Math.floorMod(indices[i] - w.getVisibleIndex(), symbols.size());
                if (stepsNeeded > 0) {
                    spinning.add(w);
                    steps.add(stepsNeeded);
                }
            }
        }
        if (!spinning.isEmpty()) {
            animatedMultiRotate(spinning, steps);
        }
        lastOk = true;
        refresh();
    }
    
    /**
     * Gira la rueda indicada un paso a la vez hasta completar el número de
     * pasos solicitado (un valor negativo gira en sentido contrario). Es un
     * caso particular de {@link #animatedMultiRotate} para una sola rueda.
     */
    private void animatedRotate(Wheel target, int steps) {
        animatedMultiRotate(java.util.List.of(target), java.util.List.of(steps));
    }
    
    /**
     * Gira simultáneamente varias ruedas, cada una su propia cantidad de
     * pasos con signo (que indica la dirección), avanzando de a un paso por
     * rueda y refrescando el dibujo después de cada paso hasta que todas
     * completen su recorrido. Si la máquina es visible, hace una breve
     * pausa entre pasos para que el movimiento pueda apreciarse; si es
     * invisible, cada paso ocurre sin demora.
     */
    private void animatedMultiRotate(java.util.List<Wheel> targets, java.util.List<Integer> stepsList) {
        int maxMagnitude = 0;
        int[] directions = new int[targets.size()];
        int[] magnitudes = new int[targets.size()];
        for (int i = 0; i < targets.size(); i++) {
            directions[i] = Integer.signum(stepsList.get(i));
            magnitudes[i] = Math.abs(stepsList.get(i));
            maxMagnitude = Math.max(maxMagnitude, magnitudes[i]);
        }
        for (int step = 0; step < maxMagnitude; step++) {
            for (int i = 0; i < targets.size(); i++) {
                if (magnitudes[i] > step) {
                    targets.get(i).rotate(directions[i], symbols.size());
                }
            }
            refresh();
            if (visible) {
                canvas.wait(SPIN_STEP_DELAY_MS);
            }
        }
    }
    
}