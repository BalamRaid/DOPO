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
 * <p>
 * Ciclo 4 M1: existen tres tipos de ruedas ({@code normal}, {@code lefty} y
 * {@code rebel}). {@code lefty} copia a la izquierda al girar y {@code rebel}
 * veta {@code lock}, {@code swap} y {@code delWheel}. Los tipos se eligen con
 * {@link #addWheel(int, String)}; {@link #addWheel(int)} y
 * {@link #SlotMachine(int)} crean ruedas {@code normal} por compatibilidad.
 * <p>
 * Ciclo 4 M2: existen cuatro tipos de símbolos ({@code normal},
 * {@code ephemeral}, {@code shy} y {@code giant} nuevo Req19).
 * {@code ephemeral} encoge por giro hasta punto, {@code giant} crece hasta
 * 2.0, {@code shy} alterna visible/fantasma al ser seleccionado. Se eligen con
 * {@link #addSymbol(int, String, String)}; {@link #addSymbol(int, String)} y
 * {@link #SlotMachine(int)} crean {@code normal}. La lógica de jackpot no cambia.
 *
 * @version 4.0 (Ciclo 4 M2)
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
     * Crea una máquina tragamonedas vacía, sin ruedas ni símbolos, invisible por defecto.
     * La última operación queda registrada como exitosa.
     */
    public SlotMachine() {
        wheels = new ArrayList<>();
        symbols = new ArrayList<>();
        random = new java.util.Random();
        visible = false;
        lastOk = true;
    }

    /**
     * Crea una máquina tragamonedas de n ruedas y n símbolos, con colores
     * CSS distintos elegidos al azar. Cada rueda se inicializa en una
     * posición aleatoria de la secuencia de símbolos, garantizando que no
     * todas empiecen mostrando el mismo símbolo (salvo que n sea 1, caso en
     * el que es inevitable). La máquina nace invisible.
     *
     * @param n cantidad de ruedas y de símbolos que tendrá la máquina
     */
    public SlotMachine(int n) {
        wheels = new ArrayList<>();
        symbols = new ArrayList<>();
        random = new java.util.Random();
        visible = false;
        lastOk = true;

        for (String color : CssColors.randomDistinctNames(n, random)) {
            symbols.add(new Symbol(color));
        }
        for (int i = 0; i < n; i++) {
            Wheel w = new Wheel();
            w.setVisibleIndex(random.nextInt(n));
            wheels.add(w);
        }
        ensureNotAllEqual();
    }

    /**
     * Si, por azar, todas las ruedas quedaron mostrando el mismo símbolo
     * tras la inicialización aleatoria, mueve una rueda al azar a otra
     * posición hasta que deje de ser el caso. No hace nada si hay menos de
     * dos ruedas (con una sola rueda siempre habría "premio mayor").
     */
    private void ensureNotAllEqual() {
        if (wheels.size() < 2) return;
        while (isJackpot()) {
            int idx = random.nextInt(wheels.size());
            wheels.get(idx).setVisibleIndex(random.nextInt(symbols.size()));
        }
    }
    
    /**
    * Añade una nueva rueda normal en la posición indicada (basada en 1).
    * Si la posición está fuera de rango, se ajusta a la posición válida más cercana.
    *
    * @param pos posición deseada (base 1); se ajusta al rango válido.
    */
    public void addWheel(int pos) {
        addWheel(pos, "normal");
    }

    /**
    * Añade una nueva rueda del tipo indicado en la posición indicada (basada en 1).
    * Si la posición está fuera de rango, se ajusta a la posición válida más cercana.
    * Los tipos válidos son {@code normal}, {@code lefty} y {@code rebel}
    * (insensible a mayúsculas y espacios).
    * La operación falla (ok() == false) si el tipo es desconocido y no añade nada.
    *
    * @param pos posición deseada (base 1); se ajusta al rango válido.
    * @param type tipo de rueda a crear ({@code normal}, {@code lefty} o {@code rebel}).
    */
    public void addWheel(int pos, String type) {
        Wheel created = createWheelByType(type);
        if (created == null) {
            fail("Tipo de rueda desconocido: '" + type + "'. Use normal, lefty o rebel.");
            return;
        }
        int clamped = clamp(pos, 1, wheels.size() + 1);
        wheels.add(clamped - 1, created);
        lastOk = true;
        refresh();
    }

    /**
    * Crea una rueda del tipo indicado sin añadirla a la máquina.
    *
    * @param type tipo pedido; se normaliza a minúsculas y sin espacios.
    * @return la rueda creada; null si el tipo es nulo o desconocido.
    */
    private Wheel createWheelByType(String type) {
        if (type == null) return null;
        String t = type.trim().toLowerCase();
        if (t.equals("normal")) return new Wheel();
        if (t.equals("lefty")) return new LeftyWheel();
        if (t.equals("rebel")) return new RebelWheel();
        return null;
    }

    /**
    * Consulta el tipo de la rueda en la posición indicada (basada en 1 y
    * ajustada al rango válido).
    * La operación falla (ok() == false) si no hay ruedas y retorna null.
    *
    * @param wheel posición de la rueda (base 1); se ajusta al rango válido.
    * @return tipo de la rueda ({@code normal}, {@code lefty} o {@code rebel}); null si no hay ruedas.
    */
    public String wheelType(int wheel) {
        if (wheels.isEmpty()) {
            fail("No hay ruedas para consultar el tipo.");
            return null;
        }
        int clamped = clamp(wheel, 1, wheels.size());
        return wheels.get(clamped - 1).getType();
    }

    /**
    * Construye la clave del marcador visual asociado a una rueda.
    * Cada rueda posee a lo sumo un marcador que distingue su tipo.
    *
    * @param w rueda de la que se desea la clave del marcador.
    * @return clave única y estable para el marcador de esa rueda.
    */
    private String markerKey(Wheel w) {
        return "type-marker-" + System.identityHashCode(w);
    }

    /**
    * Construye la clave de la insignia de tipo de símbolo visible en una rueda.
    * La insignia va ligada a la rueda (posición), no al símbolo compartido.
    *
    * @param w rueda de la que se desea la clave de insignia.
    * @return clave única y estable para esa insignia.
    */
    private String symBadgeKey(Wheel w) {
        return "symbadge-" + System.identityHashCode(w);
    }

    /**
    * Elimina la rueda de la posición indicada (basada en 1).
    * La operación falla (ok() == false) si no hay ruedas para eliminar
    * o si la rueda es de tipo {@code rebel} (no se deja eliminar).
    *
    * @param pos posición de la rueda (base 1); se ajusta al rango válido.
    */
    public void delWheel(int pos) {
        if (wheels.isEmpty()) {
            fail("No hay ruedas para eliminar.");
            return;
        }
        int clamped = clamp(pos, 1, wheels.size());
        Wheel target = wheels.get(clamped - 1);
        if (!target.canBeDeleted()) {
            fail("La rueda " + clamped + " es rebel y no se deja eliminar.");
            return;
        }
        Wheel removed = wheels.remove(clamped - 1);
        if (visible && canvas != null) {
            canvas.erase(removed);
            canvas.erase(markerKey(removed));
            canvas.erase(symBadgeKey(removed));
        }
        lastOk = true;
        refresh();
    }

    /**
    * Añade un nuevo símbolo normal del color indicado en la posición especificada (basada en 1).
    * Si la posición está fuera de rango, se ajusta a la posición válida más cercana.
    * La operación falla si el color no es un color CSS válido o si ya existe
    * un símbolo con ese color.
    *
    * @param pos posición deseada (base 1); se ajusta al rango válido.
    * @param color nombre de color CSS del nuevo símbolo.
    */
    public void addSymbol(int pos, String color) {
        addSymbol(pos, color, "normal");
    }

    /**
    * Añade un nuevo símbolo del tipo indicado en la posición especificada (basada en 1).
    * Si la posición está fuera de rango, se ajusta a la posición válida más cercana.
    * Tipos válidos: {@code normal}, {@code ephemeral}, {@code shy} y {@code giant}
    * (insensible a mayúsculas y espacios). Falla si el color no es CSS válido,
    * si ya existe ese color o si el tipo es desconocido.
    *
    * @param pos posición deseada (base 1); se ajusta al rango válido.
    * @param color nombre de color CSS del nuevo símbolo.
    * @param type tipo de símbolo a crear.
    */
    public void addSymbol(int pos, String color, String type) {
        if (!CssColors.isValid(color)) {
            fail("'" + color + "' no es un color CSS válido.");
            return;
        }
        if (colorExists(color)) {
            fail("Ya existe un símbolo con el color '" + color + "'.");
            return;
        }
        Symbol created = createSymbolByType(color, type);
        if (created == null) {
            fail("Tipo de símbolo desconocido: '" + type + "'. Use normal, ephemeral, shy o giant.");
            return;
        }
        int previousSize = symbols.size();
        int clamped = clamp(pos, 1, symbols.size() + 1);
        symbols.add(clamped - 1, created);
        if (previousSize > 0) {
            for (Wheel w : wheels) {
                w.adjustForInsertion(clamped - 1);
            }
        }
        lastOk = true;
        refresh();
    }

    /**
    * Crea un símbolo del tipo indicado sin añadirlo a la máquina.
    *
    * @param color nombre de color CSS del símbolo.
    * @param type tipo pedido; se normaliza a minúsculas y sin espacios.
    * @return el símbolo creado; null si el tipo es nulo o desconocido.
    */
    private Symbol createSymbolByType(String color, String type) {
        if (type == null) return null;
        String t = type.trim().toLowerCase();
        if (t.equals("normal")) return new Symbol(color);
        if (t.equals("ephemeral")) return new EphemeralSymbol(color);
        if (t.equals("shy")) return new ShySymbol(color);
        if (t.equals("giant")) return new GiantSymbol(color);
        return null;
    }

    /**
    * Consulta el tipo del símbolo con el color indicado.
    * Falla (ok() == false) si no existe y retorna null.
    *
    * @param color nombre de color CSS del símbolo.
    * @return tipo ({@code normal}, {@code ephemeral}, {@code shy} o {@code giant}); null si no existe.
    */
    public String symbolType(String color) {
        int idx = indexOfColor(color);
        if (idx == -1) {
            fail("No existe un símbolo con el color '" + color + "'.");
            return null;
        }
        return symbols.get(idx).getType();
    }

    /**
    * Consulta la escala visual actual del símbolo con el color indicado.
    * Falla (ok() == false) si no existe y retorna 1.0.
    *
    * @param color nombre de color CSS del símbolo.
    * @return escala actual (ephemeral 0.15-1.0, giant 1.0-2.0, resto 1.0).
    */
    public double symbolScale(String color) {
        int idx = indexOfColor(color);
        if (idx == -1) {
            fail("No existe un símbolo con el color '" + color + "'.");
            return 1.0;
        }
        return symbols.get(idx).getScale();
    }

    /**
    * Consulta si el símbolo con el color indicado está visible (no fantasma).
    * Falla (ok() == false) si no existe y retorna true.
    *
    * @param color nombre de color CSS del símbolo.
    * @return true si visible; false si shy en estado fantasma.
    */
    public boolean isSymbolVisible(String color) {
        int idx = indexOfColor(color);
        if (idx == -1) {
            fail("No existe un símbolo con el color '" + color + "'.");
            return true;
        }
        return symbols.get(idx).isVisible();
    }

    /**
    * Notifica un giro a todos los símbolos escalables (ephemeral/giant).
    * Se invoca una vez por cada rueda movida en un giro.
    *
    * @param wheelsMoved cantidad de ruedas movidas en la operación.
    */
    private void notifySpun(int wheelsMoved) {
        for (int k = 0; k < wheelsMoved; k++) {
            for (Symbol s : symbols) {
                s.onSpun();
            }
        }
    }

    /**
    * Notifica selección a los símbolos recién visibles en las ruedas indicadas.
    * Los shy alternan visible/fantasma. No cambia escalas.
    *
    * @param affected ruedas cuyo símbolo visible final debe notificarse.
    */
    private void notifySelected(java.util.List<Wheel> affected) {
        if (symbols.isEmpty()) return;
        for (Wheel w : affected) {
            int vi = w.getVisibleIndex();
            if (vi >= 0 && vi < symbols.size()) {
                symbols.get(vi).onSelected();
            }
        }
    }

    /**
    * Elimina el símbolo que tiene el color indicado.
    * La operación falla si no existe un símbolo con ese color.
    *
    * @param color nombre de color CSS del símbolo a eliminar.
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
    * en la secuencia compartida de símbolos. No aplica la copia de lefty:
    * asigna el símbolo pedido para permitir un montaje determinista.
    * Asignación directa: no cambia escalas, pero sí notifica selección
    * (un shy alterna visible/fantasma).
    *
    * @param wheel posición de la rueda (base 1); se ajusta al rango válido.
    * @param symbol nombre de color CSS del símbolo a mostrar.
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
        Wheel target = wheels.get(clamped - 1);
        target.setVisibleIndex(index);
        notifySelected(java.util.List.of(target));
        lastOk = true;
        refresh();
    }

    /**
     * Gira la rueda en la posición indicada (basada en 1 y ajustada al rango
     * válido) hasta un símbolo aleatorio de la secuencia compartida de
     * símbolos. Si la rueda es {@code lefty} con vecina a la izquierda,
     * copia su estado en lugar de girar aleatoriamente. Falla si no hay
     * símbolos cargados o si la rueda está fija (locked).
     *
     * @param wheel posición de la rueda (base 1); se ajusta al rango válido.
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
    *
    * @return arreglo con los colores de los símbolos en orden.
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
    *
    * @return cantidad de colores distintos visibles (0 si no hay ruedas o símbolos).
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
    *
    * @return true si hay premio mayor; false en caso contrario.
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
    *
    * @return arreglo con el color visible de cada rueda en orden.
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
    *
    * @param pos valor a ajustar (base 1).
    * @param min mínimo permitido.
    * @param max máximo permitido.
    * @return pos ajustada al intervalo [min, max].
    */
    private int clamp(int pos, int min, int max) {
        if (pos < min) return min;
        if (pos > max) return max;
        return pos;
    }

    /**
     * Hace visible la máquina, creando (si aún no existe) el {@link Canvas}
     * compartido y dibujando en él el estado actual de la máquina.
     * La operación siempre queda registrada como exitosa.
     */
    public void makeVisible() {
        canvas = Canvas.getCanvas();
        visible = true;
        lastOk = true;
        refresh();
    }

    /**
     * Oculta la máquina, eliminando del lienzo las ruedas, sus marcadores de tipo
     * y sus insignias de símbolo antes de ocultar la ventana del {@link Canvas}.
     * La operación siempre queda registrada como exitosa.
     */
    public void makeInvisible() {
        if (canvas != null) {
            for (Wheel w : wheels) {
                canvas.erase(w);
                canvas.erase(markerKey(w));
                canvas.erase(symBadgeKey(w));
            }
            canvas.erase("jackpotGlow");
            canvas.setVisible(false);
        }
        visible = false;
        lastOk = true;
    }

    /**
    * Actualiza la representación visual de la máquina en el lienzo.
    * Ajusta el tamaño del lienzo según el número de ruedas y muestra el efecto
    * visual de premio mayor cuando todas las ruedas muestran el mismo símbolo.
    * Dibuja cada símbolo con su escala (ephemeral pequeño, giant grande) y en
    * gris fantasma si su shy está invisible. Además dibuja el marcador de tipo
    * de rueda (arriba) y la insignia de tipo de símbolo (abajo).
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
            String mKey = markerKey(w);
            String bKey = symBadgeKey(w);
            if (color == null || symbols.isEmpty()) {
                canvas.erase(w);
                canvas.erase(mKey);
                canvas.erase(bKey);
                continue;
            }
            int vi = w.getVisibleIndex();
            Symbol sym = (vi >= 0 && vi < symbols.size()) ? symbols.get(vi) : null;
            double scale = (sym == null) ? 1.0 : sym.getScale();
            boolean symVisible = (sym == null) || sym.isVisible();
            Shape shape = SymbolShapeCatalog.shapeFor(color);
            int index = wheels.indexOf(w);
            int centerX = 60 + index * 70;
            int centerY = 100;
            AffineTransform t = AffineTransform.getTranslateInstance(centerX, centerY);
            t.scale(scale, scale);
            Shape marker = w.getMarkerShape();
            String markerColor = w.getMarkerColor();
            if (marker != null && markerColor != null) {
                AffineTransform mt = AffineTransform.getTranslateInstance(
                    centerX + w.getMarkerDx(), centerY + w.getMarkerDy());
                canvas.draw(mKey, markerColor, mt.createTransformedShape(marker));
            } else {
                canvas.erase(mKey);
            }
            if (symVisible) {
                canvas.draw(w, color, t.createTransformedShape(shape));
            } else {
                canvas.draw(w, "lightgray", t.createTransformedShape(shape));
            }
            if (sym != null && sym.getBadgeShape() != null && sym.getBadgeColor() != null) {
                AffineTransform bt = AffineTransform.getTranslateInstance(
                    centerX + sym.getBadgeDx(), centerY + sym.getBadgeDy());
                canvas.draw(bKey, sym.getBadgeColor(), bt.createTransformedShape(sym.getBadgeShape()));
            } else {
                canvas.erase(bKey);
            }
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
    *
    * @param message mensaje de error a mostrar si la máquina está visible.
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
     * {@link #unlock(int)}. Falla si no hay ruedas o si la rueda es
     * {@code rebel} (no se deja fijar).
     *
     * @param wheel posición de la rueda (base 1); se ajusta al rango válido.
     */
    public void lock(int wheel) {
        if (wheels.isEmpty()) {
            fail("No hay ruedas para fijar.");
            return;
        }
        int clamped = clamp(wheel, 1, wheels.size());
        Wheel target = wheels.get(clamped - 1);
        if (!target.canBeLocked()) {
            fail("La rueda " + clamped + " es rebel y no se deja fijar.");
            return;
        }
        target.setLocked(true);
        lastOk = true;
        refresh();
    }
    
    /**
     * Libera (unlock) la rueda en la posición indicada (basada en 1 y
     * ajustada al rango válido), permitiendo que vuelva a girarse. Falla si
     * no hay ruedas. Sobre una rueda {@code rebel} es un no-op exitoso,
     * pues nunca está fija.
     *
     * @param wheel posición de la rueda (base 1); se ajusta al rango válido.
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
     * Intercambia de posición las dos ruedas indicadas (posiciones basadas
     * en 1 y ajustadas al rango válido). Falla si no hay ruedas, si alguna
     * de las dos ruedas está fija (locked) o si alguna es {@code rebel}
     * (no se deja intercambiar); en esos casos ninguna cambia.
     *
     * @param wheel1 posición de la primera rueda (base 1).
     * @param wheel2 posición de la segunda rueda (base 1).
     */
    public void swap(int wheel1, int wheel2) {
        if (wheels.isEmpty()) {
            fail("No hay ruedas para intercambiar.");
            return;
        }
        int c1 = clamp(wheel1, 1, wheels.size());
        int c2 = clamp(wheel2, 1, wheels.size());
        Wheel w1 = wheels.get(c1 - 1);
        Wheel w2 = wheels.get(c2 - 1);
        if (!w1.canBeSwapped() || !w2.canBeSwapped()) {
            fail("No se puede intercambiar: una de las ruedas es rebel.");
            return;
        }
        if (w1.isLocked() || w2.isLocked()) {
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
     *
     * @param pos posición pedida (base 1); se ajusta al rango válido.
     * @return la rueda giratoria; null si no hay ruedas o está fija.
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
     * en sentido contrario. Si la rueda es {@code lefty} con vecina a la
     * izquierda, copia su estado en lugar de rotar. Falla si no hay
     * símbolos o si la rueda está fija.
     *
     * @param wheel posición de la rueda (base 1); se ajusta al rango válido.
     * @param steps cantidad de pasos con signo (negativo gira al contrario).
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
     * Asignación directa: no aplica la copia de {@code lefty} ni cambia escalas;
     * sí notifica selección (un shy alterna visible/fantasma).
     * <p>
     * Esta operación es todo o nada: cada color del arreglo se valida antes
     * de tocar cualquier rueda, así que si algún color no existe en la lista
     * de símbolos de la máquina, o el tamaño del arreglo no coincide con el
     * número de ruedas, la máquina queda completamente sin cambios y
     * {@link #ok()} retorna false.
     *
     * @param setSymbols el color deseado para cada rueda, de izquierda a derecha.
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

        java.util.List<Wheel> affected = new java.util.ArrayList<>();
        for (int i = 0; i < wheels.size(); i++) {
            Wheel w = wheels.get(i);
            if (!w.isLocked()) {
                w.setVisibleIndex(indices[i]);
                affected.add(w);
            }
        }
        notifySelected(affected);
        lastOk = true;
        refresh();
    }
    
    /**
     * Gira la rueda indicada un paso a la vez hasta completar el número de
     * pasos solicitado (un valor negativo gira en sentido contrario). Si es
     * {@code lefty} con vecina, copia su estado. Es un caso particular de
     * {@link #animatedMultiRotate} para una sola rueda.
     *
     * @param target rueda a girar (ya validada como giratoria).
     * @param steps cantidad de pasos con signo.
     */
    private void animatedRotate(Wheel target, int steps) {
        animatedMultiRotate(java.util.List.of(target), java.util.List.of(steps));
    }
    
    /**
     * Gira simultáneamente varias ruedas, cada una su propia cantidad de
     * pasos con signo (que indica la dirección), avanzando de a un paso por
     * rueda y refrescando el dibujo después de cada paso hasta que todas
     * completen su recorrido. Las ruedas {@code lefty} con vecina a la
     * izquierda copian su estado final (tras girar las normales, de izquierda
     * a derecha) en lugar de rotar; sin vecina giran normal. Copia cualquier
     * vecina (normal, lefty o rebel). Cada rueda movida dispara un giro en los
     * símbolos escalables (ephemeral encoge, giant crece) y cada aterrizaje
     * final notifica selección (shy alterna). Si la máquina es visible, hace
     * una breve pausa entre pasos; si es invisible, sin demora.
     *
     * @param targets ruedas a girar, en cualquier orden.
     * @param stepsList pasos con signo para cada rueda, en el mismo orden que targets.
     */
    private void animatedMultiRotate(java.util.List<Wheel> targets, java.util.List<Integer> stepsList) {
        java.util.List<Wheel> lefties = new java.util.ArrayList<>();
        java.util.List<Wheel> rotating = new java.util.ArrayList<>();
        java.util.List<Integer> rotatingSteps = new java.util.ArrayList<>();
        for (int i = 0; i < targets.size(); i++) {
            Wheel target = targets.get(i);
            int idx = wheels.indexOf(target);
            if (target.copiesLeft() && idx > 0) {
                lefties.add(target);
            } else {
                rotating.add(target);
                rotatingSteps.add(stepsList.get(i));
            }
        }
        int maxMagnitude = 0;
        int[] directions = new int[rotating.size()];
        int[] magnitudes = new int[rotating.size()];
        for (int i = 0; i < rotating.size(); i++) {
            directions[i] = Integer.signum(rotatingSteps.get(i));
            magnitudes[i] = Math.abs(rotatingSteps.get(i));
            maxMagnitude = Math.max(maxMagnitude, magnitudes[i]);
        }
        for (int step = 0; step < maxMagnitude; step++) {
            for (int i = 0; i < rotating.size(); i++) {
                if (magnitudes[i] > step) {
                    rotating.get(i).rotate(directions[i], symbols.size());
                }
            }
            refresh();
            if (visible) {
                canvas.wait(SPIN_STEP_DELAY_MS);
            }
        }
        lefties.sort((a, b) -> Integer.compare(wheels.indexOf(a), wheels.indexOf(b)));
        for (Wheel lefty : lefties) {
            int idx = wheels.indexOf(lefty);
            if (idx > 0) {
                lefty.setVisibleIndex(wheels.get(idx - 1).getVisibleIndex());
            }
        }
        int moved = lefties.size();
        for (int m : magnitudes) {
            if (m > 0) moved++;
        }
        if (moved > 0) {
            notifySpun(moved);
            notifySelected(targets);
        }
    }
    
}