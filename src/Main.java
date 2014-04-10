public class Main {
    public static void main(String args[]) {

        // [Note] : This has to be the first line in main
        init();

        GUI gui = new GUI();
        gui.show();

    }


    /**
     * Code that needs to be executed before starting this app, goes here
     */
    public static void init() {
        Globals.setFullDEBUG(false);
        LuceneUtils.TotalWordCount = Settings.getTotalNoOfWords();
    }
}
