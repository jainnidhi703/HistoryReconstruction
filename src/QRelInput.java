import com.itextpdf.text.DocumentException;
import edu.stanford.nlp.util.StringUtils;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Input data directly from Qrel file
 */
public class QRelInput {
    private String qRelPath = null;
    QRelInput(String qRelPath) {
        this.qRelPath = qRelPath;
    }


    /**
     * Get relevant documents from qrel file
     * @param r retriever object
     * @param queryNo qrel query no
     * @return relevant documents
     * @throws IOException
     * @throws ParseException
     */
    public List<DocumentClass> getDocsFromQrel(Retriever r, int queryNo) throws IOException, ParseException {
        BufferedReader br = new BufferedReader(new FileReader(qRelPath));
        List<String> lst = new ArrayList<String>();
        String str;
        do {
            str = br.readLine();
            String tmp = getFileName(queryNo, str);
            if(tmp != null)
                lst.add(tmp);
        } while(str != null);

        List<DocumentClass> docs = r.filenamesToDocs(lst);
        return docs;
    }


    /**
     * start creating summary for queryNo from qrel file
     * @param queryNo qrel query no
     * @param exportTo export summary to
     * @throws Exception
     */
    public void start(int queryNo, String exportTo) throws Exception {
        QRelTopicParser qParser = new QRelTopicParser(Globals.QREL_TOPIC_FILE, queryNo);
        SearchQuery.setMainQuery(qParser.getTitle());

        Retriever r;
        try{
            r = new Retriever(Globals.INDEX_STORE_DIR);
        } catch (IndexNotFoundException e) {
            System.out.println("No data found!");
            return;
        }
        List<DocumentClass> docs = getDocsFromQrel(r, queryNo);
        TopicModel modeller = new TopicModel();
        List<Cluster> clusters = null;
        clusters = modeller.getClusters(docs, r, Globals.NUM_CLUSTERS);

        List<Sentence> sentences = new ArrayList<Sentence>(Globals.DEFAULT_SUMMARY_LENGTH);
        for (Cluster c : clusters) {
            sentences.addAll(c.getTopKSentences(
                    (int) Math.ceil( Globals.DEFAULT_SUMMARY_LENGTH/(double) clusters.size()),
                    SearchQuery.getMainQuery()));
        }

        // FIXME : crude way to solve redundancy
        // it will reduce the number of sentences in output
        sentences = new ArrayList<Sentence>(new HashSet<Sentence>(sentences));

        Collections.sort(sentences, new Comparator<Sentence>() {
            @Override
            public int compare(Sentence s1, Sentence s2) {
                return s1.getDateWithPlacement().compareTo(s2.getDateWithPlacement());
            }
        });

        String output = ExportDocument.generateContent(sentences, clusters);
        String debugContent = ExportDocument.generateDebugContent(clusters);

        if(exportTo.endsWith(".pdf")) {
            try {
                ExportDocument.toPDF(exportTo, SearchQuery.getMainQuery(), "QRel Query No. : " + queryNo, output);
                int extIndx = exportTo.lastIndexOf(".");
                exportTo = exportTo.substring(0,(extIndx==-1)?exportTo.length():extIndx) + Globals.DEBUG_FILE_SUFFIX + ".pdf";
                ExportDocument.printToPDF(exportTo, debugContent);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (DocumentException e1) {
                e1.printStackTrace();
            }
        } else {
            try {
                ExportDocument.toText(exportTo, SearchQuery.getMainQuery(), "QRel Query No. : " + queryNo, output);
                int extIndx = exportTo.lastIndexOf(".");
                exportTo = exportTo.substring(0, (extIndx==-1)?exportTo.length():extIndx) + Globals.DEBUG_FILE_SUFFIX + ".txt";
                StringUtils.printToFile(exportTo, debugContent);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        PostRunner.run();

        System.out.println("Done!");
    }


    /**
     * Get relevant file name
     * @param indx qrel query no
     * @param str line in qrel file
     * @return return relevant filename
     */
    private String getFileName(int indx, String str) {
        if(str == null) return null;
        String[] spl = str.split(" ");
        if(indx == Integer.parseInt(spl[0]) && /*spl[2].startsWith("en") &&*/ str.endsWith("1")) {
            return spl[2];
        }
        return null;
    }


    public int getNumberOfRelevantDocs(Integer queryNo) throws IOException,NumberFormatException{
        int cnt = 0;
        BufferedReader br = new BufferedReader(new FileReader(qRelPath));
        String str = br.readLine();
        while(str !=null) {

            String[] spl = str.split(" ");
            if (!spl[0].equals("")) {
                if (queryNo == Integer.parseInt(spl[0]) && str.endsWith("1")) {
                    cnt++;
                }
            }
            str = br.readLine();
        }

        return cnt;
    }
}
