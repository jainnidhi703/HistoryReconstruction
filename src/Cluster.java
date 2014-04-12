import java.util.*;

/**
 * DataStructure for storing Cluster related info
 */
public class Cluster {

    private int clusterID = -1;
    private String title = null;
    private List<Sentence> sentences = null;
    private List<DocumentClass> docs = null;

    /**
     * Cluster constructor
     * sets clusterID and title of cluster
     * @param indx clusterID
     * @param title title of cluster
     */
    public Cluster(int indx, String title) {
        this.clusterID = indx;
        this.title = title;
        sentences = new ArrayList<Sentence>();
        docs = new ArrayList<DocumentClass>();
    }

    /**
     * Cluster constructor
     * @param indx clusterID
     * @param title title of cluster
     * @param docs documents to include in this cluster
     */
    public Cluster(int indx, String title, List<DocumentClass> docs) {
        this.clusterID = indx;
        this.title = title;
        this.docs = docs;
        sentences = new ArrayList<Sentence>();

        keepOnlyImpDocs();
    }

    /**
     * Cluster constructor
     * @param indx clusterID
     * @param title title of cluster
     * @param docs documents to include in this cluster
     * @param impNum no of important documents to keep
     */
    public Cluster(int indx, String title, List<DocumentClass> docs, int impNum) {
        this.clusterID = indx;
        this.title = title;
        this.docs = docs;
        sentences = new ArrayList<Sentence>();

        keepOnlyImpDocs(impNum);
    }

    /**
     * More Documents in a cluster leads to more computation
     * So we keep only important documents
     * @param impNum no of important documents to keep
     */
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
            for (int i = 0; i < sents.size(); i++) {
                String s = sents.get(i);
                s = s.trim();
                if (s.split(" ").length > 2)
                    sentences.add(new Sentence(d.getClusterID(), d.getFilename(), d.getDate(), s, i/(double)sents.size()));
            }
        }
        sentences = new ArrayList<Sentence>(new HashSet<Sentence>(sentences));
    }


    /**
     * More Documents in a cluster leads to more computation
     * So we keep only important documents
     */
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
            for (int i = 0; i < sents.size(); i++) {
                String s = sents.get(i);
                s = s.trim();
                if (s.split(" ").length > 2)
                    sentences.add(new Sentence(d.getClusterID(), d.getFilename(), d.getDate(), s, i/(double)sents.size()));
            }
        }
        sentences = new ArrayList<Sentence>(new HashSet<Sentence>(sentences));
    }


    /**
     * More Documents in a cluster leads to more computation
     * So we keep only specified documents
     * @param filenames documents to keep
     */
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
            for (int i = 0; i < sents.size(); i++) {
                String s = sents.get(i);
                s = s.trim();
                if (s.split(" ").length > 2)
                    sentences.add(new Sentence(d.getClusterID(), d.getFilename(), d.getDate(), s, i/(double)sents.size()));
            }
        }

        sentences = new ArrayList<Sentence>(new HashSet<Sentence>(sentences));
    }

    /**
     * Adds a document to cluster
     * @param doc document to add
     * [Note] : need to call keep only imp doc after this
     */
    public void addDocument(DocumentClass doc) {
        this.docs.add(doc);
    }

    /**
     * @return cluster id
     */
    public int getClusterID() {
        return clusterID;
    }


    /**
     * Sets cluster id
     * @param id cluster id
     */
    public void setClusterID(int id) {
        this.clusterID = id;
    }


    /**
     * @return title of cluster
     */
    public String getTitle() {
        return title;
    }


    /**
     * Sets title of cluster
     * @param title title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }


    /**
     * Gets sentences from all the documents in cluster
     * @return sentences in cluster
     */
    public List<Sentence> getSentences() {
        return sentences;
    }


    /**
     * @return documents in the cluster
     */
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


    /**
     * Computes the similarity of sentence with index `indx` to
     * all other sentences in the cluster
     * @param indx index of the sentence
     * @param sentence sentence object of sentence with index `indx`
     * @param sentences all sentences
     * @return similarity score with other sentences
     */
    private double F1(int indx, Sentence sentence, List<Sentence> sentences) {
        double sum = 0.0;
        for(int i = 0; i < sentences.size(); ++i) {
            if(i == indx) continue;
            sum += sentence.getSimilarity(sentences.get(i));
        }
        sum /= (sentences.size() - 1);
        return sum;
    }


    /**
     * Computes the similarity of a sentence with query
     * @param sentence sentence object
     * @param title query
     * @return similarity score
     */
    private double F2(Sentence sentence, String title) {
        return sentence.getSimilarity(new Sentence(-1, null,null, title, 0.0));
    }


    /**
     * Sentence score = lambda(F1) + (1-lambda)F2;
     * For more details on sentence selection from a cluster, refer.
     * Multi-Document Summarization via Sentence-Level Semantic Analysis and Symmetric Matrix Factorization
     * [Section 3.4 Within-Cluster Sentence Selection]
     * by Dingding Wang, et. al
     *
     * @param indx index of the sentence
     * @param s sentence object
     * @param sentences all sentences
     * @param lambda value of lambda
     * @param query query of the search
     * @return sentence score
     */
    private double getSentenceScore(int indx, Sentence s, List<Sentence> sentences, double lambda, String query) {
        return (lambda*F1(indx,s, sentences)) + (1.0-lambda) * F2(s, query);
    }


    /**
     * Gets K sentences with highest score
     * @param K no of sentences to select
     * @param query query of the search
     * @return list of K sentences
     */
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
        if(Globals.SHOW_SENTENCE_SCORE_UNDER_CLUSTER)
            DebugLogger.addSentencesInCluster(sentences);

        sentences = sentences.subList(0, Math.min(K, sentences.size()));

        return sentences;
    }
}
