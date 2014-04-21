import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public class Main {
    public static void main(String args[]) throws Exception {

        // [Note] : This has to be the first line in main
        init();

        // if required
//        index("/home/nidhi/IRsdata/");

        Gui g=new Gui();
        g.show();

//        QRelInput qrel = new QRelInput("qrel/en.qrels.126-175.2011.txt");
//        qrel.start(Globals.QUERY_NO, "/home/nidhi/ss.txt");





    }


    /**
     * Code that needs to be executed before starting this app, goes here
     */
    public static void init() {
        LuceneUtils.TotalWordCount = Settings.getTotalNoOfWords();
    }


    /**
     * Quick handy method to index
     * @param dir directory to index
     * @throws IOException
     * @throws XMLStreamException
     */
    private static void index(String dir) throws IOException, XMLStreamException {
        Indxer indxer = new Indxer(Globals.INDEX_STORE_DIR);
        indxer.indxDir(dir);
        indxer.killWriter();
        System.out.println("done indexing!");
    }
}
