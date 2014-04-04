import java.util.ArrayList;
import java.util.List;

public class DebugLogger {
    private static List<List<DocumentClass>> docsInEachCluster = null;
    private static List<List<Sentence>> sentsInEachCluster = null;

    public static void addDocsInCluster(List<DocumentClass> docs) {
        if(docsInEachCluster == null)
            docsInEachCluster = new ArrayList<List<DocumentClass>>();
        docsInEachCluster.add(docs);
    }

    public static List<List<DocumentClass>> getDocsInEachCluster() {
        return docsInEachCluster;
    }

    public static void addSentencesInCluster(List<Sentence> sentences) {
        if(sentsInEachCluster == null)
            sentsInEachCluster = new ArrayList<List<Sentence>>();
        sentsInEachCluster.add(sentences);
    }

    public static List<List<Sentence>> getSentsInEachCluster() {
        return sentsInEachCluster;
    }
}
