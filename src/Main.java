public class Main {
    public static void main(String args[]) {
//        GUI gui = new GUI();
//        gui.show();

        QRelInput qrel = new QRelInput("qrel/en.qrels.126-175.2011.txt");
        try {
            qrel.start(129, "/home/jaydeep/jackson.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
