package ru.fizteh.fivt.students.elenav.storeable;

import java.io.StringReader;
import java.text.ParseException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLInputFactory;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.elenav.utils.Functions;

public class Deserializer {
	
	public static Storeable run(Table table, String value) throws XMLStreamException, ParseException {
		
		MyStoreable st = new MyStoreable(table);
		StringReader r = new StringReader(value);
		XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(r);
		int i = 0;
		reader.next();
		if (!reader.isStartElement() || !reader.getName().getLocalPart().equals("row")) {
			throw new ParseException("parse error1", i);
		}
		
		while(i < table.getColumnsCount()) {
			reader.next();
			if (reader.isStartElement() && reader.getName().getLocalPart().equals("col")) {
				reader.next();
				if (reader.isCharacters()) {
					String smth = reader.getText();
					st.setColumnAt(i, Functions.getClass(smth, table.getColumnType(i).getSimpleName()));
					++i;
				} else {
					throw new ParseException("parse error2", i);
				}  
			} else {
				if (reader.isStartElement() && reader.getName().getLocalPart().equals("null")) {
					st.setColumnAt(i, null);
					++i;
				} else {
					throw new ParseException("", i);
				}
			}
			reader.next();
			if (!reader.isEndElement()) {
				throw new ParseException("parse error3", i);
			}
			
		}

		reader.next();
		if (!reader.isEndElement()) {
			throw new ParseException("parse error4", 0);
		}
		
		return st;
		
	}
	
}
