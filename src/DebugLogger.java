import java.util.ArrayList;
import java.util.List;


/**
 * Class that prints debug info
 */
public class DebugLogger {
    private static List<List<DocumentClass>> docsInEachCluster = null;
    private static List<List<Sentence>> sentsInEachCluster = null;


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
     * Clear DebugLogger after each run
     */
    public static void clear() {
        if(docsInEachCluster != null)
            docsInEachCluster.clear();
        if(sentsInEachCluster != null)
            sentsInEachCluster.clear();
    }
}