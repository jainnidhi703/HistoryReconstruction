import edu.washington.cs.knowitall.morpha.MorphaStemmer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.tartarus.snowball.ext.PorterStemmer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public final class LuceneUtils {

    public enum Stemmer {Porter, Morpho, Porter2}

    private LuceneUtils() {}

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
}
