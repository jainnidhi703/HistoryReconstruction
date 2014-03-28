public class Main {
    public static void main(String args[]) {
//        GUI gui = new GUI();
//        gui.show();

        QRelInput qrel = new QRelInput("qrel/en.qrels.126-175.2011.txt");
        try {
            qrel.start(128, "/home/jaydeep/train1.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
