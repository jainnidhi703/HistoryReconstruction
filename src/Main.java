import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public class Main {
    public static void main(String args[]) throws Exception {

        // [Note] : This has to be the first line in main
        init();

        Gui gui=new Gui();
        gui.show();


//        index("/home/nidhi/IRdata/");

//        QRelInput qrel = new QRelInput(Globals.QREL_PATH);
//        qrel.start(126,"output.txt");

    }


    /**
     * Code that needs to be executed before starting this app, goes here
     */
    public static void init() {
        LuceneUtils.TotalWordCount = Settings.getTotalNoOfWords();
    }

    private static void index(String dir) throws IOException, XMLStreamException {
        Indxer indxer = new Indxer(Globals.INDEX_STORE_DIR);
        indxer.indxDir(dir);
        indxer.killWriter();
        System.out.println("done indexing!");
    }

}
