package ru.fizteh.fivt.students.irinapodorozhnaya.utils;

import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.irinapodorozhnaya.storable.MyStoreable;

public class XMLSerializer {
    
    public static String serialize(Table table, Storeable s) throws XMLStreamException {
      
	if (s == null) {
	    return null;
	}
	
	StringWriter result = new StringWriter();
        XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(result);
       
        writer.writeStartElement("row");
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            writer.writeStartElement("col");
            Object element = s.getColumnAt(i);
            if (element == null) {
                writer.writeStartElement("null");
                writer.writeEndElement();
        
            } else {
                if (element.getClass() != table.getColumnType(i)) {
                    throw new ColumnFormatException();
                }
                writer.writeCharacters(element.toString());
            }
            writer.writeEndElement();
        }
        writer.writeEndElement();
      
        return result.toString();            
    }
    
    public static Storeable deserialize(Table table, String s) throws XMLStreamException, ParseException {
	
	if (s == null) {
	    return null;
	}
	
        XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(s));
        Storeable storeable = new MyStoreable(table);
        int i = 0;
        
        reader.next();
        if (!reader.isStartElement() || !reader.getName().getLocalPart().equals("row")) {
            throw new ParseException("", 0);
        }
        
        while (i < table.getColumnsCount()) {
            
            reader.next();
            if (!reader.isStartElement() || !reader.getName().getLocalPart().equals("col")) {
                    throw new ParseException("", 0);
            }   
            
            reader.next();
            if (reader.isStartElement()) {
                if (reader.getName().getLocalPart().equals("null")) {
                    reader.next();
                    if (!reader.isEndElement()) {
                        throw new ParseException("", 0);
                    }
                    storeable.setColumnAt(i++, null);
                } else {
                    throw new ParseException("", 0);
                }
            } else if (reader.isCharacters()) {
                String object = reader.getText();
                storeable.setColumnAt(i, getObject(object, table.getColumnType(i++).getSimpleName()));
            } else {
                throw new ParseException("", 0);
            }
            
            reader.next();
            if (!reader.isEndElement()) {
                throw new ParseException("", 0);
            }    
        }
        
        return storeable;
    }
    
    public static Object getObject(String string, String expectedClassName) throws ParseException {
        if (expectedClassName.equals("String")) {
            return string;
        } else if (expectedClassName.equals("Integer")) {
            return Integer.parseInt(string);
        }  else if (expectedClassName.equals("Long")) {
            return Long.parseLong(string);
        }  else if (expectedClassName.equals("Byte")) {
            return Byte.parseByte(string);
        }  else if (expectedClassName.equals("Float")) {
            return Float.parseFloat(string);
        }  else if (expectedClassName.equals("Double")) {
            return Double.parseDouble(string);
        }  else if (expectedClassName.equals("Boolean")) {
            return Boolean.parseBoolean(string);
        } else {
           throw new ParseException("", 0); 
        }
    }
}
