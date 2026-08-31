import java.util.Objects;

class Song {

    private final String title;
    private final String artist;
    private final String genre;
    private final Integer duration;
    private final String rating;

    private Song(String title, String artist, String genre, Integer duration, String rating) {
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.duration = duration;
        this.rating = rating;
    }

    static Song from(String[] row) {
        if (row == null || row.length != 5) {
            return null;
        }
        String title = normalize(row[0]);
        String artist = normalize(row[1]);
        if (title == null || artist == null) {
            return null;
        }
        String genre = normalize(row[2]);

        Integer duration = null;
        if (row[3] != null) {
            try {
                duration = Integer.parseInt(row[3].trim());
            } catch (NumberFormatException e) {
                return null;
            }
            if (duration < 1 || duration > 9) {
                return null;
            }
        }

        String rating = null;
        if (row[4] != null) {
            rating = row[4].replaceAll("\\s+", "");
            if (!rating.matches("\\*{1,5}")) {
                return null;
            }
        }

        return new Song(title, artist, genre, duration, rating);
    }

    private static String normalize(String s) {
        if (s == null) {
            return null;
        }
        String cleaned = s.trim().replaceAll("\\s+", " ");
        return cleaned.isEmpty() ? null : cleaned.toUpperCase();
    }

    boolean sameEntryAs(Song other) {
        return title.equals(other.title) && artist.equals(other.artist);
    }
    
    String title() { return title; }
    String artist() { return artist; }
    String genreOrDot() { return genre == null ? "." : genre; }
    String durationOrBlank() { return duration == null ? "" : String.valueOf(duration); }
    String ratingOrBlank() { return rating == null ? "" : rating; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Song)) return false;
        Song other = (Song) o;
        return title.equals(other.title)
            && artist.equals(other.artist)
            && Objects.equals(genre, other.genre)
            && Objects.equals(duration, other.duration)
            && Objects.equals(rating, other.rating);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, artist, genre, duration, rating);
    }
    
    boolean matches(String[] criteria) {
        if (criteria == null || criteria.length != 5) {
            return false;
        }
        return fieldMatches(criteria[0], title)
            && fieldMatches(criteria[1], artist)
            && fieldMatches(criteria[2], genre)
            && fieldMatches(criteria[3], duration == null ? null : String.valueOf(duration))
            && fieldMatches(criteria[4], rating);
    }

    private static boolean fieldMatches(String criterion, String actual) {
        if (criterion == null) {
            return true;
        }
        String normalized = normalize(criterion);
        return normalized == null ? actual == null : normalized.equals(actual);
    }
    
}