import java.util.ArrayList;
import java.util.List;

public class DebugLogger {
    public static List<List<DocumentClass>> docsInEachCluster = null;
    public static List<List<Sentence>> sentsInEachCluster = null;

    public static void setDocsInEachCluster(List<List<DocumentClass>> listing) {
        docsInEachCluster = listing;
    }

    public static void addDocsInCluster(List<DocumentClass> docs) {
        if(docsInEachCluster == null)
            docsInEachCluster = new ArrayList<List<DocumentClass>>();
        docsInEachCluster.add(docs);
    }

    public static List<List<DocumentClass>> getDocsInEachCluster() {
        return docsInEachCluster;
    }

    public static void setSentencesInEachCluster(int indx, List<Sentence> sentences) {
//        sentsInEachCluster.set(indx, sentences);
        if(sentsInEachCluster == null)
            sentsInEachCluster = new ArrayList<List<Sentence>>();
        sentsInEachCluster.add(sentences);
    }

    public static List<List<Sentence>> getSentsInEachCluster() {
        return sentsInEachCluster;
    }
}
