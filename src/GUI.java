import com.itextpdf.text.DocumentException;
import edu.stanford.nlp.util.StringUtils;
import org.apache.lucene.index.IndexNotFoundException;
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
import java.util.*;

/**
 * Class for Graphical User Interface
 * and it's handlers
 */
public class GUI {
    private JTextField dataDirField;
    private JTextField queryField;

    private boolean isIndexing = false;

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
                    dataDirField.setText("");
                    dataDirField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                    index();
                }
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

        summarizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                summarize();
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
                if(queryField.getText().isEmpty() || exportField.getText().isEmpty() || isIndexing)
                    summarizeButton.setEnabled(false);
                else
                    summarizeButton.setEnabled(true);
            }
        };

        queryField.getDocument().addDocumentListener(documentListener);
        exportField.getDocument().addDocumentListener(documentListener);
    }

    /**
     * Initializes all GUI elements with its default values
     */
    private void initializeGUIValues() {

        this.summarizeButton.setEnabled(false);

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

        getSettings();
    }

    /**
     * Restore last stored config
     */
    private void getSettings() {
        dataDirField.setText(Settings.getDataDir());
        exportField.setText(Settings.getExportFile());
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


    /**
     * Creates a new instance of Indxer using SwingWorker
     */
    private void index() {
        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
            int i = 1;
            boolean growing = true;
            @Override
            protected Void doInBackground() throws Exception {
                Indxer indxer = new Indxer(Globals.INDEX_STORE_DIR);
                indxer.indxDir(dataDirField.getText());
                indxer.killWriter();
                return null;
            }

            @Override
            protected void process(List<Integer> chunks) {
                String gen = "indexing ";
                for(int j = 0; j < i; ++j)
                    gen += ". ";
                if(growing) i++;
                else i--;
                if(i == 4) {
                    i = 3;
                    growing = !growing;
                } else if(i == 0) {
                    i = 1;
                    growing = !growing;
                }
                statusLabel.setText(gen);
            }

            @Override
            protected void done() {
                dataDirField.setEnabled(true);
                summarizeButton.setEnabled(false);
                isIndexing = false;
                statusLabel.setText("done indexing!");
                System.out.println("done Indexing!");
            }
        };

        dataDirField.setEnabled(false);
        summarizeButton.setEnabled(false);
        isIndexing = true;

        // store data dir
        Settings.setDataDir(dataDirField.getText());

        // start indexing
        worker.execute();
    }


    private void summarize() {
        // start retrieving
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() {
                SearchQuery.setMainQuery(queryField.getText());

                if(bingSearchCheckBox.isSelected()) {
                    publish("Searching Bing . . .");
                    List<String> bingResults = BingResults.getResults(SearchQuery.getMainQuery(), BingResults.Results.TOP_10);

                    publish("Reading Bing Results . . .");
                    try {
                        BingResults.mergeResultsWithIndex(bingResults);
                    } catch (IOException e) { e.printStackTrace(); }
                }

                publish("Retrieving . . .");
                Retriever r = null;
                try {
                    r = new Retriever(Globals.INDEX_STORE_DIR);
                } catch (IndexNotFoundException e) {
                    System.out.println("No data found!");
                    publish("No data found!");
                    return null;
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
                List<DocumentClass> docs = null;
                try {
                    docs = r.topRelevantResults(SearchQuery.getMainQuery(), Globals.RETRIEVAL_RESULT_COUNT);
                } catch (ParseException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                // Date filter
                if(filterResultsCheckBox.isSelected()) {
                    for(Iterator<DocumentClass> it = docs.iterator(); it.hasNext();) {
                        DocumentClass d = it.next();
                        String year = IRUtils.yearFromDate(d.getDate());
                        int dt = Integer.parseInt(year);
                        int from = (Integer) fromSpinner.getValue();
                        int to = (Integer) toSpinner.getValue();
                        if( dt < from || dt > to)
                            it.remove();
                    }
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
                            SearchQuery.getMainQuery()));
                }

                // remove redundancy
                sentences = new ArrayList<Sentence>(new HashSet<Sentence>(sentences));

                publish("Sorting chronologically . . .");
                Collections.sort(sentences, new Comparator<Sentence>() {
                    @Override
                    public int compare(Sentence s1, Sentence s2) {
                        return s1.getDate().compareTo(s2.getDate());
                    }
                });

                publish("Exporting to file");
                String output = ExportDocument.generateContent(sentences, clusters);
                String debugContent = ExportDocument.generateDebugContent(clusters);
                String exportTo = exportField.getText();
                if(exportTo.endsWith(".pdf")) {
                    try {
                        ExportDocument.toPDF(exportTo, "", SearchQuery.getMainQuery(), output);
                        int extIndx = exportTo.lastIndexOf(".");
                        exportTo = exportTo.substring(0,(extIndx==-1)?exportTo.length():extIndx) + Globals.DEBUG_FILE_SUFFIX + ".pdf";
                        ExportDocument.printToPDF(exportTo, debugContent);
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (DocumentException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    try {
                        ExportDocument.toText(exportTo, "", SearchQuery.getMainQuery(), output);
                        int extIndx = exportTo.lastIndexOf(".");
                        exportTo = exportTo.substring(0, (extIndx==-1)?exportTo.length():extIndx) + Globals.DEBUG_FILE_SUFFIX + ".txt";
                        StringUtils.printToFile(exportTo, debugContent);
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
                if(filterResultsCheckBox.isSelected()) {
                    fromSpinner.setEnabled(true);
                    toSpinner.setEnabled(true);
                }
                if(customizeSummaryLengthCheckBox.isSelected())
                    lengthSpinner.setEnabled(true);
                exportBrowse.setEnabled(true);
                dataDirBrowse.setEnabled(true);
                summarizeButton.setEnabled(true);

                PostRunner.run();

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
        exportBrowse.setEnabled(false);
        dataDirBrowse.setEnabled(false);
        summarizeButton.setEnabled(false);

        worker.execute();

    }

    private JPanel rootJpanel;
    private JTextField exportField;
    private JCheckBox filterResultsCheckBox;
    private JSpinner fromSpinner;
    private JSpinner toSpinner;
    private JButton dataDirBrowse;
    private JButton exportBrowse;
    private JCheckBox customizeSummaryLengthCheckBox;
    private JSpinner lengthSpinner;
    private JLabel statusLabel;
    private JCheckBox bingSearchCheckBox;
    private JButton summarizeButton;
}
