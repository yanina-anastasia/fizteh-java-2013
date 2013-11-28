package ru.fizteh.fivt.students.vlmazlov.storeable;

import java.text.ParseException;
import java.io.StringWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.vlmazlov.shell.QuietCloser;
public class XMLStoreableWriter {

	private static void writeColumn(XMLStreamWriter writer, Object value) throws XMLStreamException {
		if (value == null) {
			writer.writeEmptyElement("null");
		} else {
			writer.writeStartElement("col");
			writer.writeCharacters(value.toString());
			writer.writeEndElement();
		}
	}

	public static String serialize(Storeable value) throws XMLStreamException {	
		StringWriter stringWriter = new StringWriter();
		XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(stringWriter);

		try {
			
			writer.writeStartElement("row");

			int columnIndex = 0;

			do {
				try {
					value.getColumnAt(columnIndex);
				} catch (IndexOutOfBoundsException ex) {
					break;
				}

				writeColumn(writer, value.getColumnAt(columnIndex));
				++columnIndex;
			} while (!Thread.currentThread().isInterrupted());

			writer.writeEndElement();
			
			writer.flush();

			return stringWriter.toString();
		} finally {
			try {
				writer.close();
			} catch (XMLStreamException ex) {
				System.err.println("Unable to close XMLStreamWriter: " + ex.getMessage());
			}
		}	
	}
}