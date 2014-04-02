import com.itextpdf.text.DocumentException;
import org.apache.lucene.queryparser.classic.ParseException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
                SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        Indxer indxer = new Indxer(storeDirField.getText());
                        final File docDir = new File(dataDirField.getText());
                        File[] files = docDir.listFiles();
                        if(files == null) {
                            System.out.println("[ dataDir ] is empty!");
                            return null;
                        }

                        for (int i = 0; i < files.length; i++) {
                            File f = files[i];
                            publish((int) Math.ceil((i * 100) / (double) files.length));
                            if (f.isDirectory()) {
                                indxer.indxDir(f.getAbsolutePath());
                            } else {
                                indxer.indxFile(f);
                            }
                        }
                        return null;
                    }

                    @Override
                    protected void process(List<Integer> chunks) {
                        updateProgressBar(chunks.get(chunks.size()-1));
                    }

                    @Override
                    protected void done() {
                        startIndexingButton.setEnabled(true);
                        storeDirField.setEnabled(true);
                        dataDirField.setEnabled(true);
                    }
                };

                startIndexingButton.setEnabled(false);
                storeDirField.setEnabled(false);
                dataDirField.setEnabled(false);

                // store index path
                Settings.setStoreDir(storeDirField.getText());

                // start indexing
                worker.execute();
            }
        });


        exportBrowse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(new File("."));
                fileChooser.setDialogTitle("Select data directory");
//                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                final int chooseStatus = fileChooser.showSaveDialog(rootJpanel);
                if (chooseStatus == JFileChooser.APPROVE_OPTION) {
                    exportField.setText(fileChooser.getSelectedFile().getAbsolutePath());
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
                SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
                    @Override
                    protected Void doInBackground() {
                        publish("Retrieving . . .");
                        Retriever r = null;
                        try {
                            r = new Retriever(storeDirField.getText());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                        List<XmlDocument> docs = null;
                        String queryTitle = queryField.getText();
                        try {
                            docs = r.topRelevantResults(queryTitle, Globals.RETRIEVAL_RESULT_COUNT);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                        publish("Topic Modelling . . .");
                        TopicModel modeller = new TopicModel();
                        List<Cluster> clusters = null;
                        try {
                            clusters = modeller.getClusters(docs, r, Globals.NUM_CLUSTERS);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }


                        publish("Getting Top " + ((Integer)lengthSpinner.getValue()).toString() + " sentences . . .");
                        List<Sentence> sentences = new ArrayList<Sentence>((Integer) lengthSpinner.getValue());
                        for (Cluster c : clusters) {
                            sentences.addAll(c.getTopKSentences(
                                    (int) Math.ceil((Integer) lengthSpinner.getValue()/(double) clusters.size()),
                                    queryTitle));
                        }

                        publish("Sorting chronologically . . .");
                        Collections.sort(sentences, new Comparator<Sentence>() {
                            @Override
                            public int compare(Sentence s1, Sentence s2) {
                                return s1.getDate().compareTo(s2.getDate());
                            }
                        });

                        publish("Exporting to file");
                        String output = ExportDocument.generateContent(sentences, clusters);
                        if(exportField.getText().endsWith(".txt")) {
                            try {
                                ExportDocument.toText(exportField.getText(), "", queryTitle, output);
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else if(exportField.getText().endsWith(".pdf")) {
                            try {
                                ExportDocument.toPDF(exportField.getText(), "", queryTitle, output);
                            } catch (FileNotFoundException e1) {
                                e1.printStackTrace();
                            } catch (DocumentException e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            try {
                                ExportDocument.toText(exportField.getText(), "", queryTitle, output);
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }

                        publish("Ready!");
                        return null;
                    }

                    @Override
                    protected void process(List<String> chunks) {
                        statusLabel.setText(chunks.get(chunks.size() - 1));
                    }

                    @Override
                    protected void done() {
                        // re-enable after thread finishes
                        queryField.setEnabled(true);
                        exportField.setEnabled(true);
                        filterResultsCheckBox.setEnabled(true);
                        customizeSummaryLengthCheckBox.setEnabled(true);
                        fromSpinner.setEnabled(true);
                        toSpinner.setEnabled(true);
                        lengthSpinner.setEnabled(true);
                        System.out.println("Done!");
                    }
                };

                // these paramaters shouldn't be allowed to change
                // while execution is running
                queryField.setEnabled(false);
                exportField.setEnabled(false);
                filterResultsCheckBox.setEnabled(false);
                customizeSummaryLengthCheckBox.setEnabled(false);
                fromSpinner.setEnabled(false);
                toSpinner.setEnabled(false);
                lengthSpinner.setEnabled(false);

                worker.execute();

            }
        });

        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                check();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                check();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                check();
            }

            public void check() {
                if (!dataDirField.getText().isEmpty() && !storeDirField.getText().isEmpty()) {
                    startIndexingButton.setEnabled(true);
                    progressBar1.setEnabled(true);
                } else {
                    startIndexingButton.setEnabled(false);
                    progressBar1.setEnabled(false);
                }
            }
        };

        dataDirField.getDocument().addDocumentListener(documentListener);
        storeDirField.getDocument().addDocumentListener(documentListener);
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
        startIndexingButton.setEnabled(false);

        progressBar1.setEnabled(false);

        getSettings();
    }

    /**
     * Restore last stored config
     */
    private void getSettings() {
        storeDirField.setText(Settings.getStoreDir());
        exportField.setText(Settings.getExportFile());
    }

    /**
     * Updates the value of progressBar
     * used to show indexing progress
     * @param value % of completion
     */
    public void updateProgressBar(int value) {
        progressBar1.setValue(value);
        progressBar1.setStringPainted(true);
    }


    /**
     * Make the GUI visible
     */
    public void show() {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
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
        });
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
