import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Cluster {

    private int clusterID = -1;
    private String title = null;
    private String content = null;
    private List<Sentence> sentences = null;
    private List<Document> docs = null;

    public Cluster(int indx, String title) {
        this.clusterID = indx;
        this.title = title;
        sentences = new ArrayList<Sentence>();
        docs = new ArrayList<Document>();
    }

    public Cluster(int indx, String title, List<Document> docs) {
        this.clusterID = indx;
        this.title = title;
        this.docs = docs;
        sentences = new ArrayList<Sentence>();

        StringBuilder sb = new StringBuilder();
        for(Document d : docs) {
            String[] sents = d.getContent().split("\\.");
            for(String s : sents) {
                sentences.add(new Sentence(d.getClusterID(), d.getFilename(), d.getDate(), s));
            }
        }
        System.out.println("\n");
    }

    public void addDocument(Document doc) {
        String[] sents = doc.getContent().split("\\.");
        for(String s : sents) {
            sentences.add(new Sentence(doc.getClusterID(), doc.getFilename(), doc.getDate(), s));
        }
        this.docs.add(doc);
    }

    public int getClusterID() {
        return clusterID;
    }

    public void setClusterID(int id) {
        this.clusterID = id;
    }

    public String getTitle() {
        return title;
    }

    public List<Sentence> getSentences() {
        return sentences;
    }

    public void setSentences(List<Sentence> sentences) {
        this.sentences = sentences;
    }

    public String getContent() {
        return content;
    }

    public List<Document> getDocs() {
        return docs;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ").append(this.getClusterID()).append(" ] => ");
        for(Document d : this.getDocs()) {
            sb.append(d.getFilename()).append(", ");
        }
        return sb.toString();
    }

    private double F1(int indx, Sentence sentence, List<Sentence> sentences) {
        double sum = 0.0;
        for(int i = 0; i < sentences.size(); ++i) {
            if(i == indx) continue;
            sum += sentence.getSimilarity(sentences.get(i));
        }
        sum /= (sentences.size() - 1);
        return sum;
    }

    private double F2(Sentence sentence, String title) {
        return sentence.getSimilarity(new Sentence(-1, null,null, title));
    }

    private double getSentenceScore(int indx, Sentence s, List<Sentence> sentences, double lambda) {
        return (lambda*F1(indx,s, sentences)) + lambda * F2(s, title);
    }

    public List<Sentence> getTopKSentences(int K) {
        for(int i = 0; i < this.sentences.size(); ++i) {
            double score = getSentenceScore(i, sentences.get(i), sentences, Globals.LAMBDA_FOR_SENTENCE_SCORING);
            sentences.get(i).setScore(score);
        }

        Collections.sort(this.sentences, new Comparator<Sentence>() {
            @Override
            public int compare(Sentence s1, Sentence s2) {
                return Double.compare(s2.getScore(), s1.getScore());
            }
        });

        sentences = sentences.subList(0, K);

        return sentences;
    }
}
