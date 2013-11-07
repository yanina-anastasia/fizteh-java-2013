package ru.fizteh.fivt.students.demidov.filemap;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

public class FileMap implements FileMapState {
	public FileMap(String path) throws IOException {
		if (path == null) {
			throw new IOException("wrong path " + path);
		}	
		if ((new File(path)).isDirectory()) {
			path += File.separator + "db.dat";
		}
		this.path = path;
		
		currentTable = new HashMap<String, String>();
	}
	
	public FileMap getCurrentFileMap(String key) {
		return this;
	}
	
	public Map<String, String> getCurrentTable() {
		return currentTable;
	}
	
	public void clearFile() throws IOException {
		File currentFile = new File(path);
		
		if (currentFile.exists()) {
			currentFile.delete();
		}
		
		if (!currentFile.createNewFile()) {
			throw new IOException("unable to create " + path);
		}
	}
	
	private String readString(RandomAccessFile dataBaseFile, int readPosition, int finishPosition) throws IOException {
		int readLength = finishPosition - readPosition;
		if ((readLength <= 0) || (readLength > 1024 * 1024)) {  //max string length is declared as 1Mb
			dataBaseFile.close();
			throw new IOException("wrong string format");
		}
		if ((readPosition > (int)dataBaseFile.length()) || (finishPosition > (int)dataBaseFile.length())) {  
			dataBaseFile.close();
			throw new IOException("offset exceeds the limit");
		}
		byte[] bytes = new byte[readLength];
		dataBaseFile.seek(readPosition);
		dataBaseFile.read(bytes);
		return new String(bytes, "UTF-8");
	}
	
	public void readDataFromFile() throws IOException {	
		File currentFile = new File(path);
		
		if (!currentFile.exists()) {
			if (!currentFile.createNewFile()) {
				throw new IOException("unable to create " + path);
			}
		}
			
		try(RandomAccessFile dataBaseFile = new RandomAccessFile(currentFile, "r")) {		
			if (dataBaseFile.length() == 0) {
				return;
			}
		
			String readKey = null;
			String previousKey = null;
			int positionOfValues = Integer.MAX_VALUE;
			int readPosition = 0;
			int previousOffset = -1;
			dataBaseFile.seek(readPosition);

			while (readPosition < positionOfValues) {
				while ((dataBaseFile.getFilePointer() < dataBaseFile.length()) && !(dataBaseFile.readByte() == '\0')) {}
			
				int nextOffset = dataBaseFile.readInt();
				if (nextOffset < 0) {
					throw new IOException("negative offset");
				}
			
				int keyPosition = (int)dataBaseFile.getFilePointer();
				previousKey = readKey;
				readKey = readString(dataBaseFile, readPosition, (int)dataBaseFile.getFilePointer() - 5);
	
				if (previousOffset == -1) {
					positionOfValues = nextOffset;
				} else {
					currentTable.put(previousKey, readString(dataBaseFile, previousOffset, nextOffset));
				}
			
				previousOffset = nextOffset;		
				dataBaseFile.seek(keyPosition);
				readPosition = (int)dataBaseFile.getFilePointer();
			} 
	
			currentTable.put(readKey, readString(dataBaseFile, previousOffset, (int)dataBaseFile.length()));
		
			dataBaseFile.close();
		}
	}


	public void writeDataToFile() throws IOException {
		clearFile();
		
		try(RandomAccessFile dataBaseFile = new RandomAccessFile(new File(path), "rwd")) {		
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
		
			if (dataBaseFile.length() == 0) {
				dataBaseFile.close();
				(new File(path)).delete();
			}
		
			dataBaseFile.close();
		}
	}
	
	private Map<String, String> currentTable;
	private String path;
}
