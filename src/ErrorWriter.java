import java.io.*;

public class ErrorWriter {

    private File file;
    public static final String errorFileName = "exception.txt";
    private FileWriter fw = null;
    private int i = 0;

    public ErrorWriter() throws IOException {
        file = new File(errorFileName);
        fw = new FileWriter(file);
    }

    public void write(String filename, String errorMsg) throws IOException {
        fw.write("["+i+"] "+filename + "\t" + errorMsg + "\n");
        i++;
        fw.flush();
    }

    public void close() throws IOException {
        fw.close();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        fw.close();
    }
}
