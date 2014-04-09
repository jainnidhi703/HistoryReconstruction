import edu.washington.cs.knowitall.morpha.MorphaStemmer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;
import org.tartarus.snowball.ext.PorterStemmer;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Contains Lucene related misc functions
 */
public final class LuceneUtils {

    public enum Stemmer {Porter, Morpho, Porter2}

    private static IndexReader ir = null;

    public static long TotalWordCount = 0;

    // looking up lucene index everytime is costly
    // thus maintaining a cache would result in much better performance
    private static TreeMap<String, Long> freqCache = new TreeMap<String, Long>();

    private LuceneUtils() {}


    /**
     * Tokenizes string using given analyzer
     * @param analyzer analyzer to tokenize string
     * @param string string to tokenize
     * @return list of tonkenized words
     */
    public static List<String> tokenizeString(Analyzer analyzer, String string) {
        List<String> result = new ArrayList<String>();
        TokenStream stream  = null;
        try {
            stream  = analyzer.tokenStream(null, new StringReader(string));
            stream.reset();
            while (stream.incrementToken()) {
                result.add(stream.getAttribute(CharTermAttribute.class).toString());
            }
        } catch (IOException e) {
            // not thrown b/c we're using a string reader...
            throw new RuntimeException(e);
        } finally {
            if(stream != null) try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    /**
     * Generic stemmer method
     * @param lst list of words to stem
     * @param stmr type of stemmer to use
     * @return list of stemmed words
     */
    public static List<String> stemWords(List<String> lst, Stemmer stmr) {
        switch (stmr) {
            case Porter:
                PorterStemmer stemmer = new PorterStemmer();
                for(int i = 0; i < lst.size(); ++i) {
                    stemmer.setCurrent(lst.get(i));
                    stemmer.stem();
                    lst.set(i, stemmer.getCurrent());
                }
                break;
            case Morpho:
                // Morphological Stemmer
                for(int i = 0; i < lst.size(); ++i) {
                    lst.set(i, MorphaStemmer.stemToken(lst.get(i)));
                }
                break;
            case Porter2:
                org.tartarus.snowball.ext.PorterStemmer stemmer1 = new org.tartarus.snowball.ext.PorterStemmer();
                for(int i = 0; i < lst.size(); ++i) {
                    stemmer1.setCurrent(lst.get(i));
                    stemmer1.stem();
                    lst.set(i, stemmer1.getCurrent());
                }
                break;
        }

        // none of the above stemmers remove apostrophe S
        for(int i = 0; i < lst.size(); ++i) {
            String str = lst.get(i);
            str = str.replaceAll("\'$", "");
            lst.set(i, str);
        }
        return lst;
    }


    /**
     * Gives the term freq in the indexed corpus
     * @param word the word for which freq is requested
     * @return term frequency
     * @throws IOException
     */
    public static long getTermFreq(String word) throws IOException {
        if (ir == null)
            ir = DirectoryReader.open(FSDirectory.open(new File(Globals.INDEX_STORE_DIR)));

        // first look-up in cache
        Long f = freqCache.get(word);

        if(f == null) {
            // if not found
            Term termInstance = new Term("contents", word);
            long termFreq = ir.totalTermFreq(termInstance);

            // add it in cache
            freqCache.put(word, termFreq);
            return termFreq;
        }

        return f;
    }


    /**
     * Information Content(IC) : The IC of a word/concept(c) can be quantified as
     * the negative log likelihood [ -log p(c) ]. As probability of a word occurring in
     * a corpus increases, informativeness decreases, so the more abstract a concept,
     * the lower its information content
     *
     * @param word the word for which IC is requested
     * @return IC
     * @throws IOException
     */
    public static double informationContent(String word) throws IOException {
        long n; // freq of word in corpus
        long N; // Total no. of words in corpus

        n = getTermFreq(word);

        N = LuceneUtils.TotalWordCount;

        double ic = 1.0 - (Math.log(n + 1) / Math.log(N + 1));
        return ic;
    }
}
