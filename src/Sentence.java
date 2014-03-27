import javafx.util.Pair;

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

//        Random rand = new Random(System.currentTimeMillis());
//        return rand.nextDouble();

        double score = 0;
        for(String w1 : s.getTokens()) {
            for(String w2 : this.getTokens()) {
                if(w1.equals(w2))
                    score += 1;
                else {
                    Double sTmp = Similarity.cache.get(new Pair<String, String>(w1, w2));
                    if(sTmp == null) {
                        sTmp = Similarity.lin.calcRelatednessOfWords(w1, w2);
                        score += sTmp;
                        Similarity.cache.put(new Pair<String, String>(w1, w2), sTmp);
                        Similarity.cache.put(new Pair<String, String>(w2, w1), sTmp);
                    } else {
                        score += sTmp;
                    }
                }
            }
        }

        score /= (s.getTokens().size()*this.getTokens().size());
        return score;
    }
}
