package test;
import domain.*;


import static org.junit.Assert.*;
import org.junit.Test;


public class NeoFlixTest{

    @Test
    public void shouldStartWithSampleData() throws NeoFlixException{
        NeoFlix nf = new NeoFlix();
        assertEquals(6, nf.numberContents());
    }

    @Test
    public void shouldFindSampleContentByTitle() throws NeoFlixException{
        NeoFlix nf = new NeoFlix();
        assertTrue(nf.consult("The Neo Ninja") instanceof Series);
    }

    @Test
    public void shouldFindContentIgnoringCase() throws NeoFlixException{
        NeoFlix nf = new NeoFlix();
        assertNotNull(nf.consult("the neo ninja"));
    }

    @Test
    public void shouldReturnNullWhenContentDoesNotExist() throws NeoFlixException{
        NeoFlix nf = new NeoFlix();
        assertNull(nf.consult("Nope"));
    }

    @Test
    public void shouldAddAnEpisode() throws NeoFlixException{
        NeoFlix nf = new NeoFlix();
        nf.addEpisode("The Escape","", "100","80","20","150");
        assertEquals(7, nf.numberContents());
        assertTrue(nf.consult("The Escape") instanceof Episode);
    }

    @Test
    public void shouldAddASeries() throws NeoFlixException{
        NeoFlix nf = new NeoFlix();
        nf.addSeries("Ninja Origins","2019", "The Rival\nThe Tournament");
        assertEquals(7, nf.numberContents());
        assertTrue(nf.consult("Ninja Origins") instanceof Series);
    }

    @Test
    public void shouldListAllContentsWithoutIndicators() throws NeoFlixException{
        NeoFlix nf = new NeoFlix();
        assertEquals("6 elementos\n"
            + ">The Hidden Village\n"
            + ">The First Mission\n"
            + ">The Rival\n"
            + ">The Tournament\n"
            + ">The Final Battle\n"
            + ">The Neo Ninja: 2020\n"
            + "\tThe Hidden Village\n"
            + "\tThe First Mission\n"
            + "\tThe Rival\n"
            + "\tThe Tournament\n"
            + "\tThe Final Battle\n",
            nf.toString());
    }

    @Test
    public void shouldListAnAddedEpisode() throws NeoFlixException{
        NeoFlix nf = new NeoFlix();
        nf.addEpisode("The Escape","", "100","80","20","150");
        String listed = nf.toString();
        assertTrue(listed.contains("7 elementos"));
        assertTrue(listed.contains(">The Escape"));
    }

    @Test
    public void shouldListSeriesWithItsEpisodesIndented() throws NeoFlixException{
        NeoFlix nf = new NeoFlix();
        assertTrue(nf.toString().contains(">The Neo Ninja: 2020\n\tThe Hidden Village"));
    }

    @Test
    public void shouldNotAddEpisodeWithExistingTitle() throws NeoFlixException{
        NeoFlix nf = new NeoFlix();
        try {
            nf.addEpisode("The Hidden Village","", "100","80","20","150");
            fail("Did not throw exception");
        } catch (NeoFlixException e) {
            assertEquals(NeoFlixException.CONTENT_EXISTS, e.getMessage());
        }
    }

    @Test
    public void shouldNotAddEpisodeWithExistingTitleIgnoringCase() throws NeoFlixException{
        NeoFlix nf = new NeoFlix();
        try {
            nf.addEpisode("the hidden village","", "100","80","20","150");
            fail("Did not throw exception");
        } catch (NeoFlixException e) {
            assertEquals(NeoFlixException.CONTENT_EXISTS, e.getMessage());
        }
    }

    @Test
    public void shouldNotAddSeriesWithExistingTitle() throws NeoFlixException{
        NeoFlix nf = new NeoFlix();
        try {
            nf.addSeries("The Neo Ninja","2020","The Rival");
            fail("Did not throw exception");
        } catch (NeoFlixException e) {
            assertEquals(NeoFlixException.CONTENT_EXISTS, e.getMessage());
        }
    }

    @Test
    public void shouldNotAddEpisodeWithTheNameOfASeries() throws NeoFlixException{
        NeoFlix nf = new NeoFlix();
        try {
            nf.addEpisode("The Neo Ninja","", "100","80","20","150");
            fail("Did not throw exception");
        } catch (NeoFlixException e) {
            assertEquals(NeoFlixException.CONTENT_EXISTS, e.getMessage());
        }
    }

    @Test
    public void shouldNotChangeTheCatalogWhenAddFails() throws NeoFlixException{
        NeoFlix nf = new NeoFlix();
        try {
            nf.addEpisode("The Hidden Village","", "100","80","20","150");
            fail("Did not throw exception");
        } catch (NeoFlixException e) {
            assertEquals(NeoFlixException.CONTENT_EXISTS, e.getMessage());
        }
        try {
            nf.addEpisode("The Escape","", "abc","80","20","150");
            fail("Did not throw exception");
        } catch (NeoFlixException e) {
            assertEquals(NeoFlixException.NOT_NUMERIC, e.getMessage());
        }
        try {
            nf.addSeries("Ghost Series","2020","No Existe");
            fail("Did not throw exception");
        } catch (NeoFlixException e) {
            assertEquals(NeoFlixException.EPISODE_NOT_FOUND, e.getMessage());
        }
        assertEquals(6, nf.numberContents());
        assertNull(nf.consult("The Escape"));
        assertNull(nf.consult("Ghost Series"));
    }

    @Test
    public void shouldNotAddEpisodeWithNonNumericAttempts() throws NeoFlixException{
        NeoFlix nf = new NeoFlix();
        try {
            nf.addEpisode("The Escape","", "abc","80","20","150");
            fail("Did not throw exception");
        } catch (NeoFlixException e) {
            assertEquals(NeoFlixException.NOT_NUMERIC, e.getMessage());
        }
    }

    @Test
    public void shouldNotAddEpisodeWithEmptyNumericField() throws NeoFlixException{
        NeoFlix nf = new NeoFlix();
        try {
            nf.addEpisode("The Escape","", "","80","20","150");
            fail("Did not throw exception");
        } catch (NeoFlixException e) {
            assertEquals(NeoFlixException.NOT_NUMERIC, e.getMessage());
        }
    }

    @Test
    public void shouldNotAddEpisodeWithNonNumericVotes() throws NeoFlixException{
        NeoFlix nf = new NeoFlix();
        try {
            nf.addEpisode("The Escape","", "100","80","20x","150");
            fail("Did not throw exception");
        } catch (NeoFlixException e) {
            assertEquals(NeoFlixException.NOT_NUMERIC, e.getMessage());
        }
    }

    @Test
    public void shouldNotAddEpisodeWithNonNumericSumVotes() throws NeoFlixException{
        NeoFlix nf = new NeoFlix();
        try {
            nf.addEpisode("The Escape","", "100","80","20","1.5");
            fail("Did not throw exception");
        } catch (NeoFlixException e) {
            assertEquals(NeoFlixException.NOT_NUMERIC, e.getMessage());
        }
    }

    @Test
    public void shouldNotAddSeriesWithNonNumericYear() throws NeoFlixException{
        NeoFlix nf = new NeoFlix();
        try {
            nf.addSeries("Ninja Origins","20x0","The Rival");
            fail("Did not throw exception");
        } catch (NeoFlixException e) {
            assertEquals(NeoFlixException.NOT_NUMERIC, e.getMessage());
        }
    }

    @Test
    public void shouldKeepTheOriginalCauseWhenTranslating() throws NeoFlixException{
        NeoFlix nf = new NeoFlix();
        try {
            nf.addEpisode("The Escape","", "abc","80","20","150");
            fail("Did not throw exception");
        } catch (NeoFlixException e) {
            assertTrue(e.getCause() instanceof NumberFormatException);
        }
    }

    @Test
    public void shouldNotAddEpisodeWhenCompletedExceedsAttempts() throws NeoFlixException{
        NeoFlix nf = new NeoFlix();
        try {
            nf.addEpisode("The Escape","", "5","6","0","0");
            fail("Did not throw exception");
        } catch (NeoFlixException e) {
            assertEquals(NeoFlixException.INVALID_COMPLETED, e.getMessage());
        }
    }

    @Test
    public void shouldNotAddEpisodeWhenSumVotesExceedsLimit() throws NeoFlixException{
        NeoFlix nf = new NeoFlix();
        try {
            nf.addEpisode("The Escape","", "100","80","10","101");
            fail("Did not throw exception");
        } catch (NeoFlixException e) {
            assertEquals(NeoFlixException.INVALID_VOTES, e.getMessage());
        }
    }

    @Test
    public void shouldNotAddEpisodeWithSumVotesAndNoVotes() throws NeoFlixException{
        NeoFlix nf = new NeoFlix();
        try {
            nf.addEpisode("The Escape","", "10","5","0","5");
            fail("Did not throw exception");
        } catch (NeoFlixException e) {
            assertEquals(NeoFlixException.INVALID_VOTES, e.getMessage());
        }
    }

    @Test
    public void shouldNotAddEpisodeWithNegativeValues() throws NeoFlixException{
        NeoFlix nf = new NeoFlix();
        try {
            nf.addEpisode("The Escape","", "-1","0","0","0");
            fail("Did not throw exception");
        } catch (NeoFlixException e) {
            assertEquals(NeoFlixException.NEGATIVE_VALUE, e.getMessage());
        }
    }

    @Test
    public void shouldAddEpisodeAtTheLimits() throws NeoFlixException{
        NeoFlix nf = new NeoFlix();
        nf.addEpisode("En el limite","", "5","5","10","100");
        nf.addEpisode("En ceros","", "0","0","0","0");
        assertEquals(8, nf.numberContents());
        assertTrue(nf.consult("En el limite") instanceof Episode);
        assertTrue(nf.consult("En ceros") instanceof Episode);
    }

    @Test
    public void shouldNotAddSeriesWithUnknownEpisode() throws NeoFlixException{
        NeoFlix nf = new NeoFlix();
        try {
            nf.addSeries("Ghost Series","2020","No Existe");
            fail("Did not throw exception");
        } catch (NeoFlixException e) {
            assertEquals(NeoFlixException.EPISODE_NOT_FOUND, e.getMessage());
        }
        assertEquals(6, nf.numberContents());
    }

    @Test
    public void shouldNotAddASeriesIfAnyEpisodeIsMissing() throws NeoFlixException{
        NeoFlix nf = new NeoFlix();
        try {
            nf.addSeries("Ghost Series","2020","The Hidden Village\nThe Rival\nThe Tournament\nNo Existe");
            fail("Did not throw exception");
        } catch (NeoFlixException e) {
            assertEquals(NeoFlixException.EPISODE_NOT_FOUND, e.getMessage());
        }
        assertEquals(6, nf.numberContents());
    }

    @Test
    public void shouldIgnoreBlankLinesInTheEpisodeList() throws NeoFlixException{
        NeoFlix nf = new NeoFlix();
        nf.addSeries("Copias","2020","The Rival\n\nThe Tournament");
        assertEquals(7, nf.numberContents());
        assertTrue(nf.consult("Copias") instanceof Series);
        assertTrue(nf.toString().contains("Copias: 2020\n\tThe Rival\n\tThe Tournament"));
    }

    @Test
    public void shouldNotAddAnEpisodeWithAnEmptyTitle() throws NeoFlixException{
        NeoFlix nf = new NeoFlix();
        try {
            nf.addEpisode("   ","", "100","80","20","150");
            fail("Did not throw exception");
        } catch (NeoFlixException e) {
            assertEquals(NeoFlixException.EMPTY_TITLE, e.getMessage());
        }
    }

    @Test
    public void shouldSelectContentsByPrefix(){
        NeoFlix nf = new NeoFlix();
        assertEquals(2, nf.select("The F").size());
    }

    @Test
    public void shouldSelectAllContentsStartingWithT(){
        NeoFlix nf = new NeoFlix();
        assertEquals(6, nf.select("T").size());
    }

    @Test
    public void shouldSelectIgnoringCase(){
        NeoFlix nf = new NeoFlix();
        assertEquals(1, nf.select("the neo").size());
        assertTrue(nf.select("the neo").get(0) instanceof Series);
    }

    @Test
    public void shouldReturnEmptyWhenNoPrefixMatches(){
        NeoFlix nf = new NeoFlix();
        assertEquals(0, nf.select("X").size());
    }

    @Test
    public void shouldSearchWithIndicators() throws NeoFlixException{
        NeoFlix nf = new NeoFlix();
        assertTrue(nf.search("The Neo").contains("The Neo Ninja: 2020 ( 79 - 8 )"));
    }
}
