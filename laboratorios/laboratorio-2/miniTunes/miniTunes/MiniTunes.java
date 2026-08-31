import java.util.TreeMap;

/** MiniTunes.java
 * 
 * @author ESCUELA 2026-02
 */
    
public class MiniTunes{
    
    private TreeMap<String,Playlist> playlists;
    private boolean ok;
    
    public MiniTunes(){
        playlists = new TreeMap<>();
    }

    //Define a new playlist name
    public void define(String name){
        if (playlists.containsKey(name)) {
            ok = false;
            return;
        }
        playlists.put(name, new Playlist(new String[0][0]));
        ok = true;
    }
     
    //Assign a playlist to an existing playlist name
    //a := playlist
    public void assign(String a, String [] [] playlist){
        if (!playlists.containsKey(a)) {
            ok = false;
            return;
        }
        playlists.put(a, new Playlist(playlist));
        ok = true;
    }    

    //Return a playlist's size
    public int size(String a){
        Playlist pl = playlists.get(a);
        return pl == null ? -1 : pl.size();
    }
    
    //Returns the playlist names in alphabetical order. comma-separated
    @Override
    public String toString(){
        return String.join(", ", playlists.keySet());
    }
    
    // Returns the string representation of a playlist.
    public String toString(String name){
        Playlist pl = playlists.get(name);
        return pl == null ? null : pl.toString();
    }    
    
    //Assigns the value of a unary operation to a playlist name
    // a = b op parameters
    //The operator characters are: 'a' (add) , 'd' (delete),'s'(select)
    //For add and delete, the values correspond to the song data. For select, the parameters define the search pattern.
    public void assignUnary(String a, String b, char op, String [] values){
        if (!playlists.containsKey(a) || !playlists.containsKey(b)) {
            ok = false;
            return;
        }
        Playlist source = playlists.get(b);
        Playlist result;
        switch (op) {
            case 'a':
                result = source.add(values);
                break;
            case 'd':
                result = source.delete(values);
                break;
            case 's':
                result = source.select(values);
                break;
            default:
                ok = false;
                return;
        }
        playlists.put(a, result);
        ok = true;
    }
      
    
    //Assigns the value of a binary operation to a playlist name
    // a = b op c
    //The operator characters are:  'u' union, 'i' intersection, 'd' difference
    //Songs preserve their original order in the resulting playlist.
    public void assignBinary(String a, String b, char op, String c){
    }
  
   
    //If the last operation was successfully completed
    public boolean ok(){
        return ok;
    }
}
    



