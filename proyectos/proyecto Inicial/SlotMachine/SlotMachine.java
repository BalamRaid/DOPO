/**

Simula una máquina tragamonedas con un número configurable de ruedas y
símbolos, inspirada en el Problema I ("Slot Machine") de las Finales
Mundiales de ICPC 2025.
Una SlotMachine contiene una única secuencia compartida de símbolos
(identificados mediante nombres de colores CSS estándar) utilizada por
todas las ruedas. Cada rueda no posee sus propios símbolos, sino que
únicamente recuerda qué posición de esa secuencia compartida está
mostrando actualmente. Las ruedas y los símbolos pueden añadirse o
eliminarse dinámicamente, las ruedas pueden girarse aleatoriamente o
configurarse directamente para mostrar un símbolo específico, y la
máquina puede indicar si todas las ruedas muestran actualmente el mismo
símbolo (premio mayor).
La máquina puede funcionar en modo visible, dibujándose a sí misma y a
sus símbolos sobre un {@link Canvas}, o en modo invisible, en el cual
toda la lógica continúa funcionando, pero no se realiza ningún dibujo
ni se muestran cuadros de diálogo de error. El resultado de la última
operación realizada sobre la máquina puede consultarse en cualquier
momento mediante {@link #ok()}, sin depender de excepciones.


@version 1.0 (Ciclo 1)
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
    * Gira la rueda en la posición indicada (basada en 1 y ajustada al rango válido)
    * hasta un símbolo aleatorio de la secuencia compartida de símbolos.
    */
    public void spin(int wheel) {
        if (wheels.isEmpty() || symbols.isEmpty()) {
            fail("No se puede girar: faltan ruedas o símbolos.");
            return;
        }
        int clamped = clamp(wheel, 1, wheels.size());
        int steps = random.nextInt(symbols.size()) + 1;
        wheels.get(clamped - 1).rotate(steps, symbols.size());
        lastOk = true;
        refresh();
    }

    /**
    * Gira cada rueda de forma independiente hasta un símbolo aleatorio.
    */
    public void spin() {
        if (wheels.isEmpty() || symbols.isEmpty()) {
            fail("No se puede girar: faltan ruedas o símbolos.");
            return;
        }
        for (Wheel w : wheels) {
            w.rotate(random.nextInt(symbols.size()) + 1, symbols.size());
        }
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

    public void makeVisible() {
        canvas = Canvas.getCanvas();
        visible = true;
        lastOk = true;
        refresh();
    }

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
    
    public void exit() {
        if (visible) {
            makeInvisible();
        }
        lastOk = true;
    }
    
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
    
}