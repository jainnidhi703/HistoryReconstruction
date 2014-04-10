import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * DataStructure to keep track of sentence properties
 */
public class Sentence {

    private int clusterID = -1;
    private String filename = null;
    private String date = null;
    private double score = -1.0;
    private String content = null;
    private List<String> tokens = null;

    /**
     * Creates sentence object
     * @param id clusterID
     * @param filename filename of the document this sentence came from
     * @param date publish date of the document of this sentence
     * @param content sentence
     */
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
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object s) {
        return this.toString().equals(s.toString());
    }

    @Override
    public String toString() {
        return content;
    }


    /**
     * Set sentence score
     * @param score score to set
     */
    public void setScore(double score) {
        this.score = score;
    }


    /**
     * @return get sentence score
     */
    public double getScore() {
        return score;
    }


    /**
     * @return get publish date of the document this sentence came from
     */
    public String getDate() {
        return date;
    }


    /**
     * @return get filename of the document this sentence came from
     */
    public String getFilename() {
        return filename;
    }


    /**
     * @return get clusterID where this document belongs
     */
    public int getClusterID() {
        return clusterID;
    }


    /**
     * @return get sentence tokens
     */
    public List<String> getTokens() {
        return tokens;
    }


    /**
     * Computes similarity between two sentences
     * @param s other sentence to compare to
     * @return semantic similarity
     */
    public double getSimilarity(Sentence s) {
        try {
            return Similarity.sentence(this.toString(), s.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}
