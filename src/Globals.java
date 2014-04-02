
public class Globals {

    public static final String APP_NAME = "AuTo Summarizer";

    public static final double APP_VERSION = 0.2;

    public static final String XML_DATA_DIR = "/home/jaydeep/IR-data/en.docs.2011/data/en_BDNews24/";

    public static final String QREL_TOPIC_FILE = "qrel/en.topics.126-175.2011.txt";

    public static final String TEXT_DATA_DIR = "temp/";

    public static final String INDEX_STORE_DIR = "index/";

    public static final int RETRIEVAL_RESULT_COUNT = 100;

    public static final int SENTENCES_FROM_EACH_CLUSTER = 10;

    public static final int TOPIC_TITLE_WORD_COUNT = 5;

    public static final double LAMBDA_FOR_SENTENCE_SCORING = 0.5;

    public static final int GUI_WIDTH = 600;

    public static final int GUI_HEIGHT = 450;

    public static final int NUM_CLUSTERS = 5;

    public static final String PREFERENCES_NODE = "AutoSummarySEN";

    public static final int CENTROID_DOCS_IN_CLUSTER = 2;

    public static final int DEFAULT_SUMMARY_LENGTH = 50; //sentences

    public static final int TOPIC_MODELLING_ITERATIONS = 1500;

    // 1 : compare each document to it's title and assign a score to each doc in a cluster
    // 2 : search the title in the documents contained in a cluster
    public static final int DOC_SELECTION_METHOD = 1;


    // DEBUG_INFO
    public static final boolean SHOW_TOPICS = true;

    public static final boolean SHOW_DOCS_UNDER_CLUSTERS = true;

}
