import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;

public class QRelTopicParser {

    private int num;
    private String title;
    private String desc;
    private String narr;

    QRelTopicParser(String filename, int qNo) throws IOException, XMLStreamException {
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
//        xmlContent = xmlContent.replaceAll("&", "&amp;");
//        xmlContent = xmlContent.replaceAll("<\\s?br\\s?/?>", "");
//        xmlContent = xmlContent.replaceAll("<font[\\sa-zA-Z0-9=]*>", "");
//        xmlContent = xmlContent.replaceAll("</font>", "");
        byte[] byteArray = xmlContent.getBytes("UTF-8");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
        XMLStreamReader reader = factory.createXMLStreamReader(inputStream);
//        XMLStreamReader reader = factory.createFilteredReader( reader, new MyFilter());



        String tagContent = "";
        boolean append = false;
        while(reader.hasNext()) {
            int event = XMLStreamConstants.COMMENT;
            event = reader.next();

            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    if ("title".equals(reader.getLocalName()) || "desc".equals(reader.getLocalName())
                            || "narr".equals(reader.getLocalName()))
                        append = true;
                    else
                        append = false;
                    break;
                case XMLStreamConstants.CHARACTERS:
                    if (append)
                        tagContent += reader.getText().trim().replaceAll("[ \\t\\r\\n]+"," ");
                    else
                        tagContent = reader.getText().trim();
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    if ("num".equals(reader.getLocalName())) {
                        this.num = Integer.parseInt(tagContent);
                    } else if ("title".equals(reader.getLocalName())) {
                        this.title = tagContent;
                    } else if("desc".equals(reader.getLocalName())) {
                        this.desc = tagContent;
                    } else if("narr".equals(reader.getLocalName())) {
                        this.narr = tagContent;
                        if(this.num == qNo)
                            return;
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

    public int getNum() {
        return num;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getNarr() {
        return narr;
    }
}

