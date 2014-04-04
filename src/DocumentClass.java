public class DocumentClass {

    private int clusterID = -1;
    protected String filename = null;
    protected String title = null;
    protected String content = null;
    protected String date = null;
    protected double score = 0.0;

    public DocumentClass() {
    }

    public DocumentClass(String filename) {
        this.filename = filename;
    }

    public DocumentClass(int clusterID, String filename, String content) {
        this.clusterID = clusterID;
        this.filename = filename;
        this.content = content;
        date = dateFromFileName(filename);
    }

    public boolean contains(String word) {
        return content.contains(word);
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

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getScore() {
        return this.score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
