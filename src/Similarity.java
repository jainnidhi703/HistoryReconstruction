import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;


/**
 * New sentence-sentence similarity measure
 * based on word meaning as well as word ordering in sentences
 *
 * For a detailed explanation of the approach refer,
 * Sentence Similarity Based on Semantic Nets and Corpus Statistics
 * by Yuhua L et. al.
 */
public class Similarity {

    /**
     * Computes sentence-sentence similarity
     * @param T1 Sentence1
     * @param T2 Sentence2
     * @return similarity score
     * @throws IOException
     */
    public static double sentence(String T1, String T2) throws IOException {
        T1 = T1.toLowerCase();
        T2 = T2.toLowerCase();

        List<String> T1toks = Arrays.asList(T1.split("\\s"));
        List<String> T2toks = Arrays.asList(T2.split("\\s"));

        LinkedHashSet<String> T = new LinkedHashSet<String>();

        for(String w : T1toks)
            T.add(w);

        for(String w: T2toks)
            T.add(w);

        SemanticVector s1 = new SemanticVector(T1toks, T);
        SemanticVector s2 = new SemanticVector(T2toks, T);

        double Ss = s1.getSemanticScore(s2);
        double Sr = s1.getWordOrderSimilarity(s2);

        final double lambda = Globals.SEMANTIC_SIMILARITY_WEIGHTAGE;

        double score = ( lambda * Ss ) + ( (1.0 - lambda) * Sr );
        return score;
    }


    /**
     * Computes the score of a document when compared to a title
     * @param title title to compare with
     * @param doc document to compare
     * @return similarity score
     */
    public static double titleToDocument(String title, DocumentClass doc) {
        List<String> sents = IRUtils.splitSentences(doc.getContent());
        double score = 0.0;
        for(String s : sents) {
            try {
                score += Similarity.sentence(title, s.trim());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        score /= sents.size();
        return score;
    }
}
