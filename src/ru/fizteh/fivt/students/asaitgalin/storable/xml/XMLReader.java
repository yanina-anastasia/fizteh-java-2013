package ru.fizteh.fivt.students.asaitgalin.storable.xml;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;
import java.text.ParseException;

public class XMLReader {
    private XMLStreamReader reader;

    public XMLReader(String data) throws ParseException {
        XMLInputFactory xif = XMLInputFactory.newInstance();
        try {
            reader = xif.createXMLStreamReader(new StringReader(data));
            if (!reader.hasNext()) {
                throw new ParseException("xmlreader: input string is empty", 0);
            }
            if (reader.next() == XMLStreamConstants.START_ELEMENT) {
                if (!reader.getName().getLocalPart().equals("row")) {
                    throw new ParseException("xmlreader: incorrect xml string", 0);
                }
            }
        } catch (XMLStreamException e) {
            throw new ParseException("xmlreader: failed to create XMLStreamReader, msg: " + e.getMessage(), 0);
        }
    }

    public void close() throws ParseException {
        try {
            int nextTag = reader.next();
            // document should end with </row> or EOF
            if (nextTag != XMLStreamConstants.END_ELEMENT && nextTag != XMLStreamConstants.END_DOCUMENT) {
                throw new ParseException("xmlreader: incorrect input xml", 0);
            }
            reader.close();
        } catch (XMLStreamException e) {
            throw new ParseException("xmlreader: stream error, msg: " + e.getMessage(), 0);
        }
    }

    public Object readNextValue(Class<?> type) throws ParseException {
        return null;
    }



}

