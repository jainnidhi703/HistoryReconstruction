import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Exports results to Various file formats
 */
public class ExportDocument {

    private static final String HEADER = "Summary created using " + Globals.APP_NAME + " v[" + Globals.APP_VERSION + "]\n" +
            "============================================================\n\n";

    /**
     * Creates a proper title for the document, along with the query used
     * to get the results
     * @param title (query used to get the results)
     * @return String (title)
     */
    private static String getTitle(String title, String subTitle) {
        return "Query : " + title + " [ "+ subTitle + " ]\n------------------------------------------------------------\n\n";
    }

    /**
     * Generates a proper footer for the document,
     * along with current date/time
     * @return String (footer)
     */
    private static String getFooter() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return "\n\n\n- " + dateFormat.format(date);
    }


    /**
     * Exports the results to text file
     * @param path Path to store the text file
     * @param querySubtitle Query used to get the results
     * @param content What to write in the text file
     * @throws IOException
     */
    public static void toText(String path,String queryTitle, String querySubtitle, String content) throws IOException {
        final String subTitle = getTitle(queryTitle, querySubtitle);
        final String footer = getFooter();
        String contentToWrite = HEADER + subTitle + content + footer;
        FileWriter fw = new FileWriter(path);
        fw.write(contentToWrite);
        fw.close();
    }


    /**
     * Exports the results to PDF file
     * @param path Path to store the text file
     * @param querySubtitle Query used to get the results
     * @param content What to write in the text file
     * @throws FileNotFoundException
     * @throws DocumentException
     */
    public static void toPDF(String path, String queryTitle, String querySubtitle, String content) throws FileNotFoundException, DocumentException {
        final String subTitle = getTitle(queryTitle, querySubtitle);
        final String footer = getFooter();
        String contentToWrite = HEADER + subTitle + content + footer;

        com.itextpdf.text.Document doc = new com.itextpdf.text.Document();
        PdfWriter.getInstance(doc, new FileOutputStream(path));
        doc.open();
        doc.addCreationDate();
        doc.add(new Paragraph(contentToWrite));
        doc.close();
    }


    public static void printToPDF(String filePath, String content) throws FileNotFoundException, DocumentException {
        com.itextpdf.text.Document doc = new com.itextpdf.text.Document();
        PdfWriter.getInstance(doc, new FileOutputStream(filePath));
        doc.open();
        doc.addCreationDate();
        doc.add(new Paragraph(content));
        doc.close();
    }


    public static String generateContent(List<Sentence> sentences, List<Cluster> clusters) {
        if(sentences.isEmpty()) return "";

        LinkedHashSet<String> set = new LinkedHashSet<String>();
        StringBuilder sb = new StringBuilder();
        int docIndx = 0;
        int lineNum = 1;
        for (Sentence s : sentences) {
            if(set.add(s.getFilename())) {
                docIndx++;
            }
            if(Globals.SHOW_LINE_NUM)
                sb.append("[").append(lineNum++).append("] : ");
            sb.append(s.toString()).append("[").append(docIndx).append("]").append("\n");
        }
        sb.append("\n");
        sb.append("References:\n");
        sb.append("------------------------------------------------------------\n");
        docIndx = 1;
        for (String s : set) {
            sb.append("[").append(docIndx++).append("] : ").append(s).append("\n");
        }

        return sb.toString();
    }

    public static String generateIdealContent(List<String> sentences) {
        if(sentences.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        int lineNum = 1;
        for (String s : sentences) {
            if(Globals.SHOW_LINE_NUM)
                sb.append("[").append(lineNum++).append("] : ");
            sb.append(s).append("\n");
        }
        sb.append("\n");

        return sb.toString();
    }

    public static String generateDebugContent(List<Cluster> clusters) {
        if(clusters.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        int docIndx = 0;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(6);

        sb.append("Total matching docs : ").append(SearchQuery.getTotalMatchingDocs()).append("\n");

        if(Globals.SHOW_TOPICS) {
            sb.append("\n");
            sb.append("Topics:\n");
            sb.append("------------------------------------------------------------\n");
            docIndx = 0;
            for(Cluster s : clusters) {
                sb.append("[").append(docIndx++).append("] : ").append(s.getTitle()).append("\n");
            }
        }

        if(Globals.SHOW_DOCS_UNDER_CLUSTERS) {
            sb.append("\n");
            sb.append("Clusters:\n");
            sb.append("------------------------------------------------------------\n");
            docIndx = 0;
            for(Cluster s : clusters) {
                sb.append("[").append(docIndx++).append("] : ");
                for(DocumentClass d : s.getDocs())
                    sb.append(d.getFilename()).append(", ");
                sb.append("\n");
            }
        }

        if(Globals.SHOW_DOC_SCORE_UNDER_CLUSTERS) {
            sb.append("\n");
            sb.append("Document score under each cluster:\n");
            sb.append("------------------------------------------------------------\n");
            docIndx = 0;
            for(List<DocumentClass> dl : DebugLogger.getDocsInEachCluster()) {
                sb.append("[ Cluster : ").append(docIndx++).append(" ] : ").append("\n");
                for(DocumentClass d : dl) {
                    sb.append("[").append(df.format(d.getScore())).append("] : ").append(d.getFilename()).append("\n");
                }
                sb.append("\n");
            }
        }

        if(Globals.SHOW_SENTENCE_SCORE_UNDER_CLUSTER) {
            sb.append("\n");
            sb.append("Sentence score under each cluster:\n");
            sb.append("------------------------------------------------------------\n");
            docIndx = 0;
            for(List<Sentence> allSentences : DebugLogger.getSentsInEachCluster()) {
                sb.append("[ Cluster : ").append(docIndx++).append(" ] : ").append("\n");
                for(Sentence s : allSentences)
                    sb.append("[").append(df.format(s.getScore())).append("] : ").append(s.toString()).append("\n");
                sb.append("\n");
            }
        }

        return sb.toString();
    }
}
