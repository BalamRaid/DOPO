import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class PlaylistTest{

    
    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     */
    @Before
    public void setUp(){
        
    }
    
     @Test
    public void shouldCreateAEmptyPlaylist(){
        String [][] songs = {};
        Playlist pl=new Playlist(songs);
        assertEquals(0, pl.size());     
    }    
   
    @Test
    public void shouldCreateAPlaylist(){
        String [][] songs=
            {{"One", "U2", "Rock", "4", "*****"},
             {"Numb", "Linkin Park", "Rock", "3", null},
             {"Alive", "Pearl Jam", "Rock", "5", "****"},
             {"Creep", "Radiohead", "Rock", null, "*****"},
             {"Dreams", "Fleetwood Mac", null, "4", "****"}};
        Playlist pl=new Playlist(songs);
        assertEquals(5, pl.size());   
    }    
    
    @Test
    public void shouldNotCreateABadPlaylist(){
        String [][] songs=
            {{"One", "U2", "Rock", "4", "*******"},
             {"Numb", "Linkin Park", "Rock", "Rock", null},
             {"Alive", "Pearl Jam", "Rock", "5", "****"},
             {"Creep", null, "Rock", null, "*****"},
             {null, "Fleetwood Mac", null, "4", "****"}};
        Playlist pl=new Playlist(songs);
        assertEquals(1, pl.size());   
    }  
    
    @Test
    public void shouldRecognizeEqualPlaylists(){
       String [][] songs=
            {{"One", "U2", "Rock", "4", "*******"},
             {"Numb", "Linkin Park", "Rock", "Rock", null},
             {"Alive", "Pearl Jam", "Rock", "5", "****"},
             {"Creep", null, "Rock", null, "*****"},
             {null, "Fleetwood Mac", null, "4", "****"}}; 
       String [][] sameSongs=
            {{"ONE", "U2", "Rock", "4", "*******"},
             {"   Numb", "Linkin Park   ", "Rock", "Rock", null},
             {"Alive", "PEARL   JAM", "Rock", "5", "****"},
             {"Creep", null, "ROCK", null, "*****"},
             {null, "Fleetwood Mac", null, "4", "**   **"}};
       assertEquals(new Playlist(songs),new Playlist(sameSongs));
    }
    
    @Test
    public void shouldAddANewSong() {
        Playlist pl = new Playlist(new String[][]{{"One", "U2", "Rock", "4", "*****"}});
        Playlist result = pl.add(new String[]{"Numb", "Linkin Park", "Rock", "3", null});
        assertEquals(2, result.size());
        assertEquals(1, pl.size());
    }

    @Test
    public void shouldNotAddADuplicateSong() {
        Playlist pl = new Playlist(new String[][]{{"One", "U2", "Rock", "4", "*****"}});
        Playlist result = pl.add(new String[]{"one", "u2", "Rock", "4", "***"});
        assertEquals(1, result.size());
    }

    @Test
    public void shouldNotAddAnInvalidSong() {
        Playlist pl = new Playlist(new String[][]{});
        Playlist result = pl.add(new String[]{"One", "U2", "Rock", "4", "*******"});
        assertEquals(0, result.size());
    }

    @Test
    public void shouldDeleteAnExistingSong() {
        Playlist pl = new Playlist(new String[][]{
            {"One", "U2", "Rock", "4", "*****"},
            {"Numb", "Linkin Park", "Rock", "3", null}});
        Playlist result = pl.delete(new String[]{"One", "U2", null, null, null});
        assertEquals(1, result.size());
        assertEquals(2, pl.size());
    }

    @Test
    public void shouldNotChangeWhenDeletingANonExistentSong() {
        Playlist pl = new Playlist(new String[][]{{"One", "U2", "Rock", "4", "*****"}});
        Playlist result = pl.delete(new String[]{"Numb", "Linkin Park", null, null, null});
        assertEquals(1, result.size());
    }

    @Test
    public void shouldSelectSongsMatchingAGenre() {
        Playlist pl = new Playlist(new String[][]{
            {"One", "U2", "Rock", "4", "*****"},
            {"Numb", "Linkin Park", "Rock", "3", null},
            {"Dreams", "Fleetwood Mac", "Folk", "4", "****"}});
        Playlist result = pl.select(new String[]{null, null, "Rock", null, null});
        assertEquals(2, result.size());
    }

    @Test
    public void shouldSelectAllSongsWhenCriteriaAreAllWildcards() {
        Playlist pl = new Playlist(new String[][]{
            {"One", "U2", "Rock", "4", "*****"},
            {"Numb", "Linkin Park", "Rock", "3", null}});
        Playlist result = pl.select(new String[5]);
        assertEquals(2, result.size());
    }
    
    /**
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    @After
    public void tearDown(){
    }
}
