import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.LeacockChodorow;
import javafx.util.Pair;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * Class for Word-Word Similarity
 */
public class WordRelatedness {

    private static final ILexicalDatabase db = new NictWordNet();
    public static final RelatednessCalculator lch = new LeacockChodorow(db);

    // a cache is maintained which is faster to lookup
    // than to calculate the similarity between two words everytime
    public static TreeMap<Pair<String, String>, Double> cache = new TreeMap<Pair<String, String>, Double>(new Comparator<Pair<String, String>>() {
        @Override
        public int compare(Pair<String , String> a, Pair<String, String> b) {
            String str1 = a.getKey() + " " + a.getValue();
            String str2 = b.getKey() + " " + b.getValue();
            return str1.compareTo(str2);
        }
    });

    /**
     * Gets the semantic similarity between two words
     * @param w1 word1
     * @param w2 word2
     * @return similarity score
     */
    public static double get(String w1, String w2) {
        Double sTmp = cache.get(new Pair<String, String>(w1, w2));
        if(sTmp == null) {
            sTmp = lch.calcRelatednessOfWords(w1, w2);
            cache.put(new Pair<String, String>(w1, w2), sTmp);
            cache.put(new Pair<String, String>(w2, w1), sTmp);
            return sTmp;
        }
        return sTmp;
    }
}
