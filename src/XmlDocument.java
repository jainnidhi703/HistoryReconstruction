import javax.xml.stream.*;
import java.io.*;

public class XmlDocument {
    private String filename;
    private String content;
    private String title;
    private boolean error = false;
    private static ErrorWriter ew;

    static {
        try {
            ew = new ErrorWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public XmlDocument() {
        filename = null;
        content = null;
        title = null;
    }

    public XmlDocument(String filename) throws XMLStreamException, IOException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
//        factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.FALSE);
//        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
        BufferedReader br = new BufferedReader(new FileReader(filename));
        StringBuilder sb = new StringBuilder();
        String xmlContent = null;
        while( (xmlContent = br.readLine()) != null ) {
            sb.append(xmlContent);
        }
        xmlContent = sb.toString();
        xmlContent = xmlContent.replaceAll("&", "&amp;");
        xmlContent = xmlContent.replaceAll("<\\s?br\\s?/?>", "");
        xmlContent = xmlContent.replaceAll("<font[\\sa-zA-Z0-9=]*>", "");
        xmlContent = xmlContent.replaceAll("</font>", "");
        byte[] byteArray = xmlContent.getBytes("UTF-8");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
        XMLStreamReader reader = factory.createXMLStreamReader(inputStream);
        XMLStreamReader filterReader = factory.createFilteredReader( reader, new MyFilter());



        String tagContent = "";
        boolean append = false;
        while(filterReader.hasNext()) {
            int event = XMLStreamConstants.COMMENT;
            try {
                event = filterReader.next();
            } catch (XMLStreamException e) {
                System.out.println("except");
                ew.write(filename, e.getMessage());
                error = true;
                break;
            }


            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    if ("TEXT".equals(filterReader.getLocalName()) || "TITLE".equals(filterReader.getLocalName()))
                        append = true;
                    else
                        append = false;
                    break;
                case XMLStreamConstants.CHARACTERS:
                    if (append)
                        tagContent += filterReader.getText().trim().replaceAll("[ \\t\\r\\n]+"," ");
                    else
                        tagContent = filterReader.getText().trim();
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    if ("DOCNO".equals(filterReader.getLocalName())) {
                        this.filename = tagContent;
                    } else if ("TEXT".equals(filterReader.getLocalName())) {
                        this.content = tagContent;
                    } else if("TITLE".equals(filterReader.getLocalName())) {
                        this.title = tagContent;
                    }
                    append = false;
                    tagContent = "";
                    break;
                case XMLStreamConstants.COMMENT:
                default:
                    break;
            }
        }
    }

    public String getFilename() {
        return filename;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public boolean isError() {
        return error;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

class MyFilter implements StreamFilter {

    private static final String[] lst = {"DOC", "DOCNO", "TITLE","TEXT"};
    @Override
    public boolean accept(XMLStreamReader reader) {
        if(!reader.isStartElement() && !reader.isEndElement()) return true;
        String st = reader.getLocalName();
        for (String x : lst) {
            if(x.equals(st))
                return true;
        }
        return false;
    }
}