[1mdiff --git a/src/Cluster.java b/src/Cluster.java[m
[1mindex 2edf806..13a9559 100644[m
[1m--- a/src/Cluster.java[m
[1m+++ b/src/Cluster.java[m
[36m@@ -1,7 +1,4 @@[m
[31m-import java.util.ArrayList;[m
[31m-import java.util.Collections;[m
[31m-import java.util.Comparator;[m
[31m-import java.util.List;[m
[32m+[m[32mimport java.util.*;[m
 [m
 public class Cluster {[m
 [m
[36m@@ -24,21 +21,81 @@[m [mpublic class Cluster {[m
         this.docs = docs;[m
         sentences = new ArrayList<Sentence>();[m
 [m
[31m-        StringBuilder sb = new StringBuilder();[m
[32m+[m[32m        keepOnlyImpDocs();[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m[32m    public Cluster(int indx, String title, List<Document> docs, int impNum) {[m
[32m+[m[32m        this.clusterID = indx;[m
[32m+[m[32m        this.title = title;[m
[32m+[m[32m        this.docs = docs;[m
[32m+[m[32m        sentences = new ArrayList<Sentence>();[m
[32m+[m
[32m+[m[32m        keepOnlyImpDocs(impNum);[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m[32m    public void keepOnlyImpDocs(int impNum) {[m
[32m+[m[32m        if(title == null)[m
[32m+[m[32m            throw new NullPointerException("Title is empty");[m
[32m+[m[32m        List<Document> impDocs = new ArrayList<Document>();[m
[32m+[m[32m        for(Document d : docs) {[m
[32m+[m[32m            double scre = Similarity.titleToDocument(title, d);[m
[32m+[m[32m            d.score = scre;[m
[32m+[m[32m            impDocs.add(d);[m
[32m+[m[32m        }[m
[32m+[m
[32m+[m[32m        Collections.sort(impDocs, new Comparator<Document>() {[m
[32m+[m[32m            @Override[m
[32m+[m[32m            public int compare(Document d1, Document d2) {[m
[32m+[m[32m                return Double.compare(d2.score, d1.score);[m
[32m+[m[32m            }[m
[32m+[m[32m        });[m
[32m+[m
[32m+[m[32m        docs = impDocs.subList(0, impNum);[m
[32m+[m[32m        sentences.clear();[m
[32m+[m
         for(Document d : docs) {[m
             String[] sents = d.getContent().split("\\.");[m
             for(String s : sents) {[m
[31m-                sentences.add(new Sentence(d.getClusterID(), d.getFilename(), d.getDate(), s));[m
[32m+[m[32m                s = s.trim();[m
[32m+[m[32m                if(s.split(" ").length > 2)[m
[32m+[m[32m                    sentences.add(new Sentence(d.getClusterID(), d.getFilename(), d.getDate(), s));[m
[32m+[m[32m            }[m
[32m+[m[32m        }[m
[32m+[m[32m    }[m
[32m+[m
[32m+[m[32m    public void keepOnlyImpDocs() {[m
[32m+[m[32m        if(title == null)[m
[32m+[m[32m            throw new NullPointerException("Title is empty");[m
[32m+[m[32m        List<Document> impDocs = new ArrayList<Document>();[m
[32m+[m[32m        for(Document d : docs) {[m
[32m+[m[32m            double scre = Similarity.titleToDocument(title, d);[m
[32m+[m[32m            d.score = scre;[m
[32m+[m[32m            impDocs.add(d);[m
[32m+[m[32m        }[m
[32m+[m
[32m+[m[32m        Collections.sort(impDocs, new Comparator<Document>() {[m
[32m+[m[32m            @Override[m
[32m+[m[32m            public int compare(Document d1, Document d2) {[m
[32m+[m[32m                return Double.compare(d2.score, d1.score);[m
[32m+[m[32m            }[m
[32m+[m[32m        });[m
[32m+[m
[32m+[m[32m        docs = impDocs.subList(0, Globals.CENTROID_DOCS_IN_CLUSTER);[m
[32m+[m[32m        sentences.clear();[m
[32m+[m
[32m+[m[32m        for(Document d : docs) {[m
[32m+[m[32m            String[] sents = d.getContent().split("\\.");[m
[32m+[m[32m            for(String s : sents) {[m
[32m+[m[32m                s = s.trim();[m
[32m+[m[32m                if(s.split(" ").length > 2)[m
[32m+[m[32m                    sentences.add(new Sentence(d.getClusterID(), d.getFilename(), d.getDate(), s));[m
             }[m
         }[m
         System.out.println("\n");[m
     }[m
 [m
[32m+[m[32m    // need to call keep only imp doc after this[m
     public void addDocument(Document doc) {[m
[31m-        String[] sents = doc.getContent().split("\\.");[m
[31m-        for(String s : sents) {[m
[31m-            sentences.add(new Sentence(doc.getClusterID(), doc.getFilename(), doc.getDate(), s));[m
[31m-        }[m
         this.docs.add(doc);[m
     }[m
 [m
[36m@@ -54,6 +111,10 @@[m [mpublic class Cluster {[m
         return title;[m
     }[m
 [m
[32m+[m[32m    public void setTitle(String title) {[m
[32m+[m[32m        this.title = title;[m
[32m+[m[32m    }[m
[32m+[m
     public List<Sentence> getSentences() {[m
         return sentences;[m
     }[m
[36m@@ -100,7 +161,9 @@[m [mpublic class Cluster {[m
 [m
     public List<Sentence> getTopKSentences(int K) {[m
         for(int i = 0; i < this.sentences.size(); ++i) {[m
[31m-            double score = getSentenceScore(i, sentences.get(i), sentences, Globals.LAMBDA_FOR_SENTENCE_SCORING);[m
[32m+[m[32m            System.out.println("sent score : " + i);[m
[32m+[m[32m            double score;[m
[32m+[m[32m            score = getSentenceScore(i, sentences.get(i), sentences, Globals.LAMBDA_FOR_SENTENCE_SCORING);[m
             sentences.get(i).setScore(score);[m
         }[m
 [m
[1mdiff --git a/src/Document.java b/src/Document.java[m
[1mindex d897c2b..90016cf 100644[m
[1m--- a/src/Document.java[m
[1m+++ b/src/Document.java[m
[36m@@ -4,6 +4,7 @@[m [mpublic class Document {[m
     private String filename = null;[m
     private String content = null;[m
     private String date = null;[m
[32m+[m[32m    public double score = 0.0;[m
 [m
     public Document(int clusterID, String filename, String content) {[m
         this.clusterID = clusterID;[m
[1mdiff --git a/src/GUI.java b/src/GUI.java[m
[1mindex 1175b06..3f10efa 100644[m
[1m--- a/src/GUI.java[m
[1m+++ b/src/GUI.java[m
[36m@@ -1,3 +1,6 @@[m
[32m+[m[32mimport com.itextpdf.text.DocumentException;[m
[32m+[m[32mimport org.apache.lucene.queryparser.classic.ParseException;[m
[32m+[m
 import javax.swing.*;[m
 import javax.swing.event.ChangeEvent;[m
 import javax.swing.event.ChangeListener;[m
[36m@@ -8,6 +11,8 @@[m [mimport java.awt.event.ActionListener;[m
 import java.awt.event.ItemEvent;[m
 import java.awt.event.ItemListener;[m
 import java.io.File;[m
[32m+[m[32mimport java.io.FileNotFoundException;[m
[32m+[m[32mimport java.io.IOException;[m
 import java.util.ArrayList;[m
 import java.util.Collections;[m
 import java.util.Comparator;[m
[36m@@ -154,14 +159,33 @@[m [mpublic class GUI {[m
                 // start retrieving[m
                 SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {[m
                     @Override[m
[31m-                    protected Void doInBackground() throws Exception {[m
[32m+[m[32m                    protected Void doInBackground() {[m
                         publish("Retrieving . . .");[m
[31m-                        Retriever r = new Retriever(storeDirField.getText());[m
[31m-                        List<XmlDocument> docs = r.topRelevantResults(queryField.getText(), Globals.RETRIEVAL_RESULT_COUNT);[m
[32m+[m[32m                        Retriever r = null;[m
[32m+[m[32m                        try {[m
[32m+[m[32m                            r = new Retriever(storeDirField.getText());[m
[32m+[m[32m                        } catch (IOException e1) {[m
[32m+[m[32m                            e1.printStackTrace();[m
[32m+[m[32m                        } catch (ParseException e1) {[m
[32m+[m[32m                            e1.printStackTrace();[m
[32m+[m[32m                        }[m
[32m+[m[32m                        List<XmlDocument> docs = null;[m
[32m+[m[32m                        try {[m
[32m+[m[32m                            docs = r.topRelevantResults(queryField.getText(), Globals.RETRIEVAL_RESULT_COUNT);[m
[32m+[m[32m                        } catch (ParseException e1) {[m
[32m+[m[32m                            e1.printStackTrace();[m
[32m+[m[32m                        } catch (IOException e1) {[m
[32m+[m[32m                            e1.printStackTrace();[m
[32m+[m[32m                        }[m
 [m
                         publish("Topic Modelling . . .");[m
                         TopicModel modeller = new TopicModel();[m
[31m-                        List<Cluster> clusters = modeller.getClusters(docs, Globals.NUM_CLUSTERS);[m
[32m+[m[32m                        List<Cluster> clusters = null;[m
[32m+[m[32m                        try {[m
[32m+[m[32m                            clusters = modeller.getClusters(docs, Globals.NUM_CLUSTERS);[m
[32m+[m[32m                        } catch (Exception e1) {[m
[32m+[m[32m                            e1.printStackTrace();[m
[32m+[m[32m                        }[m
 [m
                         publish("Getting Top " + ((Integer)lengthSpinner.getValue()).toString() + " sentences . . .");[m
                         List<Sentence> sentences = new ArrayList<Sentence>((Integer) lengthSpinner.getValue());[m
[36m@@ -174,16 +198,26 @@[m [mpublic class GUI {[m
                         Collections.sort(sentences, new Comparator<Sentence>() {[m
                             @Override[m
                             public int compare(Sentence s1, Sentence s2) {[m
[31m-                                return s2.getDate().compareTo(s1.getDate());[m
[32m+[m[32m                                return s1.getDate().compareTo(s2.getDate());[m
                             }[m
                         });[m
 [m
                         publish("Exporting to file");[m
                         String output = ExportDocument.generateContent(sentences);[m
                         if(exportField.getText().endsWith(".txt")) {[m
[31m-                            ExportDocument.toText(exportField.getText(), queryField.getText(), output);[m
[32m+[m[32m                            try {[m
[32m+[m[32m                                ExportDocument.toText(exportField.getText(), queryField.getText(), output);[m
[32m+[m[32m                            } catch (IOException e1) {[m
[32m+[m[32m                                e1.printStackTrace();[m
[32m+[m[32m                            }[m
                         } else if(exportField.getText().endsWith(".pdf")) {[m
[31m-                            ExportDocument.toPDF(exportField.getText(), queryField.getText(), output);[m
[32m+[m[32m                            try {[m
[32m+[m[32m                                ExportDocument.toPDF(exportField.getText(), queryField.getText(), output);[m
[32m+[m[32m                            } catch (FileNotFoundException e1) {[m
[32m+[m[32m                                e1.printStackTrace();[m
[32m+[m[32m                            } catch (DocumentException e1) {[m
[32m+[m[32m                                e1.printStackTrace();[m
[32m+[m[32m                            }[m
                         }[m
 [m
                         publish("Ready!");[m
[36m@@ -205,6 +239,7 @@[m [mpublic class GUI {[m
                         fromSpinner.setEnabled(true);[m
                         toSpinner.setEnabled(true);[m
                         lengthSpinner.setEnabled(true);[m
[32m+[m[32m                        System.out.println("Done!");[m
                     }[m
                 };[m
 [m
[36m@@ -219,6 +254,7 @@[m [mpublic class GUI {[m
                 lengthSpinner.setEnabled(false);[m
 [m
                 worker.execute();[m
[32m+[m
             }[m
         });[m
 [m
[1mdiff --git a/src/Globals.java b/src/Globals.java[m
[1mindex 3c7b164..4ecc96c 100644[m
[1m--- a/src/Globals.java[m
[1m+++ b/src/Globals.java[m
[36m@@ -26,4 +26,6 @@[m [mpublic class Globals {[m
     public static final int NUM_CLUSTERS = 5;[m
 [m
     public static final String PREFERENCES_NODE = "AutoSummarySEN";[m
[32m+[m
[32m+[m[32m    public static final int CENTROID_DOCS_IN_CLUSTER = 3;[m
 }[m
[1mdiff --git a/src/Main.java b/src/Main.java[m
[1mindex 2791cc1..fbe6e14 100644[m
[1m--- a/src/Main.java[m
[1m+++ b/src/Main.java[m
[36m@@ -1,6 +1,21 @@[m
[32m+[m[32mimport java.io.IOException;[m
[32m+[m
 public class Main {[m
[31m-    public static void main(String args[]) {[m
[32m+[m[32m    public static void main(String args[]) throws IOException {[m
[32m+[m
[32m+[m[32m//        System.setProperty("wordnet.database.dir", System.getenv("WNHOME")+"/");[m
[32m+[m
[32m+[m[32m        //construct URL to WordNet Dictionary directory on the computer[m
[32m+[m[32m//        String wordNetDirectory = System.getenv("WNHOME");[m
[32m+[m[32m//        JWS	ws = new JWS(wordNetDirectory,"3.0");[m
[32m+[m[32m//        Lin lin = ws.getLin();[m
[32m+[m
[32m+[m[32m//        WordNetDatabase database = WordNetDatabase.getFileInstance();[m
[32m+[m[32m//        System.out.println("loaded");[m
         GUI gui = new GUI();[m
         gui.show();[m
[32m+[m
     }[m
[31m-}[m
[32m+[m
[32m+[m
[32m+[m[32m}[m
\ No newline at end of file[m
[1mdiff --git a/src/Sentence.java b/src/Sentence.java[m
[1mindex 21c6c3a..ee6f236 100644[m
[1m--- a/src/Sentence.java[m
[1m+++ b/src/Sentence.java[m
[36m@@ -1,4 +1,8 @@[m
[31m-import java.util.Random;[m
[32m+[m[32mimport javafx.util.Pair;[m
[32m+[m
[32m+[m[32mimport java.util.ArrayList;[m
[32m+[m[32mimport java.util.Arrays;[m
[32m+[m[32mimport java.util.List;[m
 [m
 public class Sentence {[m
 [m
[36m@@ -7,12 +11,23 @@[m [mpublic class Sentence {[m
     private String date = null;[m
     private double score = -1.0;[m
     private String content = null;[m
[32m+[m[32m    private List<String> tokens = null;[m
 [m
     public Sentence(int id, String filename, String date, String content) {[m
         clusterID = id;[m
         this.filename = filename;[m
         this.date = date;[m
         this.content = content;[m
[32m+[m
[32m+[m[32m        String[] toks = null;[m
[32m+[m[32m        if(content != null)[m
[32m+[m[32m            toks = content.split(" ");[m
[32m+[m[32m        if(toks != null) {[m
[32m+[m[32m            tokens = Arrays.asList(toks);[m
[32m+[m[32m            tokens = LuceneUtils.stemWords(tokens, LuceneUtils.Stemmer.Morpho);[m
[32m+[m[32m        } else {[m
[32m+[m[32m            tokens = new ArrayList<String>();[m
[32m+[m[32m        }[m
     }[m
 [m
     @Override[m
[36m@@ -40,10 +55,48 @@[m [mpublic class Sentence {[m
         return clusterID;[m
     }[m
 [m
[32m+[m[32m    public List<String> getTokens() {[m
[32m+[m[32m        return tokens;[m
[32m+[m[32m    }[m
 [m
     // FIXME : the trouble maker :P[m
     public double getSimilarity(Sentence s) {[m
[31m-        Random rand = new Random(System.currentTimeMillis());[m
[31m-        return rand.nextDouble();[m
[32m+[m[32m//        double score = (new Similarity()).getSimilarity(this.getTokens(), s.getTokens());[m
[32m+[m[32m//        return score;[m
[32m+[m[32m//        Random rand = new Random(System.currentTimeMillis());[m
[32m+[m[32m//        return rand.nextDouble();[m
[32m+[m
[32m+[m[32m//        if(Similarity.cache.containsKey(new Pair<String, String>(s.toString(), this.toString()))) {[m
[32m+[m[32m//            System.out.println("hit");[m
[32m+[m[32m//            return Similarity.cache.get(new Pair<String, String>(s.toString(), this.toString()));[m
[32m+[m[32m//        }[m
[32m+[m[32m//[m
[32m+[m[32m//        System.out.println("Miss");[m
[32m+[m
[32m+[m[32m        double score = 0;[m
[32m+[m[32m        for(String w1 : s.getTokens()) {[m
[32m+[m[32m            for(String w2 : this.getTokens()) {[m
[32m+[m[32m                if(w1.equals(w2))[m
[32m+[m[32m                    score += 1;[m
[32m+[m[32m                else {[m
[32m+[m[32m                    Double sTmp = Similarity.cache.get(new Pair<String, String>(w1, w2));[m
[32m+[m[32m                    if(sTmp == null) {[m
[32m+[m[32m                        sTmp = Similarity.lin.calcRelatednessOfWords(w1, w2);[m
[32m+[m[32m                        score += sTmp;[m
[32m+[m[32m                        Similarity.cache.put(new Pair<String, String>(w1, w2), sTmp);[m
[32m+[m[32m                        Similarity.cache.put(new Pair<String, String>(w2, w1), sTmp);[m
[32m+[m[32m//                        System.out.println("Miss");[m
[32m+[m[32m                    } else {[m
[32m+[m[32m                        score += sTmp;[m
[32m+[m[32m//                        System.out.println("Hit");[m
[32m+[m[32m                    }[m
[32m+[m[32m                }[m
[32m+[m[32m            }[m
[32m+[m[32m        }[m
[32m+[m
[32m+[m[32m        score /= (s.getTokens().size()*this.getTokens().size());[m
[32m+[m[32m//        Similarity.cache.put(new Pair<String, String>(s.toString(), this.toString()), score);[m
[32m+[m[32m//        Similarity.cache.put(new Pair<String, String>(this.toString(), s.toString()), score);[m
[32m+[m[32m        return score;[m
     }[m
 }[m
[1mdiff --git a/src/TopicModel.java b/src/TopicModel.java[m
[1mindex 24bf56b..2b2b867 100644[m
[1m--- a/src/TopicModel.java[m
[1m+++ b/src/TopicModel.java[m
[36m@@ -34,7 +34,7 @@[m [mpublic class TopicModel {[m
 [m
         // FIXME : Run the model for 50 iterations and stop (this is for testing only,[m
         //  for real applications, use 1000 to 2000 iterations)[m
[31m-        model.setNumIterations(50);[m
[32m+[m[32m        model.setNumIterations(2000);[m
         model.estimate();[m
 [m
 //        File file = new File("topics.txt");[m
[36m@@ -86,6 +86,10 @@[m [mpublic class TopicModel {[m
         }[m
 [m
         topicTitles = getTopics(model, Globals.TOPIC_TITLE_WORD_COUNT);[m
[32m+[m[32m        for(int i = 0; i < clusters.size(); ++i) {[m
[32m+[m[32m            clusters.get(i).setTitle(topicTitles.get(i));[m
[32m+[m[32m            clusters.get(i).keepOnlyImpDocs();[m
[32m+[m[32m        }[m
 [m
         return clusters;[m
     }[m
