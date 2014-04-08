import junit.framework.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class LuceneUtilsTest {

    @Test
    public void testStemWords() throws Exception {
        List<String> input = Arrays.asList("keeps", "king", "ran", "ate");
        List<String> opt = LuceneUtils.stemWords(input, LuceneUtils.Stemmer.Morpho);

        List<String> output = Arrays.asList("keep", "king", "run", "eat");
        for(int i = 0; i < opt.size(); ++i) {
            Assert.assertEquals(output.get(i), opt.get(i));
        }
    }

    @Test
    public void testInformationContent() throws Exception {
        double icForStopWords = 0.35;
        String[] stopwords = {"is", "was", "a", "an", "to", "be", "the", "do"};

        for(String s : stopwords) {
            double x = LuceneUtils.informationContent(s);
            String msg = "IC for '" + s + "' is " + x;
            Assert.assertEquals(msg, true, x < icForStopWords);
        }
    }
}
