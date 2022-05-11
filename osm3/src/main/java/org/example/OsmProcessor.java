package org.example;

import java.io.InputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OsmProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(OsmProcessor.class);

    private static final QName USER_ATTR_NAME = new QName("user");
    private static final QName ID_ATTR_NAME = new QName("id");
    private static final QName KEY_ATTR_NAME = new QName("k");
    private static final String NODE = "node";
    private static final String TAG = "tag";

    public static OsmResultContainer process(InputStream inputStream) throws XMLStreamException {
        LOG.info("OSM processing start");
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = null;
        OsmResultContainer result = new OsmResultContainer();
        try {
            eventReader = factory.createXMLEventReader(inputStream);
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (XMLStreamConstants.START_ELEMENT == event.getEventType()) {
                    StartElement startElement = event.asStartElement();
                    if (NODE.equals(startElement.getName().getLocalPart())) {
                        Attribute userAttribute = startElement.getAttributeByName(USER_ATTR_NAME);
                        result.userEditsCount(userAttribute.getValue());
                        Attribute idAttribute = startElement.getAttributeByName(ID_ATTR_NAME);
                        LOG.debug("Processing tags for node with id {} start", idAttribute);
                        processTags(result, eventReader);
                        LOG.debug("Tags processing finish");
                    }
                }
            }
        } finally {
            assert eventReader != null;
            eventReader.close();
        }
        LOG.info("OSM processing finish");
        return result;
    }

    private static void processTags(OsmResultContainer result,
                                    XMLEventReader eventReader) throws XMLStreamException {
        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            if (XMLStreamConstants.END_ELEMENT == event.getEventType() &&
                    NODE.equals(event.asEndElement().getName().getLocalPart())) {
                return;
            }
            if (XMLStreamConstants.START_ELEMENT == event.getEventType()) {
                StartElement startElement = event.asStartElement();
                if (TAG.equals(startElement.getName().getLocalPart())) {
                    Attribute key = startElement.getAttributeByName(KEY_ATTR_NAME);
                    result.incrementTagCount(key.getValue());
                }
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }

    private OsmProcessor() {
        throw new IllegalAccessError();
    }
}
