import com.intellij.uiDesigner.core.GridConstraints;
import com.itextpdf.text.DocumentException;
import edu.stanford.nlp.util.StringUtils;
import org.apache.lucene.index.IndexNotFoundException;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.xml.stream.XMLStreamException;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Created by nidhi on 10/5/14.
 */
public class Gui {

    private boolean isIndexing = false;

    Object[][] data=new Object[60][2];
    Object headers[] = { "Sentences", "Required/Not"};

    public Gui() throws IOException, XMLStreamException {

        initializeGUIValues();


        // Action Listeners ----

        dataDirBrowse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(new File("."));
                fileChooser.setDialogTitle("Select data directory");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                final int chooseStatus = fileChooser.showOpenDialog(rootJPanel);
                if (chooseStatus == JFileChooser.APPROVE_OPTION) {
                    dataDirField.setText("");
                    dataDirField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                    index();
                }
            }
        });

        setParameters.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.DESELECTED){
                    setDelta.setEnabled(false);
                    setLambda.setEnabled(false);
                    setThreshold.setEnabled(false);
                }
                else{
                    setThreshold.setEnabled(true);
                    setDelta.setEnabled(true);
                    setLambda.setEnabled(true);
                }
            }
        });

        outputPathBrowse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(new File("."));
                fileChooser.setDialogTitle("Select data directory");
                final int chooseStatus = fileChooser.showOpenDialog(rootJPanel);
                if (chooseStatus == JFileChooser.APPROVE_OPTION) {
                    outputPathFile.setText(fileChooser.getSelectedFile().getAbsolutePath());
                    summarizeButton.setEnabled(true);
                }
            }
        });

        summarizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                summarize();
            }

        });

        idealSummaryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                idealSummarize();
            }
        });


    }

    private void initializeGUIValues() throws IOException, XMLStreamException {

        this.summarizeButton.setEnabled(false);
        this.outputPathBrowse.setEnabled(true);
        this.idealSummaryButton.setEnabled(false);

        SpinnerNumberModel queryNumberModel = new SpinnerNumberModel(126,126,175,1);
        queryNumber.setModel(queryNumberModel);
        try {
            QRelTopicParser queryTopic= new QRelTopicParser(Globals.QREL_TOPIC_FILE,(Integer)queryNumber.getValue());
            queryTitleLabel.setText("The Query selected is " + queryNumber.getValue()  + " and the topic is " + queryTopic.getTitle());
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (XMLStreamException e1) {
            e1.printStackTrace();
        }

        queryNumberModel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = (Integer) queryNumber.getValue();
                try {
                    QRelTopicParser queryTopic= new QRelTopicParser(Globals.QREL_TOPIC_FILE,value);
                    queryTitleLabel.setText("The Query selected is " + value + " and the topic is " + queryTopic.getTitle());
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (XMLStreamException e1) {
                    e1.printStackTrace();
                }
            }
        });

        SpinnerNumberModel lambaModel = new SpinnerNumberModel(0.75,0.1,0.9,0.05 );
        setLambda.setModel(lambaModel);
        setLambda.setEnabled(false);

        lambaModel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                double value = (Double) setLambda.getValue();
                Globals.LAMBDA_FOR_SENTENCE_SCORING = value;
            }
        });

        SpinnerNumberModel deltaModel = new SpinnerNumberModel(0.85,0.1,0.9,0.05 );
        setDelta.setModel(deltaModel);
        setDelta.setEnabled(false);

        deltaModel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                double value = (Double) setDelta.getValue();
                Globals.SEMANTIC_SIMILARITY_WEIGHTAGE = value;
            }
        });

        SpinnerNumberModel thresholdModel = new SpinnerNumberModel(0.2,0.1,0.9,0.05 );
        setThreshold.setModel(thresholdModel);
        setThreshold.setEnabled(false);

        thresholdModel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                double value = (Double) setThreshold.getValue();
                Globals.SIMILARITY_THRESHOLD = value;
            }
        });



        SpinnerNumberModel lengthModel = new SpinnerNumberModel(20,20,60,5);
        lengthSpinner.setModel(lengthModel);

        table1.setDragEnabled(true);
        table1.setModel(new DefaultTableModel(data, headers) {
            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return String.class;
                    case 1:
                        return Boolean.class;
                    default:
                        return super.getColumnClass(column);
                }
            }
        });
        table1.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table1.setDragEnabled(true);
        table1.setFillsViewportHeight(true);
        table1.setAutoCreateRowSorter(true);
        table1.setDropMode(DropMode.INSERT_ROWS);
        table1.setTransferHandler(new TableRowTransferHandler());
        table1.getColumnModel().getColumn(0).setPreferredWidth((int) (Globals.GUI_WIDTH*.90));
        getSettings();
    }

    /**
     * Restore last stored config
     */
    private void getSettings() {
        dataDirField.setText(Settings.getDataDir());
    }


    public void show(){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try
                {
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
                try {
                    frame.setContentPane(new Gui().rootJPanel);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XMLStreamException e) {
                    e.printStackTrace();
                }
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(Globals.GUI_WIDTH, Globals.GUI_HEIGHT);
                frame.setResizable(false);
//        frame.pack();
                frame.setVisible(true);
            }
        });

    }


    private void index() {
        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
            int i = 1;
            boolean growing = true;
            @Override
            protected Void doInBackground() throws Exception {
                statusLabel.setText("Indexing . . .");
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
                if(queryNumber.getValue().toString().isEmpty() || isIndexing || outputPathFile.getText().isEmpty())
                    summarizeButton.setEnabled(false);
                else
                    summarizeButton.setEnabled(true);
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

    private void summarize(){
//        final List<Sentence> sentences = new ArrayList<Sentence>((Integer) lengthSpinner.getValue());
        SwingWorker<Void,Integer> worker= new SwingWorker<Void,Integer>(){

            @Override
            protected Void doInBackground() throws Exception {
                int queryNo = (Integer)queryNumber.getValue();
                QRelTopicParser queryTopic = new QRelTopicParser(Globals.QREL_TOPIC_FILE ,queryNo);
                SearchQuery.setMainQuery(queryTopic.getTitle());
                QRelInput query = new QRelInput(Globals.QREL_PATH);
                statusLabel.setText("Retrieving . . .");
                Retriever r = null;
                try {
                    r = new Retriever(Globals.INDEX_STORE_DIR);
                } catch (IndexNotFoundException e) {
                    System.out.println("No data found!");
                    statusLabel.setText("No data found!");
                    return null;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                List<DocumentClass> docs = null;
                try {
//                    docs = r.topRelevantResults(SearchQuery.getMainQuery(), Globals.RETRIEVAL_RESULT_COUNT);
                    docs = query.getDocsFromQrel(r,queryNo);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }


                statusLabel.setText("Topic Modelling . . .");
                TopicModel modeller = new TopicModel();
                List<Cluster> clusters = null;
                try {
                    clusters = modeller.getClusters(docs, r, Globals.NUM_CLUSTERS);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }


                statusLabel.setText("Getting Top " +  lengthSpinner.getValue() + " sentences . . .");
                List<Sentence> sentences = new ArrayList<Sentence>((Integer) lengthSpinner.getValue());
                for (Cluster c : clusters) {
                    sentences.addAll(c.getTopKSentences(
                            (int) Math.ceil((Integer) lengthSpinner.getValue()/(double) clusters.size()),
                            SearchQuery.getMainQuery()));
                }

                statusLabel.setText("Sorting chronologically . . .");
                Collections.sort(sentences, new Comparator<Sentence>() {
                    @Override
                    public int compare(Sentence s1, Sentence s2) {
                        return s1.getDateWithPlacement().compareTo(s2.getDateWithPlacement());
                    }
                });

                //displaying the sentences in Gui
                for (int i=0;i<(Integer)lengthSpinner.getValue();i++){
                    data[i][0]=sentences.get(i);
                    data[i][1]=false;
                    DefaultTableModel model = (DefaultTableModel)table1.getModel();
                    model.setValueAt(data[i][0],i,0);
                    model.setValueAt(data[i][1],i,1);
                }


                statusLabel.setText("Exporting to file");
                String output = ExportDocument.generateContent(sentences, clusters);
                String debugContent = ExportDocument.generateDebugContent(clusters);
                String exportTo = outputPathFile.getText();

                if(exportTo.endsWith(".pdf")) {
                    try {
                        ExportDocument.toPDF(exportTo, SearchQuery.getMainQuery(), "QRel Query No. : " + queryNo, output);
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
                        ExportDocument.toText(exportTo, SearchQuery.getMainQuery(), "QRel Query No. : " + queryNo, output);
                        int extIndx = exportTo.lastIndexOf(".");
                        exportTo = exportTo.substring(0, (extIndx==-1)?exportTo.length():extIndx) + Globals.DEBUG_FILE_SUFFIX + ".txt";
                        StringUtils.printToFile(exportTo, debugContent);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }

//                query.start((Integer)queryNumber.getValue(),outputPathFile.getText());

                statusLabel.setText("Ready!");
                return null;
            }

//            @Override
//            protected void process(List<String> chunks) {
//                statusLabel.setText(chunks.get(chunks.size() - 1));
//            }

            @Override
            protected void done() {
                // re-enable after thread finishes
                outputPathBrowse.setEnabled(true);
                summarizeButton.setEnabled(true);
                lengthSpinner.setEnabled(true);
                dataDirBrowse.setEnabled(true);
                idealSummaryButton.setEnabled(true);

                PostRunner.run();

                System.out.println("Done!");
            }
        };
        // these paramaters shouldn't be allowed to change
        // while execution is running

        idealSummaryButton.setEnabled(false);
        lengthSpinner.setEnabled(false);
        outputPathBrowse.setEnabled(false);
        dataDirBrowse.setEnabled(false);
        summarizeButton.setEnabled(false);

        worker.execute();
        return ;
    }

    private void idealSummarize(){
        SwingWorker<Void,Integer> worker= new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {

                int queryNo = (Integer)queryNumber.getValue();
                QRelTopicParser queryTopic = new QRelTopicParser(Globals.QREL_TOPIC_FILE ,queryNo);
                SearchQuery.setMainQuery(queryTopic.getTitle());
                QRelInput query = new QRelInput(Globals.QREL_PATH);

                List<String> idealSentences = new ArrayList<String>();

                for(int i=0;i<(Integer)lengthSpinner.getValue();i++){
                    if((Boolean)table1.getModel().getValueAt(i,1)==true) {
                        idealSentences.add(table1.getModel().getValueAt(i, 0).toString());
//                        System.out.println(i + "." + idealSentences.get(i));
                    }

                }

                String idealExportTo = outputPathFile.getText();

                String idealOutput = ExportDocument.generateIdealContent(idealSentences);
                int extIndx = idealExportTo.lastIndexOf(".");
                idealExportTo = idealExportTo.substring(0, (extIndx==-1)?idealExportTo.length():extIndx) +"_ideal" + ".txt";

                if(idealExportTo.endsWith(".pdf")) {
                    try {
                        ExportDocument.toPDF(idealExportTo, SearchQuery.getMainQuery(), "QRel Query No. : " + queryNo, idealOutput);
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (DocumentException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    try {
                        ExportDocument.toText(idealExportTo, SearchQuery.getMainQuery(), "QRel Query No. : " + queryNo,idealOutput);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }


                return null;
            }

            @Override
            protected void done() {
                // re-enable after thread finishes
                outputPathBrowse.setEnabled(true);
                summarizeButton.setEnabled(true);
                lengthSpinner.setEnabled(true);
                dataDirBrowse.setEnabled(true);
                idealSummaryButton.setEnabled(true);

                PostRunner.run();

                System.out.println("Done!");
            }
        };


        idealSummaryButton.setEnabled(false);
        lengthSpinner.setEnabled(false);
        outputPathBrowse.setEnabled(false);
        dataDirBrowse.setEnabled(false);
        summarizeButton.setEnabled(false);

        worker.execute();

    }

    class TableRowTransferHandler extends TransferHandler {

        private int[] rows    = null;
        private int addIndex  = -1;
        private int addCount  = 0;
        private final DataFlavor localObjectFlavor;
        private JComponent source = null;

        public TableRowTransferHandler() {
            super();
            localObjectFlavor = new ActivationDataFlavor(Object[].class, DataFlavor.javaJVMLocalObjectMimeType, "Array of items");
        }
        @Override protected Transferable createTransferable(JComponent c) {
            source = c;
            JTable table = (JTable) c;
            DefaultTableModel model = (DefaultTableModel)table.getModel();
            ArrayList<Object> list = new ArrayList<Object>();
            rows = table.getSelectedRows();
            for(int i: rows) {
                list.add(model.getDataVector().elementAt(i));
            }
            Object[] transferedObjects = list.toArray();
            return new DataHandler(transferedObjects, localObjectFlavor.getMimeType());
        }
        @Override public boolean canImport(TransferSupport info) {
            JTable t = (JTable)info.getComponent();
            boolean b = info.isDrop() && info.isDataFlavorSupported(localObjectFlavor);
            //XXX bug?
            t.setCursor(b?DragSource.DefaultMoveDrop:DragSource.DefaultMoveNoDrop);
            return b;
        }
        @Override public int getSourceActions(JComponent c) {
            return COPY_OR_MOVE;
        }
        @Override public boolean importData(TransferSupport info) {
            JTable target = (JTable)info.getComponent();
            JTable.DropLocation dl  = (JTable.DropLocation)info.getDropLocation();
            DefaultTableModel model = (DefaultTableModel)target.getModel();
            int index = dl.getRow();
            int max = model.getRowCount();
            if(index<0 || index>max) {
                index = max;
            }
            addIndex = index;
            target.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            try{
                Object[] values = (Object[])info.getTransferable().getTransferData(localObjectFlavor);
                if(source==target) { addCount = values.length; }
                for(int i=0;i<values.length;i++) {
                    int idx = index++;
                    model.insertRow(idx, (Vector)values[i]);
                    target.getSelectionModel().addSelectionInterval(idx, idx);
                }
                return true;
            }catch(UnsupportedFlavorException ex) {
                ex.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
        @Override protected void exportDone(JComponent c, Transferable t, int act) {
            cleanup(c, act == MOVE);
        }
        private void cleanup(JComponent src, boolean remove) {
            if(remove && rows != null) {
                JTable table = (JTable)src;
                src.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                DefaultTableModel model = (DefaultTableModel)table.getModel();
                if(addCount > 0) {
                    for(int i=0;i<rows.length;i++) {
                        if(rows[i]>=addIndex) {
                            rows[i] += addCount;
                        }
                    }
                }
                for(int i=rows.length-1;i>=0;i--) {
                    model.removeRow(rows[i]);
                }
            }
            rows     = null;
            addCount = 0;
            addIndex = -1;
        }
    }

    private JPanel rootJPanel;
    private JTextField dataDirField;
    private JButton dataDirBrowse;
    private JLabel statusLabel;
    private JSpinner queryNumber;
    private JLabel queryTitleLabel;
    private JTextField outputPathFile;
    private JButton outputPathBrowse;
    private JButton summarizeButton;
    private JSpinner lengthSpinner;
    private JCheckBox setParameters;
    private JSpinner setLambda;
    private JSpinner setDelta;
    private JSpinner setThreshold;
    private JTable table1;
    private JPanel sentencePanel;
    private JScrollPane tableScrollPane;
    private JButton idealSummaryButton;

}