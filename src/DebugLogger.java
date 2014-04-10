import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;


/**
 * Class that prints debug info
 */
public class DebugLogger {

    private static List<List<DocumentClass>> docsInEachCluster = null;
    private static List<List<Sentence>> sentsInEachCluster = null;


    // values required for sentence similarity
    private static String[] similaritySents;
    private static LinkedHashSet<String> T;
    private static double[][] matrixT1, matrixT2;
    private static double[] s1, s2, ss1, ss2;
    private static int[] r1, r2;
    private static double ss, sr, similarityScore;


    /**
     * keep track of documents in each cluster
     * @param docs docs in a cluster
     */
    public static void addDocsInCluster(List<DocumentClass> docs) {
        if(docsInEachCluster == null)
            docsInEachCluster = new ArrayList<List<DocumentClass>>();
        docsInEachCluster.add(docs);
    }


    /**
     * @return get documents each cluster
     */
    public static List<List<DocumentClass>> getDocsInEachCluster() {
        return docsInEachCluster;
    }


    /**
     * keeps track of sentences in each cluster
     * @param sentences sentences in a cluster
     */
    public static void addSentencesInCluster(List<Sentence> sentences) {
        if(sentsInEachCluster == null)
            sentsInEachCluster = new ArrayList<List<Sentence>>();
        sentsInEachCluster.add(sentences);
    }


    /**
     * @return gets sentences in each cluster
     */
    public static List<List<Sentence>> getSentsInEachCluster() {
        return sentsInEachCluster;
    }


    /**
     * Puts the last sentences compared
     * @param s1 sentence1
     * @param s2 sentence2
     */
    public static void setSimilaritySents(String s1, String s2) {
        similaritySents = new String[]{s1, s2};
    }


    /**
     * @return sentences last compared
     */
    public static String[] getSimilaritySents() {
        return similaritySents;
    }


    /**
     * Similarity matrix for T1
     * @param mat matrix
     */
    public static void setMatrixT1(double[][] mat) {
        matrixT1 = mat;
    }


    /**
     * @return Similarity matrix for T1
     */
    public static double[][] getMatrixT1() {
        return matrixT1;
    }


    /**
     * Similarity matrix for T2
     * @param mat matrix
     */
    public static void setMatrixT2(double[][] mat) {
        matrixT2 = mat;
    }


    /**
     * @return Similarity matrix for T2
     */
    public static double[][] getMatrixT2() {
        return matrixT2;
    }


    /**
     * Set semantic vector for T1 before multiplying with IC
     * @param s semantic vector
     */
    public static void setSemanticVector_no_IC_T1(double[] s) {
        s1 = s;
    }


    /**
     * @return semantic vector for T1 before multiplying with IC
     */
    public static double[] getSemanticVector_no_IC_T1() {
        return s1;
    }


    /**
     * Set semantic vector for T2 before multiplying with IC
     * @param s semantic vector
     */
    public static void setSemanticVector_no_IC_T2(double[] s) {
        s2 = s;
    }


    /**
     * @return semantic vector for T2 before multiplying with IC
     */
    public static double[] getSemanticVector_no_IC_T2() {
        return s2;
    }


    /**
     * Set semantic vector for T1 after multiplying with IC
     * @param s semantic vector
     */
    public static void setSemanticVector_IC_T1(double[] s) {
        ss1 = s;
    }


    /**
     * @return semantic vector for T1 after multiplying with IC
     */
    public static double[] getSemanticVector_IC_T1() {
        return ss1;
    }


    /**
     * Set semantic vector for T2 after multiplying with IC
     * @param s semantic vector
     */
    public static void setSemanticVector_IC_T2(double[] s) {
        ss2 = s;
    }


    /**
     * @return semantic vector for T2 before multiplying with IC
     */
    public static double[] getSemanticVector_IC_T2() {
        return ss2;
    }


    /**
     * @return Word Order Vector for T1
     */
    public static int[] getWordOrderVector_r1() {
        return r1;
    }


    /**
     * @param r1 Word Order Vector for T1
     */
    public static void setWordOrderVector_r1(int[] r1) {
        DebugLogger.r1 = r1;
    }


    /**
     * @return Word Order Vector for T2
     */
    public static int[] getWordOrderVector_r2() {
        return r2;
    }


    /**
     * @param r2 Word Order Vector for T2
     */
    public static void setWordOrderVector_r2(int[] r2) {
        DebugLogger.r2 = r2;
    }


    /**
     * Clear DebugLogger after each run
     */
    public static void clear() {
        if(docsInEachCluster != null)
            docsInEachCluster.clear();
        if(sentsInEachCluster != null)
            sentsInEachCluster.clear();
    }


    /**
     * @return combined sentence T
     */
    public static LinkedHashSet<String> getCombinedSentence() {
        return T;
    }


    /**
     * @param t combined sentence T
     */
    public static void setCombinedSentence(LinkedHashSet<String> t) {
        T = t;
    }



    public static double getSemanticScore_ss() {
        return ss;
    }

    public static void setSemanticScore_ss(double ss) {
        DebugLogger.ss = ss;
    }

    public static double getWordOrderScore_sr() {
        return sr;
    }

    public static void setWordOrderScore_sr(double sr) {
        DebugLogger.sr = sr;
    }

    public static double getSimilarityScore_final() {
        return similarityScore;
    }

    public static void setSimilarityScore_final(double similarityScore) {
        DebugLogger.similarityScore = similarityScore;
    }
}