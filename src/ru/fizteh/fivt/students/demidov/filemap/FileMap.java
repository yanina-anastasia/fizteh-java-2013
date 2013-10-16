package ru.fizteh.fivt.students.demidov.filemap;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import ru.fizteh.fivt.students.demidov.shell.Shell;
import ru.fizteh.fivt.students.demidov.shell.Utils;

public class FileMap {
	public Map<String, String> getCurrentTable() {
		return currentTable;
	}
	
	public void readDataFromFile(Shell usedShell) throws IOException {
		String path = System.getProperty("user.dir"); //System.getProperty("fizteh.db.dir");
		if (path == null) {
			throw new IOException("problem with property");
		}
		
		usedShell.curShell.changeCurrentDirectory(path + File.separator + "db.dat");
		File currentDirectory = Utils.getFile(usedShell.curShell.getCurrentDirectory(), usedShell);
		
		if (!currentDirectory.exists()) {
			if (!currentDirectory.createNewFile()) {
				throw new IOException("unable to create db.dat");
			}
		}
		
		dataBaseFile = new RandomAccessFile(currentDirectory, "rwd");
		
		if (dataBaseFile.length() == 0) {
			return;
		}
		
		long positionOfValues = Long.MAX_VALUE;
		long readedPosition = 0;
		while (readedPosition < positionOfValues) {
			dataBaseFile.seek(readedPosition);
			String key = dataBaseFile.readUTF();
			dataBaseFile.readChar();
			long offset = dataBaseFile.readInt();
			readedPosition = dataBaseFile.getFilePointer();
			dataBaseFile.seek(offset);
			currentTable.put(key, dataBaseFile.readUTF());
			if (positionOfValues == Long.MAX_VALUE) {
				positionOfValues = offset;
			}
		}
	}


	public void writeDataToFile(Shell usedShell) throws IOException {
		int offset = 0;		
		for (String key: getCurrentTable().keySet()) {
			offset += 8 + key.getBytes("UTF-8").length;
		}
		
		long writenPosition = 0;
		for (Map.Entry<String, String> currentPair : getCurrentTable().entrySet()) {
			dataBaseFile.seek(writenPosition);
			dataBaseFile.writeUTF(currentPair.getKey());
			dataBaseFile.writeChar('\0');
			dataBaseFile.writeInt(offset);
			writenPosition = dataBaseFile.getFilePointer();
			dataBaseFile.seek(offset);
			dataBaseFile.writeUTF(currentPair.getValue());
			offset = (int)dataBaseFile.getFilePointer();
		}
	}
	
	private final Map<String, String> currentTable = new HashMap<String, String>();
	private RandomAccessFile dataBaseFile;
}
