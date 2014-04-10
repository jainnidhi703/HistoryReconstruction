import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * A vector that denotes the similarity of joint wordSet T
 * with Sentence T1
 *
 * [Note] : T = T1 + T2
 * Eg : T1 = I am here; T2 = He is there;
 * T = I am here he is there
 */
public class SemanticVector {

    private double similarityScores[] = null;
    private double scoresWithIC[] = null;
    private int firstWordIndex[] = null;
    private int size = 0;
    private double[][] matrix = null;

    /**
     * Semantic Vector Constructor
     * Computes Semantic Vector For Sentence T1
     * @param T1toks Sentence T1 LOWERCASE tokens
     * @param T joint wordSet of Sentence T1 and T2
     */
    public SemanticVector(List<String> T1toks, LinkedHashSet<String> T) throws IOException {
        this.size = T.size();

        similarityScores = new double[size];
        scoresWithIC = new double[size];
        firstWordIndex = new int[size];
        matrix = new double[T1toks.size()][size];

        int i = 0;  // column
        for(String w : T) {
            int indx = T1toks.indexOf(w);
            if(indx != -1) {
                this.set(i, 1.0, indx);
                matrix[indx][i] = 1.0;
            } else {
                int maxIndx = -1;
                double maxValue = 0.0;

                // find the word in T1 that has the max similarity score with `w`
                for (int i1 = 0, t1toksSize = T1toks.size(); i1 < t1toksSize; i1++) {
                    String ww = T1toks.get(i1);
                    double tmp = WordRelatedness.get(w, ww);

                    // if similarity score is less than
                    // similarity threshold then don't count it
                    if(tmp < Globals.SIMILARITY_THRESHOLD)
                        continue;
                    matrix[i1][i] = tmp;
                    if (tmp > maxValue) {
                        maxValue = tmp;
                        maxIndx = i1;
                    }
                }

                this.set(i, maxValue, maxIndx);
            }

            // increment index
            ++i;
        }

        // calculate semantic scores with
        // information content
        i = 0;
        for(String w: T) {
            String ww = w;

            if(firstWordIndex[i] != -1)
                ww = T1toks.get( firstWordIndex[i] );

            scoresWithIC[i] = similarityScores[i]
                    * LuceneUtils.informationContent(w)
                    *  LuceneUtils.informationContent(ww);

            // increment index
            ++i;
        }
    }

    /**
     * Set value at `indx` of semantic vector
     * @param indx index of Vector T
     * @param score similarity score w/o Information Content
     * @param i index of the word that is mapped with T[indx]
     */
    public void set(int indx, double score, int i) {
        similarityScores[indx] = score;
        firstWordIndex[indx] = i;
    }

    /**
     * Semantic similarity score of word at index `indx` in T
     * @param indx index of Vector T
     * @return semantic similarity of T[i]
     */
    public double getScoreWithIC(int indx) {
        return scoresWithIC[indx];
    }

    /**
     * Gets similarity score of word at index `indx` in T
     * @param indx index of Vector T
     * @return similarity score of T[indx]
     */
    public double getSimilarityScore(int indx) {
        return similarityScores[indx];
    }

    /**
     * @return score vector after multiplying Information Content
     */
    public double[] getScoreVector() {
        return scoresWithIC;
    }


    /**
     * @return similarity scores before multiplying with IC
     */
    public double[] getSimilarityScoreVector() {
        return similarityScores;
    }


    /**
     * Gets the wordIndex that is mapped to T[i]
     * @param indx index of Vector T
     * @return index of T1
     */
    public int getMatchedWordIndx(int indx) {
        return firstWordIndex[indx];
    }


    /**
     * @return Word order Vector
     */
    public int[] getWordOrderVector() {
        return firstWordIndex;
    }


    /**
     * Gives semantic score between two semantic vectors
     * @param s2 second semantic vector
     * @return semantic score
     */
    public double getSemanticScore(SemanticVector s2) {
        if(this.size != s2.size())
            throw new IllegalArgumentException("Length of s1 and s2 vectors is not equal");

        double scr = 0.0;
        for(int i = 0; i < size; ++i)
            scr += (this.getScoreWithIC(i) * s2.getScoreWithIC(i));

        scr /= ( this.magnitude(scoresWithIC) * s2.magnitude(s2.getScoreVector()) );
        return scr;
    }

    /**
     * Finds the similarity of word order between two sentences
     * @param s2 other semanticVector to compare to
     * @return Word Order Similarity
     */
    public double getWordOrderSimilarity(SemanticVector s2) {
        if(this.size != s2.size())
            throw new IllegalArgumentException("Length of s1 and s2 vectors is not equal");

        double scr = 0.0;
        int[] r = new int[size];
        int[] rr = new int[size];

        for(int i = 0; i < size; ++i) {
            r[i] = (this.firstWordIndex[i] + 1) - (s2.getMatchedWordIndx(i) + 1);
            rr[i] = (this.firstWordIndex[i] + 1) + (s2.getMatchedWordIndx(i) + 1);
        }

        scr = 1.0 - ( magnitude(r) / magnitude(rr) );
        return scr;
    }

    /**
     * @return Magnitude of given vector
     */
    public double magnitude(double[] values) {
        double sm = 0.0;
        for(double s : values) {
            sm += (s * s);
        }
        sm = Math.sqrt(sm);
        return sm;
    }

    /**
     * @return Magnitude of given vector
     */
    public double magnitude(int[] values) {
        double sm = 0.0;
        for(int s : values) {
            sm += (s * s);
        }
        sm = Math.sqrt(sm);
        return sm;
    }

    /**
     * @return size of SemanticVector
     */
    public int size() {
        return size;
    }


    /**
     * @return similarity matrix
     */
    public double[][] getMatrix() {
        return matrix;
    }
}

