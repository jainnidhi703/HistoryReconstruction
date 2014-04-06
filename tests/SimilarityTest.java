import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class SimilarityTest {

    private double expectedScore;   // expected similarity score
    private String s1;  // sentence1
    private String s2;  // sentence2

    // 0 = equals
    // -1 = less than
    // 1 = greater than
    private int comparision;

    @BeforeClass
    public static void init() {
        LuceneUtils.TotalWordCount = Settings.getTotalNoOfWords();
    }

    public SimilarityTest(int comp, double score, String[] sents) {
        this.s1 = sents[0];
        this.s2 = sents[1];
        this.expectedScore = score;
        this.comparision = comp;
    }

    @Parameterized.Parameters
    public static Collection<Object []> data() {
        Object[][] data = new Object[][] {
                // should equal 1.0
                {0, 1.0, new String[]{"These sentences are identical", "These sentences are identical"}},
                // should NOT equal 1.0
                {0, 1.0, new String[]{"Jack was killed by Bob", "Bob was killed by Jack"}}
        };
        return Arrays.asList(data);
    }

    @Test
    public void testSentence() throws Exception {
        String msg = "Improper sentence similarity\ns1 : " + s1 + "\ns2 : " + s2;
        double x = Similarity.sentence(s1, s2);
        if(comparision == 0) {
            Assert.assertEquals(msg, x, expectedScore, 0.000001);
        } else if (comparision < 0) {
            Assert.assertTrue(msg, x < expectedScore);
        } else if (comparision > 0) {
            Assert.assertTrue(msg, x > expectedScore);
        }
    }
}
