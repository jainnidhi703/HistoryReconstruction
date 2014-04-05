public class Main {
    public static void main(String args[]) {

        // [Note] : This has to be the first line in main
        init();


        GUI gui = new GUI();
        gui.show();

//        QRelInput qrel = new QRelInput("qrel/en.qrels.126-175.2011.txt");
//        try {
//            qrel.start(143, "/home/jaydeep/IdeaProjects/sampleSummaries/TataNano.txt");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


    }


    /**
     * Code that needs to be executed before starting this app, goes here
     */
    public static void init() {
        LuceneUtils.TotalWordCount = Settings.getTotalNoOfWords();
    }
}
