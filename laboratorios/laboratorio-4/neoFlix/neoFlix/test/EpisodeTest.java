package test;
import domain.*;


import static org.junit.Assert.*;
import org.junit.Test;


public class EpisodeTest{

    private Episode ep(int attempts, int completed, int votes, int sumVotes){
        return new Episode("T", null, attempts, completed, votes, sumVotes);
    }

    @Test
    public void shouldCalculateEpisodePopularity(){
        Episode e = ep(200,150,0,0);
        try {
            assertEquals(75, e.popularity());
        } catch (NeoFlixException ex){
            fail("Threw a exception "+ex.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionWhenTheEpisodeHasNoAttempts(){
        Episode e = ep(0,0,0,0);
        try {
            e.popularity();
            fail("Did not throw exception");
        } catch (NeoFlixException ex){
            assertEquals(NeoFlixException.VALUE_UNKNOWN, ex.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionWhenCompletedExceedsAttempts(){
        Episode e = ep(10,20,0,0);
        try {
            e.popularity();
            fail("Did not throw exception");
        } catch (NeoFlixException ex){
            assertEquals(NeoFlixException.DATA_ERROR, ex.getMessage());
        }
    }

    @Test
    public void shouldCalculatePopularityAtTheLimit(){
        Episode e = ep(10,10,0,0);
        try {
            assertEquals(100, e.popularity());
        } catch (NeoFlixException ex){
            fail("Threw a exception "+ex.getMessage());
        }
    }

    @Test
    public void shouldCalculateEpisodeRating(){
        Episode e = ep(0,0,50,430);
        try {
            assertEquals(8, e.rating());
        } catch (NeoFlixException ex){
            fail("Threw a exception "+ex.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionWhenTheEpisodeHasNoVotes(){
        Episode e = ep(0,0,0,0);
        try {
            e.rating();
            fail("Did not throw exception");
        } catch (NeoFlixException ex){
            assertEquals(NeoFlixException.VALUE_UNKNOWN, ex.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionWhenSumOfVotesExceedsVotesTimesTen(){
        Episode e = ep(0,0,60,610);
        try {
            e.rating();
            fail("Did not throw exception");
        } catch (NeoFlixException ex){
            assertEquals(NeoFlixException.DATA_ERROR, ex.getMessage());
        }
    }

    @Test
    public void shouldCalculateRatingAtTheLimit(){
        Episode e = ep(0,0,10,100);
        try {
            assertEquals(10, e.rating());
        } catch (NeoFlixException ex){
            fail("Threw a exception "+ex.getMessage());
        }
    }

    @Test
    public void shouldReturnOnlyTheTitleWhenDataIsCalledWithoutIndicators(){
        Episode e = ep(200,150,50,430);
        assertEquals("T", e.data(false));
    }

    @Test
    public void shouldReturnDataWithIndicatorsOrIncompleteMark(){
        Episode e = ep(200,150,50,430);
        assertEquals("T ( 75 - 8 )", e.data(true));
        Episode f = ep(0,0,0,0);
        assertTrue(f.data(true).contains("*** Indicadores incompletos"));
    }
}
