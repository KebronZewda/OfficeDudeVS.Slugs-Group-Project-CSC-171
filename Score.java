import java.io.Serializable;
import java.sql.Date;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.lang.Comparable;

public class Score implements Comparable<Score> {
    private int score;
    private String scoreString;
    
    public Score(int score, String scoreString) {
        this.score = score;
        this.scoreString = scoreString;
    }

    @Override
    public int compareTo(Score other) {
        return other.score - this.score;
    } 

    public String toString() {
        return score + " " + scoreString;
    }

    public int getScore() {
        return score;
    }
    public String getScoreString() {
        return scoreString;
    }
}
