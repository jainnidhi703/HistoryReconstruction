import edu.cmu.lti.jawjaw.JAWJAW;
import edu.cmu.lti.jawjaw.pobj.POS;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SearchQuery {
    private static String mainQuery = "";

    public static List<Set<String>> expandedQuery = null;

//    public static String expandedQuery = "";

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

    public static List<Set<String>> getExpandedQuery() {
        return expandedQuery;
    }

    public static String getMainQuery() {
        return mainQuery;
    }
}
