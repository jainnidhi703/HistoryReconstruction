import java.io.*;


/**
 * Prints errors in a file
 */
public class ErrorWriter {

    private File file;
    public static final String errorFileName = "exception.txt";
    private FileWriter fw = null;
    private int i = 0;

    public ErrorWriter() throws IOException {
        file = new File(errorFileName);
        fw = new FileWriter(file);
    }


    /**
     * @param filename name of the file who caused error
     * @param errorMsg message
     * @throws IOException
     */
    public void write(String filename, String errorMsg) throws IOException {
        fw.write("["+i+"] "+filename + "\t" + errorMsg + "\n");
        i++;
        fw.flush();
    }


    /**
     * Close error writer
     * @throws IOException
     */
    public void close() throws IOException {
        fw.close();
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        fw.close();
    }
}
