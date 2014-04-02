import javax.xml.stream.*;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;

public class XmlDocument {
    private String filename;
    private String content;
    private String title;
    private String date;
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
        br.close();
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

//        if(filename.endsWith(".utf8")) {
//            String DATE_REGEX = "(Sunday|Monday|Tuesday|Wednesday|Thursday|Friday|Saturday),\\s(January|February|March|April|May|June|July|August|September|October|November|December)\\s(\\d{2}),\\s(\\d{4})";
//            Matcher matcher = Pattern.compile(DATE_REGEX,Pattern.CASE_INSENSITIVE).matcher(content);
//            String month = "";
//            String date = "";
//            String year = "";
//            if(matcher.find()) {
//                month = matcher.group(2);
//                date = matcher.group(3);
//                year = matcher.group(4);
//            }
//            this.date = year+month+date;
//        } else {
//
//        }
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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