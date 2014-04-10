import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Class to retrieve results from the indexed data
 */
public class Retriever {

    private IndexSearcher searcher;
    private Analyzer standardAnalyzer;
    private Analyzer whiteSpaceAnalyzer;

    /**
     * Initializes objects and sets path to the directory where,
     * indexed data resides
     * @param indxDir path to indexed data
     * @throws IOException
     * @throws ParseException
     */
    public Retriever(String indxDir) throws IOException, ParseException {
        IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indxDir)));
        searcher = new IndexSearcher(reader);
        standardAnalyzer = new StandardAnalyzer(Version.LUCENE_46, CharArraySet.EMPTY_SET);
        whiteSpaceAnalyzer = new WhitespaceAnalyzer(Version.LUCENE_46);
    }


    /**
     * A generic search method
     * @param searchFor search query
     * @param searchInField search field
     * @param maxHits maximum no of results
     * @throws ParseException
     * @throws IOException
     * @deprecated
     */
    public void search(String searchFor, String searchInField, int maxHits) throws ParseException, IOException {
        QueryParser parser = new QueryParser(Version.LUCENE_46, searchInField, standardAnalyzer);
        Query qry = parser.parse(searchFor);
        System.out.println("Searching for: " + qry.toString());
        TopDocs results = searcher.search(qry, maxHits, new Sort(new SortField("title", SortField.Type.STRING)));
        ScoreDoc[] hits = results.scoreDocs;

        int numTotalHits = results.totalHits;
        SearchQuery.setTotalMatchingDocs(numTotalHits);
        System.out.println(numTotalHits + " total matching documents");

        for (ScoreDoc hit : hits) {
            Document doc = searcher.doc(hit.doc);
            System.out.println(doc.get("filename"));
//            System.out.printf("%.10f %s\n", hits[i].score, doc.get("filename"));
        }
    }


    /**
     * searches for the query in `title` and `contents` fields
     * @param searchFor search query
     * @param maxHits maximum no of results
     * @return relevant Documents
     * @throws ParseException
     * @throws IOException
     */
    public List<DocumentClass> topRelevantResults(String searchFor, int maxHits) throws ParseException, IOException {
        searchFor = searchFor.toLowerCase();
        QueryParser parser1 = new QueryParser(Version.LUCENE_46, "title",standardAnalyzer);
        Query qry1 = parser1.parse(searchFor);
        qry1.setBoost((float) 2.0);
        QueryParser parser2 = new QueryParser(Version.LUCENE_46, "contents",standardAnalyzer);

        BooleanQuery finalQuery = new BooleanQuery();
        finalQuery.add(qry1, BooleanClause.Occur.SHOULD);

        // retrieve docs using expanded query
        for(Set<String> st : SearchQuery.getExpandedQuery()) {

            BooleanQuery bq = new BooleanQuery();
            // any one word from the synonym list should occur in doc's content
            for(String w : st) {
                bq.add(parser2.parse(w), BooleanClause.Occur.SHOULD);
            }

            // at least one word from synonym list should occur
            finalQuery.add(bq, BooleanClause.Occur.MUST);
        }

        TopDocs results = searcher.search(finalQuery, maxHits);
        ScoreDoc[] hits = results.scoreDocs;

        int numTotalHits = results.totalHits;
        SearchQuery.setTotalMatchingDocs(numTotalHits);
        System.out.println(numTotalHits + " total matching documents");

        ArrayList<DocumentClass> documents = new ArrayList<DocumentClass>();
        for (ScoreDoc hit : hits) {
            Document doc = searcher.doc(hit.doc);
            DocumentClass hitDoc = new DocumentClass();
            hitDoc.setFilename(doc.get("filename"));
            hitDoc.setTitle(doc.get("title"));
            hitDoc.setContent(doc.get("contents"));
            hitDoc.setDate(doc.get("date"));
            documents.add(hitDoc);
        }

        return documents;
    }


    /**
     * Searches for a query in given files
     * @param searchFor search query
     * @param fileNames files to search in
     * @param maxHits maximum no of results
     * @return relevant Documents
     * @throws ParseException
     * @throws IOException
     */
    public List<DocumentClass> searchInGivenFiles(String searchFor, String[] fileNames, int maxHits) throws ParseException, IOException {
        QueryParser parser1 = new QueryParser(Version.LUCENE_46, "title",standardAnalyzer);
        Query qry1 = parser1.parse(searchFor);
        qry1.setBoost((float)2.0);
        QueryParser parser2 = new QueryParser(Version.LUCENE_46, "contents",standardAnalyzer);
        Query qry2 = parser2.parse(searchFor);
        QueryParser parser3 = new QueryParser(Version.LUCENE_46, "filename", whiteSpaceAnalyzer);

        BooleanQuery innerQuery1 = new BooleanQuery();
        innerQuery1.add(qry1, BooleanClause.Occur.SHOULD);
        innerQuery1.add(qry2, BooleanClause.Occur.SHOULD);

        BooleanQuery innerQuery2 = new BooleanQuery();
        for(String s : fileNames) {
            innerQuery2.add(parser3.parse(s), BooleanClause.Occur.SHOULD);
        }

        BooleanQuery finalQuery = new BooleanQuery();
        finalQuery.add(new BooleanClause(innerQuery1, BooleanClause.Occur.MUST));
        finalQuery.add(new BooleanClause(innerQuery2, BooleanClause.Occur.MUST));

        TopDocs results = searcher.search(innerQuery2, maxHits);
        ScoreDoc[] hits = results.scoreDocs;

        int numTotalHits = results.totalHits;
        SearchQuery.setTotalMatchingDocs(numTotalHits);
        System.out.println(numTotalHits + " total matching documents");

        ArrayList<DocumentClass> documents = new ArrayList<DocumentClass>();
        for (ScoreDoc hit : hits) {
            Document doc = searcher.doc(hit.doc);
            DocumentClass hitDoc = new DocumentClass();
            hitDoc.setFilename(doc.get("filename"));
            hitDoc.setTitle(doc.get("title"));
            hitDoc.setContent(doc.get("contents"));
            hitDoc.setScore((double) hit.score);
            documents.add(hitDoc);
        }

        return documents;
    }


    /**
     * Gives Documents from given filenames
     * @param fnames file names for which DocumentClasss are required
     * @return Documents
     * @throws ParseException
     * @throws IOException
     * @deprecated
     */
    public List<DocumentClass> filenamesToDocs(List<String> fnames) throws ParseException, IOException {
        QueryParser parser3 = new QueryParser(Version.LUCENE_46, "filename", whiteSpaceAnalyzer);
        BooleanQuery query = new BooleanQuery();
        for(String s : fnames) {
            query.add(parser3.parse(s), BooleanClause.Occur.SHOULD);
        }
        TopDocs results = searcher.search(query,fnames.size());
        ScoreDoc[] hits = results.scoreDocs;

        int numTotalHits = results.totalHits;
        SearchQuery.setTotalMatchingDocs(numTotalHits);
        System.out.println(numTotalHits + " total matching documents");

        List<DocumentClass> documents = new ArrayList<DocumentClass>();
        for (ScoreDoc hit : hits) {
            Document doc = searcher.doc(hit.doc);
            DocumentClass hitDoc = new DocumentClass();
            hitDoc.setFilename(doc.get("filename"));
            hitDoc.setTitle(doc.get("title"));
            hitDoc.setContent(doc.get("contents"));
            documents.add(hitDoc);
        }

        return documents;
    }
}
