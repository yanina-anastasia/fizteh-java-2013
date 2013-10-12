package ru.fizteh.fivt.students.irinapodorozhnaya.db;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.Set;

import ru.fizteh.fivt.students.irinapodorozhnaya.utils.AbstractCommand;

public class CommandExit extends AbstractCommand{
	public CommandExit(DbState st) {
		super(0, st);
	}

	public String getName() {
		return "exit";
	}

	public void execute(String[] args) throws IOException {
		DbState st = (DbState) getState();
		RandomAccessFile dbFile = st.getDbFile();
		int offset = 0;
		long pos = 0;
		
		Set<String> keys = st.getData().keySet();
		for (String s: keys) {
			offset += s.getBytes("UTF-8").length + 8;
		}
		
		for (Map.Entry<String, String> s:((DbState) getState()).getData().entrySet()) {
			dbFile.seek(pos);
			dbFile.writeUTF(s.getKey());
			dbFile.writeChar('\0');
			dbFile.writeInt(offset);
			pos = dbFile.getFilePointer();
			dbFile.seek(offset);
			dbFile.writeUTF(s.getValue());
			offset = (int) dbFile.getFilePointer();
		}
		
		if (dbFile.length() == 0) {
			st.currentDir.delete();
		}
		
		throw new ExitRuntimeException();
	}
}
