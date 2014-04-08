import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class WordRelatednessTest {

    private double expectedScore;   // expected similarity score
    private String w1;  // word1
    private String w2;  // word2

    // 0 = equals
    // -1 = less than
    // 1 = greater than
    private int comparison;

    public WordRelatednessTest(int comp, double score, String[] words) {
        this.w1 = words[0];
        this.w2 = words[1];
        this.expectedScore = score;
        this.comparison = comp;
    }

    @Parameterized.Parameters
    public static Collection<Object []> data() {
        Object[][] data = new Object[][] {
                // should equal 0.0
                {0, 0, new String[]{"run", ""}},
                // should equal 1.0
                {0, 1.0, new String[]{"fear", "fear"}},
                // should be greater than 0.9
                {1, 0.9, new String[]{"fear", "panic"}}
        };
        return Arrays.asList(data);
    }

    @Test
    public void testGet() throws Exception {
        String msg = "Improper word similarity\nw1 : " + w1 + "\nw2 : " + w2;
        double x = WordRelatedness.get(w1, w2);
        if(comparison == 0) {
            Assert.assertEquals(msg, x, expectedScore, 0.000001);
        } else if (comparison < 0) {
            Assert.assertTrue(msg, x < expectedScore);
        } else if (comparison > 0) {
            Assert.assertTrue(msg, x > expectedScore);
        }
    }
}
