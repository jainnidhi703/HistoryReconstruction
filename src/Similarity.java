import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.Lin;
import javafx.util.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class Similarity {

    private static final ILexicalDatabase db = new NictWordNet();
    public static final RelatednessCalculator lin = new Lin(db);

    public static TreeMap<Pair<String, String>, Double> cache = new TreeMap<Pair<String, String>, Double>(new Comparator<Pair<String, String>>() {
        @Override
        public int compare(Pair<String , String> a, Pair<String, String> b) {
            String str1 = a.getKey() + " " + a.getValue();
            String str2 = b.getKey() + " " + b.getValue();
            return str1.compareTo(str2);
        }
    });

    Similarity() {

    }

    public double getSimilarity(List<String> s1, List<String> s2) {
        Socket socket = null;
        try {
            //create a client Socket
            socket = new Socket("localhost", 4444);
            //-------connection established---------
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: localhost.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for " + "the connection to: localhost.");
            System.exit(1);
        }
        try {
            ObjectOutputStream outToServer = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inFromServer = new ObjectInputStream(socket.getInputStream());
            List<List<String>> sents = new ArrayList<List<String>>();
            sents.add(s1);
            sents.add(s2);
            outToServer.writeObject(sents);
            Double score = (Double)inFromServer.readObject();
            System.out.println("got " + score + " from server.");
            socket.close();
            return score;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static double sentences(List<String> s1, List<String> s2) {
        double score = 0;
        for(String w1 : s1) {
            for(String w2 : s2) {
                if(w1.equals(w2))
                    score += 1;
                else {
                    Double sTmp = Similarity.cache.get(new Pair<String, String>(w1, w2));
                    if(sTmp == null) {
                        sTmp = lin.calcRelatednessOfWords(w1, w2);
                        score += sTmp;
                        Similarity.cache.put(new Pair<String, String>(w1, w2), sTmp);
                        Similarity.cache.put(new Pair<String, String>(w2, w1), sTmp);
                    } else {
                        score += sTmp;
                    }
                }
            }
        }

        score /= (s1.size()*s2.size());
        return score;
    }

    public static double sentences(String[] s1, String[] s2) {
        double score = 0;
        for(String w1 : s1) {
            for(String w2 : s2) {
                if(w1.equals(w2))
                    score += 1;
                else {
                    Double sTmp = Similarity.cache.get(new Pair<String, String>(w1, w2));
                    if(sTmp == null) {
                        sTmp = lin.calcRelatednessOfWords(w1, w2);
                        score += sTmp;
                        Similarity.cache.put(new Pair<String, String>(w1, w2), sTmp);
                        Similarity.cache.put(new Pair<String, String>(w2, w1), sTmp);
                    } else {
                        score += sTmp;
                    }
                }
            }
        }

        score /= (s1.length*s2.length);
        return score;
    }

    public static double titleToDocument(String title, Document doc) {
        String[] titleToks = title.split(" ");
        String[] sents = doc.getContent().split("\\.");
        double score = 0.0;
        for(String s : sents) {
            score += Similarity.sentences(titleToks, s.trim().split(" "));
        }
        score /= sents.length;
        return score;
    }
}
