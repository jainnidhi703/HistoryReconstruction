import com.itextpdf.text.DocumentException;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class QRelInput {
    private String qRelPath = null;
    QRelInput(String qRelPath) {
        this.qRelPath = qRelPath;
    }

    public List<XmlDocument> getXmlDocs(Retriever r, int queryNo) throws IOException, ParseException {
        BufferedReader br = new BufferedReader(new FileReader(qRelPath));
        List<String> lst = new ArrayList<String>();
        String str;
        do {
            str = br.readLine();
            String tmp = getFileName(queryNo, str);
            if(tmp != null)
                lst.add(tmp);
        } while(str != null);

        List<XmlDocument> xmls = r.filenamesToXmlDoc(lst);
        return xmls;
    }

    public void start(int queryNo, String exportTo) throws Exception {
        QRelTopicParser qParser = new QRelTopicParser(Globals.QREL_TOPIC_FILE, queryNo);
        SearchQuery.setMainQuery(qParser.getTitle());

        Retriever r = new Retriever(Settings.getStoreDir());
        List<XmlDocument> docs = getXmlDocs(r, queryNo);
        TopicModel modeller = new TopicModel();
        List<Cluster> clusters = null;
        clusters = modeller.getClusters(docs, r, Globals.NUM_CLUSTERS);



        List<Sentence> sentences = new ArrayList<Sentence>(Globals.DEFAULT_SUMMARY_LENGTH);
        for (Cluster c : clusters) {
            sentences.addAll(c.getTopKSentences(
                    (int) Math.ceil( Globals.DEFAULT_SUMMARY_LENGTH/(double) clusters.size()),
                    SearchQuery.getMainQuery()));
        }

        Collections.sort(sentences, new Comparator<Sentence>() {
            @Override
            public int compare(Sentence s1, Sentence s2) {
                return s1.getDate().compareTo(s2.getDate());
            }
        });

        String output = ExportDocument.generateContent(sentences, clusters);
        if(exportTo.endsWith(".txt")) {
            try {
                ExportDocument.toText(exportTo, SearchQuery.getMainQuery(), "QRel Query No. : " + queryNo, output);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else if(exportTo.endsWith(".pdf")) {
            try {
                ExportDocument.toPDF(exportTo, SearchQuery.getMainQuery(), "QRel Query No. : " + queryNo, output);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (DocumentException e1) {
                e1.printStackTrace();
            }
        }
        System.out.println("Done!");
    }

    private String getFileName(int indx, String str) {
        if(str == null) return null;
        String[] spl = str.split(" ");
        if(indx == Integer.parseInt(spl[0]) && /*spl[2].startsWith("en") &&*/ str.endsWith("1")) {
            return spl[2];
        }
        return null;
    }
}
