import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Sentence {

    private int clusterID = -1;
    private String filename = null;
    private String date = null;
    private double score = -1.0;
    private String content = null;
    private List<String> tokens = null;

    public Sentence(int id, String filename, String date, String content) {
        clusterID = id;
        this.filename = filename;
        this.date = date;
        this.content = content;

        String[] toks = null;
        if(content != null)
            toks = content.split(" ");
        if(toks != null) {
            tokens = Arrays.asList(toks);
            tokens = LuceneUtils.stemWords(tokens, LuceneUtils.Stemmer.Morpho);
        } else {
            tokens = new ArrayList<String>();
        }
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

    public List<String> getTokens() {
        return tokens;
    }

    // FIXME : the trouble maker :P
    public double getSimilarity(Sentence s) {
        try {
            return Similarity.sentence(this.toString(), s.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}
