import com.jaunt.NodeNotFound;
import com.jaunt.ResponseException;
import com.jaunt.UserAgent;
import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Class that gets search results from Microsoft bing
 */
public class BingResults {

    static String accountKey = "Uc172BmWxqKuh0M2lNcjv9UpAnBpwWPRdjXKNeXuC1Y=";
    static ArrayList<String> searchUrl;

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
                e.printStackTrace();
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
}
