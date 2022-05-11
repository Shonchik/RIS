package osm;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import osm.model.NodeDb;
import osm.model.generated.Node;

@AllArgsConstructor
public class OsmProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(OsmProcessor.class);
    private static final String NODE = "node";

    private final NodeService nodeService;

    public void process(InputStream inputStream) throws JAXBException, XMLStreamException {
        LOG.info("OSM processing start");
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = null;
        JAXBContext jaxbContext = JAXBContext.newInstance(Node.class);
        try {
            reader = factory.createXMLStreamReader(inputStream);
            while (reader.hasNext()) {
                int event = reader.next();
                if (XMLStreamConstants.START_ELEMENT == event && NODE.equals(reader.getLocalName())) {
                    processNode(jaxbContext, reader);
                }
            }
        } finally {
            assert reader != null;
            reader.close();
        }
        nodeService.flush();
        LOG.info("OSM processing finish");
    }

    private void processNode(JAXBContext jaxbContext, XMLStreamReader reader) throws JAXBException {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Node node = (Node) unmarshaller.unmarshal(reader);
        nodeService.putNodeBuffered(NodeDb.convert(node));
    }

}
