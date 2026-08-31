//Each song is described by its title, artist, genre, duration, and rating.
//The title and artist are mandatory. The genre, duration, and rating may be unknown.
//The combination (title, artist) must be unique. Two songs cannot have the same title and artist.
//The duration (minutes) must be between 1 and 9.
//The rating must be between * and *****.
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Playlist {
    
    private final List<Song> songs;
    
    public Playlist(String[][] songs) {
        this(buildValidSongs(songs));
    }

    private Playlist(List<Song> songs) {
        this.songs = Collections.unmodifiableList(songs);
    }
    
    private static boolean containsEntry(List<Song> list, Song song) {
        for (Song s : list) {
            if (s.sameEntryAs(song)) {
                return true;
            }
        }
        return false;
    }
    
    private static List<Song> buildValidSongs(String[][] rows) {
        List<Song> valid = new ArrayList<>();
        if (rows != null) {
            for (String[] row : rows) {
                Song song = Song.from(row);
                if (song != null && !containsEntry(valid, song)) {
                    valid.add(song);
                }
            }
        }
        return valid;
    }
    
    public Playlist add(String [] song){
        Song candidate = Song.from(song);
        if (candidate == null || containsEntry(songs, candidate)) {
            return this;
        }
        List<Song> updated = new ArrayList<>(songs);
        updated.add(candidate);
        return new Playlist(updated);
    }
    
    public Playlist delete(String [] song){
        Song candidate = Song.from(song);
        if (candidate == null) {
            return this;
        }
        List<Song> updated = new ArrayList<>();
        boolean removed = false;
        for (Song s : songs) {
            if (!removed && s.sameEntryAs(candidate)) {
                removed = true;
                continue;
            }
            updated.add(s);
        }
        return removed ? new Playlist(updated) : this;
    }
    
    public Playlist select(String [] values){
        List<Song> selected = new ArrayList<>();
        for (Song s : songs) {
            if (s.matches(values)) {
                selected.add(s);
            }
        }
        return new Playlist(selected);
    }      

    public int size(){
        return songs.size();
    }    
    
   
    // Songs are in uppercase with unnecessary spaces removed.
    // Columns are aligned and separated by three spaces.
//TITLE    ARTIST          GENRE   DURATION   RATING
//ONE      U2              ROCK           4   *****
//NUMB     LINKIN PARK     ROCK           3
//ALIVE    PEARL JAM       ROCK           5   ****
//CREEP    RADIOHEAD       ROCK               *****
//DREAMS   FLEETWOOD MAC   .              4   ****
    @Override
    public String toString() {
        if (songs.isEmpty()) {
            return "";
        }
        int titleWidth = "TITLE".length();
        int artistWidth = "ARTIST".length();
        int genreWidth = "GENRE".length();
        for (Song s : songs) {
            titleWidth = Math.max(titleWidth, s.title().length());
            artistWidth = Math.max(artistWidth, s.artist().length());
            genreWidth = Math.max(genreWidth, s.genreOrDot().length());
        }
        titleWidth += 3;
        artistWidth += 3;
        genreWidth += 3;
        int durationWidth = "DURATION".length() + 3;

        StringBuilder sb = new StringBuilder();
        for (Song s : songs) {
            String line = justifyLeft(s.title(), titleWidth)
                    + justifyLeft(s.artist(), artistWidth)
                    + justifyLeft(s.genreOrDot(), genreWidth)
                    + justifyRight(s.durationOrBlank(), durationWidth)
                    + s.ratingOrBlank();
            sb.append(rstrip(line)).append("\n");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    private static String justifyLeft(String value, int width) {
        StringBuilder sb = new StringBuilder(value);
        while (sb.length() < width) {
            sb.append(' ');
        }
        return sb.toString();
    }

    private static String justifyRight(String value, int width) {
        StringBuilder sb = new StringBuilder();
        while (sb.length() < width - value.length()) {
            sb.append(' ');
        }
        sb.append(value);
        return sb.toString();
    }

    private static String rstrip(String value) {
        int end = value.length();
        while (end > 0 && value.charAt(end - 1) == ' ') {
            end--;
        }
        return value.substring(0, end);
    }
    
    public boolean equals(Playlist pl){
        return pl != null && this.songs.equals(pl.songs);
    }
    
    @Override
    public boolean equals(Object o){
        if (!(o instanceof Playlist)) return false;
        return equals((Playlist)o);
    }
    
    @Override
    public int hashCode() {
        return songs.hashCode();
    }
    
}
