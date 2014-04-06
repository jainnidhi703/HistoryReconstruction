import edu.cmu.lti.jawjaw.JAWJAW;
import edu.cmu.lti.jawjaw.pobj.POS;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Keeps track of Search Query
 */
public class SearchQuery {
    private static String mainQuery = "";

    public static List<Set<String>> expandedQuery = null;
    private static long totalMatchingDocs = 0;


    /**
     * Sets main search Query
     * along with some query expansion
     * @param query query to set
     */
    public static void setMainQuery(String query) {
        mainQuery = query;
        expandedQuery = new ArrayList<Set<String>>();

        for(String w : query.split(" ")) {
            Set<String> synonyms = new LinkedHashSet<String>();
            synonyms.add(w);
            for(POS pos : POS.values()) {
                Set<String> sy = JAWJAW.findSynonyms(w, pos);
                synonyms.addAll(sy);
            }
            expandedQuery.add(synonyms);
        }
    }


    /**
     * @return get expanded query
     */
    public static List<Set<String>> getExpandedQuery() {
        return expandedQuery;
    }


    /**
     * @return get main Query
     */
    public static String getMainQuery() {
        return mainQuery;
    }


    /**
     * Set total documents that match the query
     * @param docs
     */
    public static void setTotalMatchingDocs(long docs) {
        totalMatchingDocs = docs;
    }


    /**
     * @return total matching documents
     */
    public static long getTotalMatchingDocs() {
        return totalMatchingDocs;
    }


    /**
     * Clear SearchQuery after each run
     */
    public static void clear() {
        mainQuery = null;
        expandedQuery.clear();
        totalMatchingDocs = 0;
    }
}
