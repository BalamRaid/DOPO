package test;
import domain.*;


import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class SeriesTest{
   
 
    @Test
    public void shouldCalculateSeriessRating(){                              
        Series s = new Series("The Neo Ninja", 2020);
        s.addEpisode(new Episode("The Hidden Village",s,120,199,50,430));
        s.addEpisode(new Episode("The First Mission",s,140,126,60,510));
        s.addEpisode(new Episode("The Rival",s,120,199,50,430));
        s.addEpisode(new Episode("The Tournament",s,180, 162, 90, 810));
        s.addEpisode(new Episode("The Final Battle",s, 200, 190, 120, 1080));
        try {
           assertEquals(8,s.rating());
        } catch (NeoFlixException e){
            fail("Threw a exception "+e.getMessage());
        }    
    }    

    
    @Test
    public void shouldThrowExceptionIfTheSeriessHasNoEpisodes(){
        Series s = new Series("The Neo Ninja", 2020);
        try { 
           int value=s.rating();
           fail("Did not throw exception");
        } catch (NeoFlixException e) {
            assertEquals(NeoFlixException.CONTENT_EMPTY,e.getMessage());
        }    
    }    
    
    
    @Test
    public void shouldThrowExceptionWhenAnEpisodeRatingIsUnknown(){
        Series s = new Series("The Neo Ninja", 2020);
        s.addEpisode(new Episode("The Hidden Village",s,120,199,50,430));
        s.addEpisode(new Episode("The First Mission",s,140,126,60,510));
        s.addEpisode(new Episode("The Rival",s,120,199,0,0));
        s.addEpisode(new Episode("The Tournament",s,180, 162, 90, 810));
        s.addEpisode(new Episode("The Final Battle",s, 200, 190, 120, 1080));
        try { 
           int value=s.rating();
           fail("Did not throw exception");
        } catch (NeoFlixException e) {
            assertEquals(NeoFlixException.VALUE_UNKNOWN,e.getMessage());
        }    
    } 
    
   @Test
    public void shouldThrowExceptionIfAEpisodeContainsInvalidRatingData(){
        Series s = new Series("The Neo Ninja", 2020);
        s.addEpisode(new Episode("The Hidden Village",s,120,199,50,430));
        s.addEpisode(new Episode("The First Mission",s,140,126,60,610));
        s.addEpisode(new Episode("The Rival",s,120,199,0,0));
        s.addEpisode(new Episode("The Tournament",s,180, 162, 90, 810));
        s.addEpisode(new Episode("The Final Battle",s, 200, 190, 120, 1080));
        try { 
           int value=s.rating();
           fail("Did not throw exception");
        } catch (NeoFlixException e) {
            assertEquals(NeoFlixException.DATA_ERROR,e.getMessage());
        }    
    }     

    private Episode ep(int attempts, int completed, int votes, int sumVotes){
        return new Episode("Episodio", null, attempts, completed, votes, sumVotes);
    }

    @Test
    public void shouldCalculateSeriesRatingWithDefaultWhenAllRatingsAreKnown(){
        Series s = new Series("The Neo Ninja", 2020);
        s.addEpisode(ep(100,90,50,430));    // 8
        s.addEpisode(ep(100,90,60,510));    // 8
        s.addEpisode(ep(100,90,50,430));    // 8
        s.addEpisode(ep(100,90,90,810));    // 9
        s.addEpisode(ep(100,90,120,1080));  // 9
        try {
            assertEquals(8, s.rating(5));
        } catch (NeoFlixException e){
            fail("Threw a exception "+e.getMessage());
        }
    }

    @Test
    public void shouldUseTheDefaultValueForAnUnknownEpisodeRating(){
        Series s = new Series("The Neo Ninja", 2020);
        s.addEpisode(ep(100,90,50,430));    // 8
        s.addEpisode(ep(100,90,60,510));    // 8
        s.addEpisode(ep(100,90,0,0));       // desconocida -> 5
        s.addEpisode(ep(100,90,90,810));    // 9
        s.addEpisode(ep(100,90,120,1080));  // 9
        try {
            assertEquals(7, s.rating(5));   // (8+8+5+9+9) / 5
        } catch (NeoFlixException e){
            fail("Threw a exception "+e.getMessage());
        }
    }

    @Test
    public void shouldIgnoreEpisodesWithDataErrorsAndReplaceUnknownRatings(){
        Series s = new Series("The Neo Ninja", 2020);
        s.addEpisode(ep(100,90,50,430));    // 8
        s.addEpisode(ep(100,90,10,101));    // error: se ignora
        s.addEpisode(ep(100,90,0,0));       // desconocida -> 5
        s.addEpisode(ep(100,90,90,810));    // 9
        s.addEpisode(ep(100,90,120,1080));  // 9
        try {
            assertEquals(7, s.rating(5));   // (8+5+9+9) / 4
        } catch (NeoFlixException e){
            fail("Threw a exception "+e.getMessage());
        }
    }

    @Test
    public void shouldUseTheDefaultValueForEveryUnknownEpisodeRating(){
        Series s = new Series("The Neo Ninja", 2020);
        s.addEpisode(ep(100,90,0,0));
        s.addEpisode(ep(100,90,0,0));
        s.addEpisode(ep(100,90,0,0));
        try {
            assertEquals(6, s.rating(6));
        } catch (NeoFlixException e){
            fail("Threw a exception "+e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionWhenEveryEpisodeHasDataErrors(){
        Series s = new Series("The Neo Ninja", 2020);
        s.addEpisode(ep(100,90,10,101));
        s.addEpisode(ep(100,90,20,201));
        try {
            s.rating(5);
            fail("Did not throw exception");
        } catch (NeoFlixException e){
            assertEquals(NeoFlixException.VALUE_UNKNOWN, e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionIfTheSeriesHasNoEpisodesWithDefault(){
        Series s = new Series("The Neo Ninja", 2020);
        try {
            s.rating(5);
            fail("Did not throw exception");
        } catch (NeoFlixException e){
            assertEquals(NeoFlixException.VALUE_UNKNOWN, e.getMessage());
        }
    }

    @Test
    public void shouldCalculateSeriesRatingWithSubstitutionWhenAllRatingsAreKnown(){
        Series s = new Series("The Neo Ninja", 2020);
        s.addEpisode(ep(100,90,50,430));
        s.addEpisode(ep(100,90,60,510));
        s.addEpisode(ep(100,90,50,430));
        s.addEpisode(ep(100,90,90,810));
        s.addEpisode(ep(100,90,120,1080));
        try {
            assertEquals(8, s.rating(true));
            assertEquals(8, s.rating(false));
        } catch (NeoFlixException e){
            fail("Threw a exception "+e.getMessage());
        }
    }

    @Test
    public void shouldUseTheAverageOfPreviousEpisodesWhenAnEpisodeRatingIsUnknown(){
        Series s = new Series("The Neo Ninja", 2020);
        s.addEpisode(ep(100,90,10,40));     // 4
        s.addEpisode(ep(100,90,0,0));       // desconocida -> promedio previo (4)
        s.addEpisode(ep(100,90,10,100));    // 10
        s.addEpisode(ep(100,90,10,100));    // 10
        try {
            assertEquals(7, s.rating(true)); // (4+4+10+10) / 4
        } catch (NeoFlixException e){
            fail("Threw a exception "+e.getMessage());
        }
    }

    @Test
    public void shouldUseTheAverageOfEveryEpisodeWhenAnEpisodeRatingIsUnknown(){
        Series s = new Series("The Neo Ninja", 2020);
        s.addEpisode(ep(100,90,10,40));     // 4
        s.addEpisode(ep(100,90,0,0));       // desconocida -> promedio total (8)
        s.addEpisode(ep(100,90,10,100));    // 10
        s.addEpisode(ep(100,90,10,100));    // 10
        try {
            assertEquals(8, s.rating(false)); // (4+8+10+10) / 4
        } catch (NeoFlixException e){
            fail("Threw a exception "+e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionWhenThereAreNoPreviousEpisodeRatings(){
        Series s = new Series("The Neo Ninja", 2020);
        s.addEpisode(ep(100,90,0,0));
        s.addEpisode(ep(100,90,10,80));
        s.addEpisode(ep(100,90,10,80));
        try {
            s.rating(true);
            fail("Did not throw exception");
        } catch (NeoFlixException e){
            assertEquals(NeoFlixException.VALUE_UNKNOWN, e.getMessage());
        }
    }

    @Test
    public void shouldUseTheAverageOfEveryEpisodeWhenTheFirstRatingIsUnknown(){
        Series s = new Series("The Neo Ninja", 2020);
        s.addEpisode(ep(100,90,0,0));
        s.addEpisode(ep(100,90,10,80));
        s.addEpisode(ep(100,90,10,80));
        try {
            assertEquals(8, s.rating(false));
        } catch (NeoFlixException e){
            fail("Threw a exception "+e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionWhenEveryEpisodeRatingIsUnknownWithSubstitution(){
        Series s = new Series("The Neo Ninja", 2020);
        s.addEpisode(ep(100,90,0,0));
        s.addEpisode(ep(100,90,0,0));
        try {
            s.rating(true);
            fail("Did not throw exception");
        } catch (NeoFlixException e){
            assertEquals(NeoFlixException.VALUE_UNKNOWN, e.getMessage());
        }
        try {
            s.rating(false);
            fail("Did not throw exception");
        } catch (NeoFlixException e){
            assertEquals(NeoFlixException.VALUE_UNKNOWN, e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionIfTheSeriesHasNoEpisodesWithSubstitution(){
        Series s = new Series("The Neo Ninja", 2020);
        try {
            s.rating(true);
            fail("Did not throw exception");
        } catch (NeoFlixException e){
            assertEquals(NeoFlixException.CONTENT_EMPTY, e.getMessage());
        }
        try {
            s.rating(false);
            fail("Did not throw exception");
        } catch (NeoFlixException e){
            assertEquals(NeoFlixException.CONTENT_EMPTY, e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionWhenAnEpisodeHasDataErrorsWithSubstitution(){
        Series s = new Series("The Neo Ninja", 2020);
        s.addEpisode(ep(100,90,10,40));
        s.addEpisode(ep(100,90,10,101));
        try {
            s.rating(true);
            fail("Did not throw exception");
        } catch (NeoFlixException e){
            assertEquals(NeoFlixException.DATA_ERROR, e.getMessage());
        }
        try {
            s.rating(false);
            fail("Did not throw exception");
        } catch (NeoFlixException e){
            assertEquals(NeoFlixException.DATA_ERROR, e.getMessage());
        }
    }

    @Test
    public void shouldCalculateSeriesPopularity(){
        Series s = new Series("The Neo Ninja", 2020);
        s.addEpisode(ep(200,100,0,0));   // 50
        s.addEpisode(ep(100,80,0,0));    // 80
        try {
            assertEquals(65, s.popularity()); // (50+80) / 2
        } catch (NeoFlixException e){
            fail("Threw a exception "+e.getMessage());
        }
    }

    @Test
    public void shouldIgnoreEpisodesWithoutAttemptsWhenCalculatingPopularity(){
        Series s = new Series("The Neo Ninja", 2020);
        s.addEpisode(ep(200,100,0,0));   // 50
        s.addEpisode(ep(0,0,0,0));       // desconocida: se ignora
        s.addEpisode(ep(100,80,0,0));    // 80
        try {
            assertEquals(65, s.popularity());
        } catch (NeoFlixException e){
            fail("Threw a exception "+e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionIfTheSeriesHasNoEpisodesWhenCalculatingPopularity(){
        Series s = new Series("The Neo Ninja", 2020);
        try {
            s.popularity();
            fail("Did not throw exception");
        } catch (NeoFlixException e){
            assertEquals(NeoFlixException.CONTENT_EMPTY, e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionWhenEveryEpisodePopularityIsUnknown(){
        Series s = new Series("The Neo Ninja", 2020);
        s.addEpisode(ep(0,0,0,0));
        s.addEpisode(ep(0,0,0,0));
        try {
            s.popularity();
            fail("Did not throw exception");
        } catch (NeoFlixException e){
            assertEquals(NeoFlixException.VALUE_UNKNOWN, e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionWhenAnEpisodeHasMoreCompletedThanAttempts(){
        Series s = new Series("The Neo Ninja", 2020);
        s.addEpisode(ep(200,100,0,0));
        s.addEpisode(ep(10,20,0,0));     // terminados > intentos: DATA_ERROR
        try {
            s.popularity();
            fail("Did not throw exception");
        } catch (NeoFlixException e){
            assertEquals(NeoFlixException.DATA_ERROR, e.getMessage());
        }
    }

    @Test
    public void shouldShowCompleteIndicatorsWhenListingTheSeries(){
        Series s = new Series("The Neo Ninja", 2020);
        s.addEpisode(new Episode("Ep A", s, 200,100,50,430));  // ( 50 - 8 )
        s.addEpisode(new Episode("Ep B", s, 100,80,10,80));    // ( 80 - 8 )
        assertEquals("The Neo Ninja: 2020 ( 65 - 8 )\n\tEp A ( 50 - 8 )\n\tEp B ( 80 - 8 )", s.data(true));
    }

    @Test
    public void shouldReportIncompleteIndicatorsAndStillListEpisodes(){
        Series s = new Series("The Neo Ninja", 2020);
        s.addEpisode(new Episode("Ep A", s, 0,0,0,0));
        String text = s.data(true);
        assertTrue(text.contains("*** Indicadores incompletos"));
        assertTrue(text.contains("Ep A"));
    }

}