package ru.fizteh.fivt.students.vlmazlov.filemap;

import ru.fizteh.fivt.students.vlmazlov.shell.Shell;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class FileMap implements Iterable<Map.Entry<String, String>>, FileMapState {

	private Map<String, String> map;

	public FileMap() {
		map = new HashMap<String, String>();
	}

	public String put(String key, String value) {
		return map.put(key, value);
	}

	public String get(String key) {
		return map.get(key);
	}
 
	public String remove(String key) {
		return map.remove(key);
	}

	public Iterator<Map.Entry<String, String>> iterator() {
		return map.entrySet().iterator();
	}

	public FileMap getFileMap() {
		return this;
	}
}