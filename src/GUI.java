import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.io.File;

/**
 * Class for Graphical User Interface
 * and it's handlers
 */
public class GUI {
    private JTextField dataDirField;
    private JTextField storeDirField;
    private JButton startIndexingButton;
    private JProgressBar progressBar1;
    private JTextField queryField;

    /**
     * Constructor : initializes elements and creates event handlers
     */
    public GUI() {

        initializeGUIValues();


        // Action Listeners ----

        dataDirBrowse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(new File("."));
                fileChooser.setDialogTitle("Select data directory");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                final int chooseStatus = fileChooser.showOpenDialog(rootJpanel);
                if (chooseStatus == JFileChooser.APPROVE_OPTION) {
                    dataDirField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

        storeDirBrowse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(new File("."));
                fileChooser.setDialogTitle("Select data directory");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                final int chooseStatus = fileChooser.showOpenDialog(rootJpanel);
                if (chooseStatus == JFileChooser.APPROVE_OPTION) {
                    storeDirField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

        startIndexingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // start Indexing
            }
        });


        exportBrowse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(new File("."));
                fileChooser.setDialogTitle("Select data directory");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                final int chooseStatus = fileChooser.showSaveDialog(rootJpanel);
                if (chooseStatus == JFileChooser.APPROVE_OPTION) {
                    storeDirField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

        filterResultsCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.DESELECTED) {
                    fromSpinner.setEnabled(false);
                    toSpinner.setEnabled(false);
                } else if(e.getStateChange() == ItemEvent.SELECTED) {
                    fromSpinner.setEnabled(true);
                    toSpinner.setEnabled(true);
                }
            }
        });
        customizeSummaryLengthCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.DESELECTED) {
                    lengthSpinner.setEnabled(false);
                } else if(e.getStateChange() == ItemEvent.SELECTED) {
                    lengthSpinner.setEnabled(true);
                }
            }
        });

        queryField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // start retrieving
            }
        });
    }

    /**
     * Initializes all GUI elements with its default values
     */
    private void initializeGUIValues() {
        SpinnerNumberModel lengthModel = new SpinnerNumberModel(50,10,Integer.MAX_VALUE,1);
        lengthSpinner.setModel(lengthModel);
//        lengthSpinner.setValue(50);

        SpinnerNumberModel fromYearModel = new SpinnerNumberModel(1400, 1400, 2050,1);
        fromYearModel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = (Integer) fromSpinner.getValue();
                if(value > (Integer) toSpinner.getValue()) {
                    toSpinner.setValue(value);
                }
            }
        });
        fromSpinner.setModel(fromYearModel);

        SpinnerNumberModel toYearModel = new SpinnerNumberModel(1400, 1400, 2050,1);
        toYearModel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = (Integer) toSpinner.getValue();
                if (value < (Integer) fromSpinner.getValue()) {
                    fromSpinner.setValue(value);
                }
            }
        });
        toSpinner.setModel(toYearModel);

        lengthSpinner.setEnabled(false);
        fromSpinner.setEnabled(false);
        toSpinner.setEnabled(false);
    }


    /**
     * Make the GUI visible
     */
    public void show() {

        try
        {
//            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
            for(UIManager.LookAndFeelInfo f : lafs) {
                System.out.println(f.getName());
                if(f.getName().equals("GTK+") || f.getName().equals("Windows") || f.getName().contains("Macintosh")) {
                    UIManager.setLookAndFeel(f.getClassName());
                }
            }
        }
        catch(Exception ignored) {}


        JFrame frame = new JFrame("AuTo Summarizer");
        frame.setContentPane(new GUI().rootJpanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(Globals.GUI_WIDTH, Globals.GUI_HEIGHT);
        frame.setResizable(false);
//        frame.pack();
        frame.setVisible(true);

    }

    private JPanel rootJpanel;
    private JTextField exportField;
    private JCheckBox filterResultsCheckBox;
    private JSpinner fromSpinner;
    private JSpinner toSpinner;
    private JButton dataDirBrowse;
    private JButton storeDirBrowse;
    private JButton exportBrowse;
    private JCheckBox customizeSummaryLengthCheckBox;
    private JSpinner lengthSpinner;
    private JLabel statusLabel;
}
