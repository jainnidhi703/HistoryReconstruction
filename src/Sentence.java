import java.util.Random;

public class Sentence {

    private int clusterID = -1;
    private String filename = null;
    private String date = null;
    private double score = -1.0;
    private String content = null;

    public Sentence(int id, String filename, String date, String content) {
        clusterID = id;
        this.filename = filename;
        this.date = date;
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getScore() {
        return score;
    }

    public String getDate() {
        return date;
    }

    public String getFilename() {
        return filename;
    }

    public int getClusterID() {
        return clusterID;
    }


    // FIXME : the trouble maker :P
    public double getSimilarity(Sentence s) {
        Random rand = new Random(System.currentTimeMillis());
        return rand.nextDouble();
    }
}
