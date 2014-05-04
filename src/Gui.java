import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by nidhi on 18/4/14.
 */
public class Gui {
    private JSpinner lambdavalue;
    private JPanel RootPanel;
    private JSpinner thresholdvalue;
    private JSpinner deltavalue;
    private JButton submit;
    private JSpinner queryno;
    private JTextField outputpath;
    private JSpinner numberoftopics;
    private JButton browseOutputDir;
    private JSpinner summaryLength;

    Gui() {
        SpinnerNumberModel lambda = new SpinnerNumberModel(0.1, 0.1, 0.9, 0.05);
        SpinnerNumberModel threshold = new SpinnerNumberModel(0.1, 0.1, 0.9, 0.05);
        SpinnerNumberModel delta = new SpinnerNumberModel(0.1, 0.1, 0.9, 0.05);
        SpinnerNumberModel query = new SpinnerNumberModel(126, 126, 175, 1);
        SpinnerNumberModel clusters = new SpinnerNumberModel(5, 4, 10, 1);
        SpinnerNumberModel summarylen = new SpinnerNumberModel(20 , 20 , 50 , 5);
        lambdavalue.setModel(lambda);
        thresholdvalue.setModel(threshold);
        deltavalue.setModel(delta);
        queryno.setModel(query);
        numberoftopics.setModel(clusters);
        summaryLength.setModel(summarylen);


        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
        }

        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Globals.LAMBDA_FOR_SENTENCE_SCORING = (Double) lambdavalue.getValue();
                Globals.SIMILARITY_THRESHOLD = (Double) thresholdvalue.getValue();
                Globals.SEMANTIC_SIMILARITY_WEIGHTAGE = (Double) deltavalue.getValue();
                Globals.QUERY_NO = (Integer) queryno.getValue();
                Globals.OUTPUT_PATH = outputpath.getText();
                Globals.NUM_CLUSTERS = (Integer) numberoftopics.getValue();
                Globals.DEFAULT_SUMMARY_LENGTH = (Integer) summaryLength.getValue();

                QRelInput qrel = new QRelInput("qrel/en.qrels.126-175.2011.txt");
                try {
                    qrel.start(Globals.QUERY_NO, Globals.OUTPUT_PATH);
                } catch (Exception e1) {
                    e1.printStackTrace();

                }
            }
        });


        browseOutputDir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(new File("."));
                fileChooser.setDialogTitle("Select output directory");
                final int chooseStatus = fileChooser.showSaveDialog(RootPanel);
                if (chooseStatus == JFileChooser.APPROVE_OPTION) {
                    outputpath.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
    }


    public void show(){
        JFrame frame = new JFrame("AuTo Summarizer");
        frame.setContentPane(new Gui().RootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(Globals.GUI_WIDTH, Globals.GUI_HEIGHT);
        frame.setResizable(false);
        frame.setVisible(true);

    }


}
