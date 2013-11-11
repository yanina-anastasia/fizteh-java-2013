package ru.fizteh.fivt.students.elenav.utils;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.HashMap;

import javax.xml.stream.XMLStreamException;

import ru.fizteh.fivt.students.elenav.storeable.Deserializer;
import ru.fizteh.fivt.students.elenav.storeable.StoreableTableState;

public class Reader {

	public static void readFile(File in, HashMap<String, String> map) throws IOException {
		DataInputStream s = new DataInputStream(new FileInputStream(in));
		boolean flag = true;
		do {
			try {
				int keyLength = s.readInt();
				int valueLength = s.readInt();
				if (keyLength <= 0 || valueLength <= 0 || keyLength >= 1024*1024 || valueLength >= 1024*1024) {
					throw new IOException("Invalid input");
				}
				byte[] tempKey = new byte[keyLength];	
				s.read(tempKey);
				String key = new String(tempKey, StandardCharsets.UTF_8);
				byte[] tempValue = new byte[valueLength];
				s.read(tempValue);
				String value = new String(tempValue, StandardCharsets.UTF_8);
				map.put(key, value);
			} catch (EOFException e) {
				break;
			}
		} while (flag);
		s.close();
	}

	public static void readFile(File in, StoreableTableState table) throws IOException, ParseException {
		DataInputStream s = new DataInputStream(new FileInputStream(in));
		boolean flag = true;
		do {
			try {
				int keyLength = s.readInt();
				int valueLength = s.readInt();
				if (keyLength <= 0 || valueLength <= 0 || keyLength >= 1024*1024 || valueLength >= 1024*1024) {
					throw new IOException("Invalid input");
				}
				byte[] tempKey = new byte[keyLength];	
				s.read(tempKey);
				String key = new String(tempKey, StandardCharsets.UTF_8);
				byte[] tempValue = new byte[valueLength];
				s.read(tempValue);
				String value = new String(tempValue, StandardCharsets.UTF_8);
				try {
					table.map.put(key, Deserializer.run(table, value));
				} catch (XMLStreamException e) {
					throw new RuntimeException(e);
				}
			} catch (EOFException e) {
				break;
			}
		} while (flag);
		s.close();
		
	}
}
