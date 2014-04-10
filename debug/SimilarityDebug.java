import edu.stanford.nlp.util.StringUtils;

import java.io.IOException;
import java.text.DecimalFormat;


/**
 * Gives out all information about how Similarity
 * of two sentences is calculated
 */
public class SimilarityDebug {

    private static String T1, T2;

    public static void main(String[] args) throws IOException {

        init();

        T1 = "RAM keeps things being worked with".toLowerCase();
        T2 = "The CPU uses RAM as a short-term memory store".toLowerCase();

        double score = Similarity.sentence(T1, T2);

        StringUtils.printToFile("debug/output/similarity.html", generateDebugContent());
    }

    /**
     * Call before doing anything else
     */
    public static void init() {
        LuceneUtils.TotalWordCount = Settings.getTotalNoOfWords();
    }


    /**
     * @return Generate Html output
     */
    public static String generateDebugContent() {
        StringBuilder sb = new StringBuilder();

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(3);
        sb.append("T1 : ").append(T1).append("<br>");
        sb.append("T2 : ").append(T2).append("<br>");
        sb.append("T : ");
        for(String s : DebugLogger.getCombinedSentence())
            sb.append(s).append(" ");
        sb.append("<br><br>");

        sb.append("T1 similarity matrix<br>");
        sb.append("<table border=\"1\">");
        sb.append("<tr>");
        sb.append("<td></td>");
        for(String s : DebugLogger.getCombinedSentence())
            sb.append("<td>").append(s).append("</td>");
        sb.append("</tr>");
        int i = 0;
        String[] sT1 = T1.split("\\s");
        for(double[] arr : DebugLogger.getMatrixT1()) {
            sb.append("<tr>");
            sb.append("<td>").append(sT1[i++]).append("</td>");
            for(Double x : arr) {
                sb.append("<td>").append(df.format(x)).append("</td>");
            }
            sb.append("</tr>");
        }
        sb.append("<tr>");
        sb.append("<td>").append("-s-").append("</td>");
        for(double x : DebugLogger.getSemanticVector_no_IC_T1())
            sb.append("<td>").append(df.format(x)).append("</td>");
        sb.append("</tr>");
        sb.append("<tr>");
        sb.append("<td>").append("-wt-").append("</td>");
        for(int x : DebugLogger.getWordOrderVector_r1()) {
            sb.append("<td>");
            String msg;
            if(x == -1) msg = "";
            else msg = sT1[x];
            sb.append(msg);
            sb.append("</td>");
        }
        sb.append("</tr>");
        sb.append("</table>");
        sb.append("<br/><br/>");



        sb.append("T2 similarity matrix<br>");
        sb.append("<table border=\"1\" >");
        sb.append("<tr>");
        sb.append("<td></td>");
        for(String s : DebugLogger.getCombinedSentence())
            sb.append("<td>").append(s).append("</td>");//.append("\t");
        sb.append("</tr>");
        i = 0;
        String[] sT2 = T2.split("\\s");
        for(double[] arr : DebugLogger.getMatrixT2()) {
            sb.append("<tr>");
            sb.append("<td>").append(sT2[i++]).append("</td>");
            for(Double x : arr) {
                sb.append("<td>").append(df.format(x)).append("</td>");//.append("\t");
            }
            sb.append("</tr>");
        }
        sb.append("<tr>");
        sb.append("<td>").append("-s-").append("</td>");
        for(double x : DebugLogger.getSemanticVector_no_IC_T2())
            sb.append("<td>").append(df.format(x)).append("</td>");
        sb.append("</tr>");
        sb.append("<tr>");
        sb.append("<td>").append("-wt-").append("</td>");
        for(int x : DebugLogger.getWordOrderVector_r2()) {
            sb.append("<td>");
            String msg;
            if(x == -1) msg = "";
            else msg = sT2[x];
            sb.append(msg);
            sb.append("</td>");
        }
        sb.append("</tr>");
        sb.append("</table>");
        sb.append("<br/><br/>");


        sb.append("<table border=\"1\">");
        sb.append("<tr>");
        sb.append("<td>").append("Semantic Vector 1 : ").append("</td>");
        for(double x : DebugLogger.getSemanticVector_no_IC_T1())
            sb.append("<td>").append(df.format(x)).append("</td>");
        sb.append("</tr>");


        sb.append("<td>").append("Semantic Vector 2 : ").append("</td>");
        for(double x : DebugLogger.getSemanticVector_no_IC_T2())
            sb.append("<td>").append(df.format(x)).append("</td>");
        sb.append("</tr>");
        sb.append("</table>");

        sb.append("<br/><br/>");

        sb.append("<table border=\"1\">");
        sb.append("<td>").append("Semantic Vector 1 (with IC) : ").append("</td>");
        for(double x : DebugLogger.getSemanticVector_IC_T1())
            sb.append("<td>").append(df.format(x)).append("</td>");
        sb.append("</tr>");


        sb.append("<td>").append("Semantic Vector 2 (with IC) : ").append("</td>");
        for(double x : DebugLogger.getSemanticVector_IC_T2())
            sb.append("<td>").append(df.format(x)).append("</td>");
        sb.append("</tr>");
        sb.append("</table>");

        sb.append("<br/><br/>");

        sb.append("<table border=\"1\">");
        sb.append("<td>").append("Word Order Vector (R1) : ").append("</td>");
        for(int x : DebugLogger.getWordOrderVector_r1())
            sb.append("<td>").append(df.format(x+1.0)).append("</td>");
        sb.append("</tr>");

        sb.append("<td>").append("Word Order Vector (R2) : ").append("</td>");
        for(int x : DebugLogger.getWordOrderVector_r2())
            sb.append("<td>").append(df.format(x+1.0)).append("</td>");
        sb.append("</tr>");
        sb.append("</table>");

        sb.append("<br/><br/>");

        sb.append("<table>");
        sb.append("<tr>");
        sb.append("<td>").append("Semantic Similarity : ").append("</td><td>").append(DebugLogger.getSemanticScore_ss()).append("</td>");
        sb.append("</tr>");
        sb.append("<tr>");
        sb.append("<td>").append("Word Order Similarity : ").append("</td><td>").append(DebugLogger.getWordOrderScore_sr()).append("</td>");
        sb.append("</tr>");
        sb.append("<tr>");
        sb.append("<td>").append("Overall Sentence similarity : ").append("</td><td>").append(DebugLogger.getSimilarityScore_final()).append("</td>");
        sb.append("</tr>");
        sb.append("</table>");

        return sb.toString();
    }
}
