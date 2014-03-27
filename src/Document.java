public class Document {

    private int clusterID = -1;
    private String filename = null;
    private String content = null;
    private String date = null;
    public double score = 0.0;

    public Document(int clusterID, String filename, String content) {
        this.clusterID = clusterID;
        this.filename = filename;
        this.content = content;
        date = dateFromFileName(filename);
    }

    //    en.13.3.1.2009.6.11
    public static String dateFromFileName(String str) {
        str = str.substring(str.lastIndexOf('/')+1);
        String[] toks = str.split("\\.");

        String date = "";
        if(toks.length > 6)
            date = toks[6];

        String month = "";
        if(toks.length > 5)
            month = toks[5];

        String year = "";
        if(toks.length > 4)
            year = toks[4];

        if(date.length() == 1) date = "0" + date;
        if(month.length() == 1) month = "0" + month;
        return year+month+date;
    }

    public int getClusterID() {
        return clusterID;
    }

    public void setClusterID(int id) {
        this.clusterID = id;
    }

    public String getFilename() {
        return filename;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }
}
