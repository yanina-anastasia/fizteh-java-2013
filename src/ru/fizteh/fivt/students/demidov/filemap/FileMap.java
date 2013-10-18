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
	
	public void openFile(Shell usedShell) throws IOException {
		String path = System.getProperty("fizteh.db.dir");
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
	}
	
    private String readString(long readPosition, long finishPosition) throws IOException {
        byte[] bytes = new byte[(int)(finishPosition - readPosition)];
        dataBaseFile.seek(readPosition);
        dataBaseFile.read(bytes);
        return new String(bytes, "UTF-8");
}
	
	public void readDataFromFile(Shell usedShell) throws IOException {	
		openFile(usedShell);
		
		if (dataBaseFile.length() == 0) {
			return;
		}
		
		String readKey = null;
		String previousKey = null;
		long positionOfValues = Long.MAX_VALUE;
		long readPosition = 0;
		long previousOffset = Long.MAX_VALUE;
		dataBaseFile.seek(readPosition);

		while (readPosition < positionOfValues) {
			while ((dataBaseFile.getFilePointer() < dataBaseFile.length()) && !(dataBaseFile.readByte() == '\0')) {}
			
			long nextOffset = dataBaseFile.readInt();
			long keyPosition = dataBaseFile.getFilePointer();
			previousKey = readKey;
			readKey = readString(readPosition, dataBaseFile.getFilePointer() - 5);
			
			if (previousOffset == Long.MAX_VALUE) {
				positionOfValues = nextOffset;
			} else {
				currentTable.put(previousKey, readString(previousOffset, nextOffset));
			}
			
			previousOffset = nextOffset;		
			dataBaseFile.seek(keyPosition);
			readPosition = dataBaseFile.getFilePointer();
			System.out.println(nextOffset);
		} 
	
		currentTable.put(readKey, readString(previousOffset, (int)dataBaseFile.length()));
		
		dataBaseFile.close();
	}


	public void writeDataToFile(Shell usedShell) throws IOException {
		openFile(usedShell);
		int offset = 0;		
		for (String key: getCurrentTable().keySet()) {
			offset += 5 + key.getBytes("UTF-8").length;
		}
		
		long writenPosition = 0;
		for (Map.Entry<String, String> currentPair : getCurrentTable().entrySet()) {
			dataBaseFile.seek(writenPosition);
			dataBaseFile.write(currentPair.getKey().getBytes("UTF-8"));
			dataBaseFile.writeByte('\0');
			dataBaseFile.writeInt(offset);
			writenPosition = dataBaseFile.getFilePointer();
			dataBaseFile.seek(offset);
			dataBaseFile.write(currentPair.getValue().getBytes("UTF-8"));
			offset = (int)dataBaseFile.getFilePointer();
		}
		
		dataBaseFile.close();
	}
	
	private final Map<String, String> currentTable = new HashMap<String, String>();
	private RandomAccessFile dataBaseFile;
}
