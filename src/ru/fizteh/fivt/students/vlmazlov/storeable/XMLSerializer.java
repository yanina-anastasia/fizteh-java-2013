package ru.fizteh.fivt.students.vlmazlov.storeable;

import java.text.ParseException;
import java.io.BufferedOutputStream;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.vlmazlov.shell.QuietCloser;
public class XMLStoreableWriter {

	/*private String valueToString(Object value) {
		switch (TypeName.getByClass(value.getClass())) {
			case INTEGER:
				return Integer.parseInt(value);
			case LONG:
				return Long.parseLong(value);
			case DOUBLE:
				return Double.parseDouble(value);
			case FLOAT:
				return Float.parseFloat(value);
			case BYTE:
				return Byte.parseByte(value);
			case BOOLEAN:
				return Boolean.parseBoolean(value);
			case STRING:
				return (String)value;
			case null:
				return null;
		}
	}*/

	private void writeColumn(XMLStreamWriter writer, Object value) {
		writer.writeStartElement("col");

		if (value == null) {
			writer.writeStartElement("null");
			writer.writeEndElement();
		} else {
			writer.writeCharacters(value.toString());
		}

		writer.writeEndElement();
	}

	public String serialize(Storeable value) {

		XMLOutputFactory factory = new XMLOutputFactory();
		XMLStreamWriter writer = factory.createXMLStreamWriter(new OutputStream());

		
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
		
		writer.close();	

		return writer.toString();
	}
}