package domain;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * NeoFlix
 * @author DOPO
 * @version ECI 2026
 */

public class NeoFlix{
    private ArrayList<Content> contents;
    private TreeMap<String,Episode> episodes;

    /**
     * Create a NeoFlix
     */
    public NeoFlix(){
        contents = new ArrayList<Content>();
        episodes = new TreeMap<String,Episode>();
        addSomeSeries();
    }

    
    /**
    * Adds sample series and episodes to NeoFlix.
    */
    private void addSomeSeries(){
        
        String [][] episodes= {{"The Hidden Village","199","120","50","430"},
                               {"The First Mission","126","140","60","510"},
                               {"The Rival","199","120","50","430"},
                               {"The Tournament","162","180", "90", "810"},
                               {"The Final Battle","190", "200", "120", "1080"}};
        StringBuffer titles=new StringBuffer();
        for (String [] e : episodes){
            titles.append(e[0]+"\n");
        }
        
        for (String [] e: episodes){
            addEpisode(e[0],"?", e[1],e[2],e[3],e[4]);
        }
        String [][] series = {{"The Neo Ninja", "2020", titles.toString()}};
        for (String [] s: series){
            addSeries(s[0],s[1],s[2]);
        }


    }


    /**
     * Consult a contents
     * @param name
     */
    public Content consult(String name){
        Content c=null;
        for(int i=0;i<contents.size() && c == null;i++){
            if (contents.get(i).getTitle().compareToIgnoreCase(name)==0) 
               c=contents.get(i);
        }
        return c;
    }

    
    /**
    * Adds a new episode.
    * @param title
    * @param series
    * @param attempts
    * @param completed
    * @param votes
    * @param sumVotes
    */
    public void addEpisode(String title, String series, String attempts, String completed, String votes, String sumVotes){ 
        Series s = (Series) consult(series);
        Episode e=new Episode(title,s, Integer.parseInt(attempts), Integer.parseInt(completed), Integer.parseInt(votes), Integer.parseInt(sumVotes));
        contents.add(e);
        episodes.put(title.toUpperCase(),e); 
    }
    
    
    /**
     * Add a new series
     * @param title
     * @param year
     * @param theEpisodes the titles of the episodes separated by newlines
    */
    public void addSeries(String title, String year, String theEpisodes){ 
        Series s = new Series(title,Integer.parseInt(year));
        String [] aEpisodes= theEpisodes.split("\n");
        for (String te : aEpisodes){
            s.addEpisode(episodes.get(te.toUpperCase()));
        }
        contents.add(s);
    }

    /**
     * Returns the contents whose title starts with the given prefix
     * @param prefix
     * @return 
     */
    public ArrayList<Content> select(String prefix){
        ArrayList <Content> answers=new ArrayList<Content>();
        prefix=prefix.toUpperCase();
        for(int i=0;i<contents.size();i++){
            if(contents.get(i+1).getTitle().toUpperCase().startsWith(prefix)){
                answers.add(contents.get(i));
            }   
        }
        return answers;
    }


    
    /**
     * Return the data of the selected contents
     * @param selected the selected contents
     * @param withIndicators true if indicators should be included
     * @return  
     */
    public String data(ArrayList<Content> selected, boolean withIndicators){
        StringBuffer answer=new StringBuffer();
        answer.append(selected.size()+ " elementos\n");
        for(Content c : selected) {
            try{
                answer.append('>' + c.data(withIndicators));
                answer.append("\n");
            }catch(NeoFlixException e){
                answer.append("**** "+e.getMessage());
            }
        }    
        return answer.toString();
    }
    
    
     /**
     * Return the data of episodes with a prefix
     * @param prefix
     * @return  
     */ 
    public String search(String prefix){
        return data(select(prefix),true);
    }
    
    
    /**
     * Return the data of all episodes
     * @return  
     */    
    public String toString(){
        return data(contents,false);
    }
    
    /**
     * Consult the number of episodes
     * @return 
     */
    public int numberContents(){
        return contents.size();
    }

}
