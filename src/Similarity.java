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

        // for debug purpose
        DebugLogger.setSimilaritySents(T1, T2);

        List<String> T1toks = Arrays.asList(T1.split("\\s"));
        List<String> T2toks = Arrays.asList(T2.split("\\s"));

        LinkedHashSet<String> T = new LinkedHashSet<String>();
        DebugLogger.setCombinedSentence(T);

        for(String w : T1toks)
            T.add(w);

        for(String w: T2toks)
            T.add(w);

        SemanticVector s1 = new SemanticVector(T1toks, T);

        DebugLogger.setMatrixT1(s1.getMatrix());
        DebugLogger.setSemanticVector_no_IC_T1(s1.getSimilarityScoreVector());
        DebugLogger.setSemanticVector_IC_T1(s1.getScoreVector());
        DebugLogger.setWordOrderVector_r1(s1.getWordOrderVector());


        SemanticVector s2 = new SemanticVector(T2toks, T);

        DebugLogger.setMatrixT2(s2.getMatrix());
        DebugLogger.setSemanticVector_no_IC_T2(s2.getSimilarityScoreVector());
        DebugLogger.setSemanticVector_IC_T2(s2.getScoreVector());
        DebugLogger.setWordOrderVector_r2(s2.getWordOrderVector());


        double Ss = s1.getSemanticScore(s2);
        DebugLogger.setSemanticScore_ss(Ss);


        double Sr = s1.getWordOrderSimilarity(s2);
        DebugLogger.setWordOrderScore_sr(Sr);

        final double lambda = Globals.SEMANTIC_SIMILARITY_WEIGHTAGE;

        double score = ( lambda * Ss ) + ( (1.0 - lambda) * Sr );
        DebugLogger.setSimilarityScore_final(score);

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
