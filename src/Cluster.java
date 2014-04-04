import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Cluster {

    private int clusterID = -1;
    private String title = null;
    private String content = null;
    private List<Sentence> sentences = null;
    private List<DocumentClass> docs = null;

    public Cluster(int indx, String title) {
        this.clusterID = indx;
        this.title = title;
        sentences = new ArrayList<Sentence>();
        docs = new ArrayList<DocumentClass>();
    }

    public Cluster(int indx, String title, List<DocumentClass> docs) {
        this.clusterID = indx;
        this.title = title;
        this.docs = docs;
        sentences = new ArrayList<Sentence>();

        keepOnlyImpDocs();
    }

    public Cluster(int indx, String title, List<DocumentClass> docs, int impNum) {
        this.clusterID = indx;
        this.title = title;
        this.docs = docs;
        sentences = new ArrayList<Sentence>();

        keepOnlyImpDocs(impNum);
    }

    public void keepOnlyImpDocs(int impNum) {
        if(title == null)
            throw new NullPointerException("Title is empty");
        List<DocumentClass> impDocs = new ArrayList<DocumentClass>();
        for(DocumentClass d : docs) {
            double scre = Similarity.titleToDocument(SearchQuery.getMainQuery(), d);
            d.score = scre;
            impDocs.add(d);
        }

        Collections.sort(impDocs, new Comparator<DocumentClass>() {
            @Override
            public int compare(DocumentClass d1, DocumentClass d2) {
                return Double.compare(d2.score, d1.score);
            }
        });

        docs = impDocs.subList(0, impNum);
        sentences.clear();

        for(DocumentClass d : docs) {
            List<String> sents = IRUtils.splitSentences(d.getContent());
//            String[] sents = d.getContent().split("\\.");
            for(String s : sents) {
                s = s.trim();
                if(s.split(" ").length > 2)
                    sentences.add(new Sentence(d.getClusterID(), d.getFilename(), d.getDate(), s));
            }
        }
    }

    public void keepOnlyImpDocs() {
        List<DocumentClass> impDocs = new ArrayList<DocumentClass>();
        for(DocumentClass d : docs) {
            double scre = Similarity.titleToDocument(SearchQuery.getMainQuery(), d);
            d.score = scre;
            impDocs.add(d);
        }

        Collections.sort(impDocs, new Comparator<DocumentClass>() {
            @Override
            public int compare(DocumentClass d1, DocumentClass d2) {
                return Double.compare(d2.score, d1.score);
            }
        });

        // for debug purpose Only
        DebugLogger.addDocsInCluster(impDocs);

        docs = impDocs.subList(0, Math.min(Globals.CENTROID_DOCS_IN_CLUSTER, impDocs.size()));
        sentences.clear();

        for(DocumentClass d : docs) {
            List<String> sents = IRUtils.splitSentences(d.getContent());
//            String[] sents = d.getContent().split("\\.");
            for(String s : sents) {
                s = s.trim();
                if(s.split(" ").length > 2)
                    sentences.add(new Sentence(d.getClusterID(), d.getFilename(), d.getDate(), s));
            }
        }
        System.out.println("\n");
    }

    public void keepOnlyGivenDocs(List<String> filenames) {
        List<DocumentClass> newDocs = new ArrayList<DocumentClass>(filenames.size());

        for(DocumentClass d : docs) {
            if(filenames.contains(d.getFilename())) {
                newDocs.add(d);
            }
        }
        docs = newDocs;

        sentences.clear();
        for(DocumentClass d : docs) {
            List<String> sents = IRUtils.splitSentences(d.getContent());
//            String[] sents = d.getContent().split("\\.");
            for(String s : sents) {
                s = s.trim();
                if(s.split(" ").length > 2)
                    sentences.add(new Sentence(d.getClusterID(), d.getFilename(), d.getDate(), s));
            }
        }
    }

    // need to call keep only imp doc after this
    public void addDocument(DocumentClass doc) {
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

    public void setTitle(String title) {
        this.title = title;
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

    public List<DocumentClass> getDocs() {
        return docs;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ").append(this.getClusterID()).append(" ] => ");
        for(DocumentClass d : this.getDocs()) {
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


    private double getSentenceScore(int indx, Sentence s, List<Sentence> sentences, double lambda, String query) {
        // formula taken from
        // Multi-Document Summarization via Sentence-Level Semantic Analysis and Symmetric Matrix Factorization
        // by Dingding Wang, et. al
        // Section 3.4 Within-Cluster Sentence Selection
        return (lambda*F1(indx,s, sentences)) + (1.0-lambda) * F2(s, query);
    }

    public List<Sentence> getTopKSentences(int K, String query) {
        System.out.println("Cluster ID : " + clusterID);
        for(int i = 0; i < this.sentences.size(); ++i) {
            System.out.println("sent score : " + i);
            double score;
            score = getSentenceScore(i, sentences.get(i), sentences, Globals.LAMBDA_FOR_SENTENCE_SCORING, query);
            sentences.get(i).setScore(score);
        }

        Collections.sort(this.sentences, new Comparator<Sentence>() {
            @Override
            public int compare(Sentence s1, Sentence s2) {
                return Double.compare(s2.getScore(), s1.getScore());
            }
        });

        // debug info
        DebugLogger.addSentencesInCluster(sentences);

        sentences = sentences.subList(0, Math.min(K, sentences.size()));

        return sentences;
    }
}
