import java.util.ArrayList;
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
}
