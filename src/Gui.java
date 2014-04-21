import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Created by nidhi on 18/4/14.
 */
public class Gui {
    private JSpinner spinner1;
    private JPanel RootPanel;
    private JSpinner spinner2;
    private JSpinner spinner3;
    private JButton submit;
    private JSpinner spinner4;
    private JTextField textField1;
    private JLabel status;
    private JSpinner spinner5;

    Gui(){
    SpinnerNumberModel lambda=new SpinnerNumberModel(0.1,0.1,0.9,0.05);
    SpinnerNumberModel threshold=new SpinnerNumberModel(0.1,0.1,0.9,0.05);
    SpinnerNumberModel delta=new SpinnerNumberModel(0.1,0.1,0.9,0.05);
    SpinnerNumberModel query=new SpinnerNumberModel(126,126,175,1);
    SpinnerNumberModel clusters=new SpinnerNumberModel(5,4,10,1);
    spinner1.setModel(lambda);
    spinner2.setModel(threshold);
    spinner3.setModel(delta);
    spinner4.setModel(query);
    spinner5.setModel(clusters);
    textField1.setText(Globals.OUTPUT_PATH);


        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Globals.LAMBDA_FOR_SENTENCE_SCORING = (Double) spinner1.getValue();
                Globals.SIMILARITY_THRESHOLD = (Double) spinner2.getValue();
                Globals.SEMANTIC_SIMILARITY_WEIGHTAGE = (Double) spinner3.getValue();
                Globals.QUERY_NO = (Integer) spinner4.getValue();
                Globals.OUTPUT_PATH = textField1.getText();
                Globals.NUM_CLUSTERS = (Integer)spinner5.getValue();
                QRelInput qrel = new QRelInput("qrel/en.qrels.126-175.2011.txt");
                try {
                    qrel.start(Globals.QUERY_NO, Globals.OUTPUT_PATH);
                } catch (Exception e1) {
                    e1.printStackTrace();

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
