/**
 * Represents a single wheel of the slot machine. A wheel does not own its
 * symbols — the full symbol sequence lives in SlotMachine and is shared
 * by every wheel. A Wheel only remembers which index of that shared
 * sequence it is currently showing through its window.
 */
public class Wheel {
    private int visibleIndex; // 0-based index into SlotMachine's symbol list

    public Wheel() {
        visibleIndex = 0;
    }

    public int getVisibleIndex() {
        return visibleIndex;
    }

    /**
     * Moves the window by the given number of steps, wrapping around
     * the shared symbol list (size totalSymbols).
     */
    public void rotate(int steps, int totalSymbols) {
        if (totalSymbols == 0) return;
        visibleIndex = Math.floorMod(visibleIndex + steps, totalSymbols);
    }

    /**
     * Directly sets which index of the shared symbol list is visible
     * (used by placeSymbol).
     */
    public void setVisibleIndex(int index) {
        visibleIndex = index;
    }
    
    /**
     * Adjusts this wheel's visible index after a symbol was inserted
     * at insertedAt in the shared list, so this wheel keeps showing
     * the same symbol it was showing before the insertion.
     */
    public void adjustForInsertion(int insertedAt) {
        if (insertedAt <= visibleIndex) {
            visibleIndex++;
        }
    }

    /**
     * Adjusts this wheel's visible index after the symbol at removedAt
     * was deleted from the shared list (now of size newTotal). If this
     * wheel was showing the removed symbol, it lands on the next
     * available one (wrapping if necessary).
     */
    public void adjustForRemoval(int removedAt, int newTotal) {
        if (newTotal == 0) {
            visibleIndex = 0;
        } else if (visibleIndex == removedAt) {
            visibleIndex = Math.floorMod(visibleIndex, newTotal);
        } else if (visibleIndex > removedAt) {
            visibleIndex--;
        }
    }
}