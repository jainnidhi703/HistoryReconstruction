import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.FileIterator;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


/**
 * Imports data to be used by mallet api
 */
public class MalletDataImporter {

    Pipe pipe;

    public enum PipeType {Array, File}

    public MalletDataImporter(PipeType pt) {
        if(pt == PipeType.File)
            pipe = buildPipeForFile();
        else
            pipe = buildPipeForArray();
    }


    /**
     * Create a pipe for array of data
     * @return Pipe prepared to process array data
     */
    private Pipe buildPipeForArray() {
        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

        // tokenize raw strings
        pipeList.add(new CharSequence2TokenSequence());

        // Normalize all tokens to all lowercase
        pipeList.add(new TokenSequenceLowercase());

        // Remove stopwords from a standard English stoplist.
        //  options: [case sensitive] [mark deletions]
        pipeList.add( new TokenSequenceRemoveStopwords(new File("en.txt"), "UTF-8", false, false, false) );

        // Rather than storing tokens as strings, convert
        //  them to integers by looking them up in an alphabet.
        pipeList.add(new TokenSequence2FeatureSequence());

        return new SerialPipes(pipeList);
    }


    /**
     * Create a pipe for reading data from file
     * @return Pipe prepared to process file data
     */
    private Pipe buildPipeForFile() {
        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

        // Read data from File Objects
        pipeList.add(new Input2CharSequence("UTF-8"));

        // Regular expression for what constitutes a token.
        //  This pattern includes Unicode letters, Unicode numbers,
        //   and the underscore character. Alternatives:
        //    "\\S+"   (anything not whitespace)
        //    "\\w+"    ( A-Z, a-z, 0-9, _ )
        //    "[\\p{L}\\p{N}_]+|[\\p{P}]+"   (a group of only letters and numbers OR
        //                                    a group of only punctuation marks)
        Pattern tokenPattern = Pattern.compile("[\\p{L}\\p{N}_]+");

        // tokenize raw strings
        pipeList.add(new CharSequence2TokenSequence(tokenPattern));

        // Normalize all tokens to all lowercase
        pipeList.add(new TokenSequenceLowercase());

        // Remove stopwords from a standard English stoplist.
        //  options: [case sensitive] [mark deletions]
        pipeList.add( new TokenSequenceRemoveStopwords(new File("en.txt"), "UTF-8", false, false, false) );

        // Rather than storing tokens as strings, convert
        //  them to integers by looking them up in an alphabet.
        pipeList.add(new TokenSequence2FeatureSequence());

        // Do the same thing for the "target" field:
        //  convert a class label string to a Label object,
        //  which has an index in a Label alphabet.
        pipeList.add(new Target2Label());

        return new SerialPipes(pipeList);
    }


    /**
     * Read directory for data
     * @param directory directory to read
     * @return InstanceList
     */
    public InstanceList readDirectory(File directory) {
        return readDirectories(new File[] {directory});
    }


    /**
     * Read multiple directories for data
     * @param directories directories to read
     * @return InstanceList
     */
    public InstanceList readDirectories(File[] directories) {
        // Construct a file iterator, starting with the
        //  specified directories, and recursing through subdirectories.
        // The second argument specifies a FileFilter to use to select
        //  files within a directory.
        // The third argument is a Pattern that is applied to the
        //   filename to produce a class label. In this case, I've
        //   asked it to use the last directory name in the path.
        FileIterator iterator =
                new FileIterator(directories,
                        new TxtFilter(),
                        FileIterator.LAST_DIRECTORY);

        // Construct a new instance list, passing it the pipe
        //  we want to use to process instances.
        InstanceList instances = new InstanceList(pipe);

        // Now process each instance provided by the iterator.
        instances.addThruPipe(iterator);

        return instances;
    }


    /**
     * Read data from given Documents
     * @param docs documents to read
     * @return InstanceList
     */
    public InstanceList readDocuments(List<DocumentClass> docs) {
        InstanceList instances = new InstanceList(pipe);
        for (DocumentClass doc : docs) {
            instances.addThruPipe(new Instance(doc.getTitle()+" "+doc.getContent(),null,doc.getFilename(),null));
        }
        return instances;
    }


    /**
     * Class to filter all files except '.txt' files
     */
    private class TxtFilter implements FileFilter {

        /** Test whether the string representation of the file
         *   ends with the correct extension. Note that {@ref FileIterator}
         *   will only call this filter if the file is not a directory,
         *   so we do not need to test that it is a file.
         */
        @Override
        public boolean accept(File file) {
            return file.toString().endsWith(".txt");
        }
    }
}
