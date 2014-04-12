/**
 * All configurations of this app are stored here
 */
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

    public static double LAMBDA_FOR_SENTENCE_SCORING = 0.75;

    public static final int GUI_WIDTH = 600;

    public static final int GUI_HEIGHT = 450;

    public static final int NUM_CLUSTERS = 5;

    public static final String PREFERENCES_NODE = "AutoSummarySEN";

    public static final int CENTROID_DOCS_IN_CLUSTER = 2;

    public static final int DEFAULT_SUMMARY_LENGTH = 50; //sentences

    public static final int TOPIC_MODELLING_ITERATIONS = 1500;

    public static final double SIMILARITY_THRESHOLD = 0.2;

    public static final double SEMANTIC_SIMILARITY_WEIGHTAGE = 0.85;

    // 1 : compare each document to it's title and assign a score to each doc in a cluster
    // 2 : search the title in the documents contained in a cluster
    // FIXME : decide upon a single method
    public static final int DOC_SELECTION_METHOD = 1;


    // DEBUG_INFO
    public static final String DEBUG_FILE_SUFFIX = "_debug";

    public static boolean SHOW_TOPICS = true;

    public static boolean SHOW_DOCS_UNDER_CLUSTERS = true;

    public static boolean SHOW_DOC_SCORE_UNDER_CLUSTERS = true;

    public static boolean SHOW_SENTENCE_SCORE_UNDER_CLUSTER = true;

    public static boolean SHOW_LINE_NUM = true;

    public static void setFullDEBUG(boolean bool) {
        Globals.SHOW_TOPICS = bool;
        Globals.SHOW_DOCS_UNDER_CLUSTERS = bool;
        Globals.SHOW_DOC_SCORE_UNDER_CLUSTERS = bool;
        Globals.SHOW_SENTENCE_SCORE_UNDER_CLUSTER = bool;
    }
}
