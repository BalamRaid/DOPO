import java.util.ArrayList;
import java.util.List;

public class SlotMachine {
    private List<Wheel> wheels;
    private boolean visible;
    private boolean lastOk;
    private List<Symbol> symbols;
    private java.util.Random random;

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
     * Adds a new empty wheel at the given position (1-based).
     * If pos is out of range, it is clamped to the nearest valid position.
     */
    public void addWheel(int pos) {
        int clamped = clamp(pos, 1, wheels.size() + 1);
        wheels.add(clamped - 1, new Wheel());
        lastOk = true;
    }

    /**
     * Removes the wheel at the given position (1-based).
     * Fails (ok() == false) if there are no wheels to remove.
     */
    public void delWheel(int pos) {
        if (wheels.isEmpty()) {
            lastOk = false;
            return;
        }
        int clamped = clamp(pos, 1, wheels.size());
        wheels.remove(clamped - 1);
        lastOk = true;
    }

    public void addSymbol(int pos, String color) {
        if (!CssColors.isValid(color) || colorExists(color)) {
            lastOk = false;
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
    }

    public void delSymbol(String color) {
        int index = indexOfColor(color);
        if (index == -1) {
            lastOk = false;
            return;
        }
        symbols.remove(index);
        for (Wheel w : wheels) {
            w.adjustForRemoval(index, symbols.size());
        }
        lastOk = true;
    }
    
    /**
     * Sets the wheel at the given position (1-based, clamped) to show the
     * given symbol color directly, without spinning.
     * Fails (ok() == false) if there are no wheels or the color does not
     * exist in the shared symbol sequence.
     */
    public void placeSymbol(int wheel, String symbol) {
        if (wheels.isEmpty()) {
            lastOk = false;
            return;
        }
        int index = indexOfColor(symbol);
        if (index == -1) {
            lastOk = false;
            return;
        }
        int clamped = clamp(wheel, 1, wheels.size());
        wheels.get(clamped - 1).setVisibleIndex(index);
        lastOk = true;
    }

    /**
     * Spins the wheel at the given position (1-based, clamped) to a random
     * symbol from the shared symbol sequence.
     */
    public void spin(int wheel) {
        if (wheels.isEmpty() || symbols.isEmpty()) {
            lastOk = false;
            return;
        }
        int clamped = clamp(wheel, 1, wheels.size());
        int steps = random.nextInt(symbols.size()) + 1;
        wheels.get(clamped - 1).rotate(steps, symbols.size());
        lastOk = true;
    }

    /**
     * Spins every wheel independently to a random symbol.
     */
    public void spin() {
        if (wheels.isEmpty() || symbols.isEmpty()) {
            lastOk = false;
            return;
        }
        for (Wheel w : wheels) {
            w.rotate(random.nextInt(symbols.size()) + 1, symbols.size());
        }
        lastOk = true;
    }
    
    private boolean colorExists(String color) {
        return indexOfColor(color) != -1;
    }

    private int indexOfColor(String color) {
        for (int i = 0; i < symbols.size(); i++) {
            if (symbols.get(i).getColor().equals(color)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Returns the colors of all symbols in the shared sequence, in order,
     * starting from position 1.
     */
    public String[] symbols() {
        String[] result = new String[symbols.size()];
        for (int i = 0; i < symbols.size(); i++) {
            result[i] = symbols.get(i).getColor();
        }
        return result;
    }
    
    /**
     * Returns the number of distinct colors currently visible across all
     * wheels. Wheels with no visible symbol (null) are not counted.
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
     * Returns true if the machine has at least one wheel and every wheel
     * is showing the same non-null symbol.
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
     * Returns the colors currently visible on each wheel, left to right.
     */
    public String[] configuration() {
        String[] result = new String[wheels.size()];
        for (int i = 0; i < wheels.size(); i++) {
            result[i] = visibleColorOf(wheels.get(i));
        }
        return result;
    }

    private String visibleColorOf(Wheel wheel) {
        if (symbols.isEmpty()) {
            return null;
        }
        return symbols.get(wheel.getVisibleIndex()).getColor();
    }
    
    /**
     * Clamps a 1-based position between min and max.
     */
    private int clamp(int pos, int min, int max) {
        if (pos < min) return min;
        if (pos > max) return max;
        return pos;
    }

    public void makeVisible() {
        visible = true;
        lastOk = true;
    }

    public void makeInvisible() {
        visible = false;
        lastOk = true;
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
}