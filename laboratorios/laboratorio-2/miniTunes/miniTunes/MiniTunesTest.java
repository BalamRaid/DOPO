import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class MiniTunesTest {

    private MiniTunes mt;

    @Before
    public void setUp() {
        mt = new MiniTunes();
    }

    @Test
    public void shouldDefineANewPlaylistName() {
        mt.define("Favorites");
        assertTrue(mt.ok());
        assertEquals("Favorites", mt.toString());
    }

    @Test
    public void shouldNotRedefineAnExistingPlaylistName() {
        mt.define("Favorites");
        mt.define("Favorites");
        assertFalse(mt.ok());
        assertEquals("Favorites", mt.toString());
    }

    @Test
    public void shouldAssignAPlaylistToADefinedName() {
        mt.define("Favorites");
        String[][] songs = {{"One", "U2", "Rock", "4", "*****"}};
        mt.assign("Favorites", songs);
        assertTrue(mt.ok());
        assertEquals(1, mt.size("Favorites"));
    }

    @Test
    public void shouldNotAssignToAnUndefinedName() {
        String[][] songs = {{"One", "U2", "Rock", "4", "*****"}};
        mt.assign("Favorites", songs);
        assertFalse(mt.ok());
        assertEquals(-1, mt.size("Favorites"));
    }

    @Test
    public void shouldReturnZeroSizeForANewlyDefinedPlaylist() {
        mt.define("Favorites");
        assertEquals(0, mt.size("Favorites"));
    }

    @Test
    public void shouldReturnMinusOneSizeForAnUndefinedName() {
        assertEquals(-1, mt.size("Favorites"));
    }

    @Test
    public void shouldListPlaylistNamesInAlphabeticalOrder() {
        mt.define("Rock");
        mt.define("Chill");
        assertEquals("Chill, Rock", mt.toString());
    }

    @Test
    public void shouldReturnEmptyStringWhenNoPlaylistsAreDefined() {
        assertEquals("", mt.toString());
    }

    @Test
    public void shouldReturnStringRepresentationOfAPlaylist() {
        mt.define("Favorites");
        String[][] songs = {{"One", "U2", "Rock", "4", "*****"}};
        mt.assign("Favorites", songs);
        assertEquals(new Playlist(songs).toString(), mt.toString("Favorites"));
    }

    @Test
    public void shouldReturnNullStringForAnUndefinedPlaylistName() {
        assertNull(mt.toString("Favorites"));
    }
    
    @Test
    public void shouldAddASongThroughUnaryOperation() {
        mt.define("A");
        mt.define("B");
        mt.assign("B", new String[][]{{"One", "U2", "Rock", "4", "*****"}});
        mt.assignUnary("A", "B", 'a', new String[]{"Numb", "Linkin Park", "Rock", "3", null});
        assertTrue(mt.ok());
        assertEquals(2, mt.size("A"));
        assertEquals(1, mt.size("B"));
    }

    @Test
    public void shouldDeleteASongThroughUnaryOperation() {
        mt.define("A");
        mt.define("B");
        mt.assign("B", new String[][]{
        {"One", "U2", "Rock", "4", "*****"},
        {"Numb", "Linkin Park", "Rock", "3", null}});
        mt.assignUnary("A", "B", 'd', new String[]{"One", "U2", null, null, null});
        assertTrue(mt.ok());
        assertEquals(1, mt.size("A"));
    }

    @Test
    public void shouldSelectSongsThroughUnaryOperation() {
        mt.define("A");
        mt.define("B");
        mt.assign("B", new String[][]{
            {"One", "U2", "Rock", "4", "*****"},
            {"Dreams", "Fleetwood Mac", "Folk", "4", "****"}});
        mt.assignUnary("A", "B", 's', new String[]{null, null, "Rock", null, null});
        assertTrue(mt.ok());
        assertEquals(1, mt.size("A"));
    }

    @Test
    public void shouldFailWhenSourcePlaylistIsUndefined() {
        mt.define("A");
        mt.assignUnary("A", "B", 'a', new String[]{"One", "U2", "Rock", "4", "*****"});
        assertFalse(mt.ok());
        assertEquals(0, mt.size("A"));
    }

    @Test
    public void shouldFailWhenOperatorCharIsInvalid() {
        mt.define("A");
        mt.define("B");
        mt.assignUnary("A", "B", 'x', new String[]{"One", "U2", "Rock", "4", "*****"});
        assertFalse(mt.ok());
        assertEquals(0, mt.size("A"));
    }
    
    @Test
    public void shouldUnionThroughBinaryOperation() {
        mt.define("A"); mt.define("B"); mt.define("C");
        mt.assign("B", new String[][]{{"One", "U2", "Rock", "4", "*****"}});
        mt.assign("C", new String[][]{{"Numb", "Linkin Park", "Rock", "3", null}});
        mt.assignBinary("A", "B", 'u', "C");
        assertTrue(mt.ok());
        assertEquals(2, mt.size("A"));
    }

    @Test
    public void shouldIntersectThroughBinaryOperation() {
        mt.define("A"); mt.define("B"); mt.define("C");
        mt.assign("B", new String[][]{
            {"One", "U2", "Rock", "4", "*****"},
            {"Numb", "Linkin Park", "Rock", "3", null}});
        mt.assign("C", new String[][]{{"Numb", "Linkin Park", "Rock", "3", null}});
        mt.assignBinary("A", "B", 'i', "C");
        assertTrue(mt.ok());
        assertEquals(1, mt.size("A"));
    }

    @Test
    public void shouldComputeDifferenceThroughBinaryOperation() {
        mt.define("A"); mt.define("B"); mt.define("C");
        mt.assign("B", new String[][]{
            {"One", "U2", "Rock", "4", "*****"},
            {"Numb", "Linkin Park", "Rock", "3", null}});
        mt.assign("C", new String[][]{{"Numb", "Linkin Park", "Rock", "3", null}});
        mt.assignBinary("A", "B", 'd', "C");
        assertTrue(mt.ok());
        assertEquals(1, mt.size("A"));
    }

    @Test
    public void shouldFailBinaryOperationWhenAnOperandIsUndefined() {
        mt.define("A"); mt.define("B");
        mt.assignBinary("A", "B", 'u', "C");
        assertFalse(mt.ok());
        assertEquals(0, mt.size("A"));
    }

    @Test
    public void shouldFailBinaryOperationWhenOperatorCharIsInvalid() {
        mt.define("A"); mt.define("B"); mt.define("C");
        mt.assignBinary("A", "B", 'x', "C");
        assertFalse(mt.ok());
        assertEquals(0, mt.size("A"));
    }
    
}