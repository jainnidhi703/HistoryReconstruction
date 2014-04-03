import java.util.ArrayList;
import java.util.List;

public class DebugLogger {
    public static List<List<XmlDocument>> docsInEachCluster = null;
    public static List<List<Sentence>> sentsInEachCluster = null;

    public static void setDocsInEachCluster(List<List<XmlDocument>> listing) {
        docsInEachCluster = listing;
    }

    public static void addDocsInCluster(List<XmlDocument> docs) {
        if(docsInEachCluster == null)
            docsInEachCluster = new ArrayList<List<XmlDocument>>();
        docsInEachCluster.add(docs);
    }

    public static List<List<XmlDocument>> getDocsInEachCluster() {
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
