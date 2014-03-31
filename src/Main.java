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

//        try {
//            Indxer indxer = new Indxer("./index");
//            indxer.indxDir("/home/jaydeep/IR-data/en.docs.2011/data");
//            System.out.println("Rubbish files : " + Indxer.dd);
//            indxer.killWriter();
//            System.out.println("done!");
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (XMLStreamException e) {
//            e.printStackTrace();
//        }
    }
}
