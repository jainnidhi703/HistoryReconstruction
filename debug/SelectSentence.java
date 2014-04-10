import edu.stanford.nlp.util.StringUtils;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Used to determine what value of lambda should be used
 * in F1 & F2 measure
 */
public class SelectSentence {

    private static List<String> qry = null;
    private static List<String[]> docs = null;
    private static List<Cluster> clusters = null;
    private static String folder = "debug/output/";
    private static List<Double> values = new ArrayList<Double>();

    public static void main(String[] args) {

        for(int i = 50; i < 100; i+=5)
            values.add(i/100.0);

        init();
        for(int i = 0; i < docs.size(); ++i) {
            List< List<Sentence> > bunch = new ArrayList<List<Sentence>>();
            for(Double l : values) {
                init();
                Globals.LAMBDA_FOR_SENTENCE_SCORING = l;
                List<Sentence> sents = clusters.get(i).getTopKSentences(10, qry.get(i));
                bunch.add(sents);
            }
            putInFile(i, bunch);
        }

    }


    // FIXME : needs more queries
    public static void init() {
        LuceneUtils.TotalWordCount = Settings.getTotalNoOfWords();

        qry = new ArrayList<String>(){{
            add("Piracy in the world of entertainment");
            add("Bhopal gas tragedy");
            add("Cyber crime in India");
        }};
        docs = new ArrayList<String[]>(){{
            // Piracy in the world of entertainment
            add(new String[]{"en.13.55.290.2010.5.23", "en.13.55.469.2010.5.24"});

            // Bhopal gas tragedy
            add(new String[]{"en.13.59.461.2010.6.8", "en.13.59.492.2010.6.8"});

            // Cyber crime in India
            add(new String[]{"en.15.83.161.2008.9.7", "en.15.83.236.2008.9.5"});
        }};

        Retriever r = null;
        try {
            r = new Retriever(Globals.INDEX_STORE_DIR);
        } catch(IndexNotFoundException e){
            System.out.println("Index not found");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        clusters = new ArrayList<Cluster>();
        for(int i = 0; i < docs.size(); ++i) {
            try {
                clusters.add(new Cluster(i, qry.get(i), r.filenamesToDocs(Arrays.asList(docs.get(i)))));
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void putInFile(int indx, List< List<Sentence> > sents) {
        StringBuilder sb = new StringBuilder();
        sb.append("Query : [").append(qry.get(indx)).append("]\n\n");

        for(int i = 0; i < values.size(); ++i) {
            sb.append("lambda = ").append(values.get(i)).append("\n");
            sb.append("------------------------------------------------------------\n");
            List<Sentence> sts = sents.get(i);
            for (Sentence s : sts) {
                sb.append("[").append(s.getScore()).append("] : ").append(s).append("\n");
            }
            sb.append("\n");
        }

        StringUtils.printToFile(folder + "indx" + indx, sb.toString());
    }
}