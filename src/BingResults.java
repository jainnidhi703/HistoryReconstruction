import com.jaunt.NodeNotFound;
import com.jaunt.ResponseException;
import com.jaunt.UserAgent;
import org.apache.commons.codec.binary.Base64;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


/**
 * Class that gets search results from Microsoft bing
 */
public class BingResults {

    private static String accountKey = "Uc172BmWxqKuh0M2lNcjv9UpAnBpwWPRdjXKNeXuC1Y=";
    private static ArrayList<String> searchUrl;


    /**
     * No. of results needed
     */
    public enum Results{
        TOP_10, TOP_20, TOP_30
    }


    /**
     * Fires a query in bing and downloads the html of search results
     * @param query search query
     * @param maxResults max no. of results
     * @return content of each search result
     */
    public static List<String> getResults(String query, Results maxResults) {
        ArrayList<String> contents = null;
        if(maxResults == Results.TOP_10)
            contents = new ArrayList<String>(10);
        else if(maxResults == Results.TOP_20)
            contents = new ArrayList<String>(20);
        else if(maxResults == Results.TOP_30)
            contents = new ArrayList<String>(30);

        query = query.replaceAll(" ", "%20");
        byte[] accountKeyBytes = Base64
                .encodeBase64((accountKey + ":" + accountKey).getBytes());
        String accountKeyEnc = new String(accountKeyBytes);

        JSONParser parser = new JSONParser();
        StringBuilder sb = new StringBuilder();
        URL url;

        try{
            url = new URL("https://api.datamarket.azure.com/Data.ashx/Bing/Search/v1/Web?Query=%27" +
                    query + "%27&$top=10&$format=JSON");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Basic " + accountKeyEnc);
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) parser.parse(sb.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JSONObject d = (JSONObject) jsonObject.get("d");
        JSONArray results = (JSONArray) d.get("results");

        Iterator<?> i = results.iterator();
        int no = 0;
        searchUrl  = new ArrayList<String>();
        while(i.hasNext()){
            JSONObject innerObject = (JSONObject) i.next();
            searchUrl.add(innerObject.get("Url").toString());
            URL downloadUrl = null;
            try {
                downloadUrl = new URL(innerObject.get("Url").toString());
            } catch (MalformedURLException e) {
                // if the url formed in not proper no need to go ahead
                // move to next iteration
                continue;
            }
            InputStream is = null;
            try {
                is = downloadUrl.openStream();
                int ptr = 0;
                StringBuilder buffer = new StringBuilder();
                while ((ptr = is.read()) != -1) {
                    buffer.append((char)ptr);
                }
                no++;

                String tmp = getInnerText(buffer.toString());
                if(tmp != null)
                    contents.add(tmp);
            } catch (IOException e) {
                System.out.println("Skipping doc no. " + no);
                continue;
            }
            System.out.println("downloaded doc no : " + no);
        }

        return contents;
    }


    /**
     * @param data html data
     * @return Text inside body tag
     */
    private static String getInnerText(String data) {
        UserAgent ua = new UserAgent();
        String usefulData = null;
        try {
            ua.openContent(data);
            com.jaunt.Element body = ua.doc.findFirst("body");
            usefulData = body.innerText();
            usefulData = usefulData.replaceAll("\\s{2,}", " ");
            return usefulData;
        } catch (NodeNotFound nodeNotFound) {
            nodeNotFound.printStackTrace();
        } catch (ResponseException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void mergeResultsWithIndex(List<String> contents) throws IOException {
        // get no of words in indexed data
        long totalNoOfWords = Settings.getTotalNoOfWords();

        Directory dir = FSDirectory.open(new File(Globals.INDEX_STORE_DIR));
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_46, CharArraySet.EMPTY_SET);
        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_46, analyzer);

//        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE); // remove any previous indxes
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);   // keep prev indxes

        IndexWriter indxWriter = new IndexWriter(dir, iwc);
        int dd = 0;
        for(String file : contents) {
            org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
            String filename = UUID.randomUUID().toString() + ".txt";
            // FIXME : extract date might need some fix for extracting dates of any format as documents are taken from web
            String[] dates = IRUtils.extractDate(file, filename);
            if(dates == null || dates.length == 0) {
                dd++;
                continue;
            }

            // keep the oldest date
            Arrays.sort(dates);
            String dateData = dates[0];
            if(dateData.isEmpty()) {
                dd++;
                return;
            }
            doc.add(new StringField("filename", filename, Field.Store.YES));
            doc.add(new TextField("title", "DEFAULT", Field.Store.YES));
            doc.add(new TextField("date", dateData, Field.Store.YES));
            doc.add(new TextField("contents", file, Field.Store.YES));

            totalNoOfWords += file.split("\\s+").length;

            if (indxWriter.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE) {
                indxWriter.addDocument(doc);
            } else {
                indxWriter.updateDocument(new Term("filename", filename), doc);
            }
        }

        System.out.println(dd + " no of files were skipped coz they had no date");

        // for future use
        Settings.setTotalNoOfWords(totalNoOfWords);
        LuceneUtils.TotalWordCount = totalNoOfWords;

        // close indexwriter
        indxWriter.close();
    }
}
