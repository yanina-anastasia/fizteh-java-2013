package ru.fizteh.fivt.students.demidov.multifilehashmap;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import ru.fizteh.fivt.students.demidov.shell.Utils;

public class FileMap {	
	public FileMap(String newDirectoryPath) throws IOException { 
		currentTable = new HashMap<String, String>();
		baseFiles = new HashMap<String, RandomAccessFile>();
		
		if (new File(newDirectoryPath).isDirectory()) {
			directoryPath = newDirectoryPath;
		} else {
			throw new IOException("wrong directory");
		}
	}
	
	public Map<String, String> getCurrentTable() {
		return currentTable;
	}
	
	private void putFile(Integer ndirectory, Integer nfile) throws IOException {	
		File currentDirectory = new File(directoryPath + File.separator + ndirectory.toString() + ".dir");
		if (!currentDirectory.exists()) {			
			if (!currentDirectory.mkdir()) {
				throw new IOException("unable to create " + currentDirectory.getPath());
			}
		}
		
		File currentFile = new File(currentDirectory.getPath() + File.separator + nfile.toString() + ".dat");
		
		if (!currentFile.exists()) {			
			if (!currentFile.createNewFile()) {
				throw new IOException("unable to create " + currentFile.getPath());
			}
		}
		
		baseFiles.put(Integer.toString(ndirectory) + " " + Integer.toString(nfile), new RandomAccessFile(currentFile, "rwd"));
	}
	
	private void openFiles() throws IOException {
		for (String subdirectoryName : (new File(directoryPath)).list()) {
			File subdirectory = new File(directoryPath, subdirectoryName);
			if ((!(subdirectory.isDirectory())) || (!(subdirectoryName.matches("[0-9][.]dir|1[0-5][.]dir")))) {
				throw new IOException("wrong subdirectory " + subdirectory.getPath());
			} else {
				for (String baseFileName : subdirectory.list()) {
					File baseFile = new File(subdirectory, baseFileName);
					if ((!(baseFile.isFile())) || (!(baseFileName.matches("[0-9][.]dat|1[0-5][.]dat")))) {
						throw new IOException("wrong baseFile " + baseFile.getPath());
					} else {
						baseFiles.put(Integer.toString(getNumber(subdirectoryName)) + " " + Integer.toString(getNumber(baseFileName)), new RandomAccessFile(baseFile.getPath(), "rwd")); 
					}
				}
			}
		}
	}
	
	public void closeFiles() throws IOException {
		for (String currentPlace: baseFiles.keySet()) {
			baseFiles.get(currentPlace).close();
		}	
	}
	
	public void clearFileMapDirectory() throws IOException {
		for (String subdirectory: (new File(directoryPath)).list()) {
			Utils.deleteFileOrDirectory(new File(directoryPath, subdirectory));
		}	
		baseFiles = new HashMap<String, RandomAccessFile>();
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
	
	public void readData() throws IOException {	
		openFiles();
		
		for (String currentPlace: baseFiles.keySet()) {
			RandomAccessFile dataBaseFile = baseFiles.get(currentPlace);
			int currentNDirectory = Integer.parseInt(currentPlace.substring(0, currentPlace.indexOf(" ")));
			int currentNfile = Integer.parseInt(currentPlace.substring(currentPlace.indexOf(" ") + 1));
	
			if (dataBaseFile.length() == 0) {
				throw new IOException("empty file");
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
					int previousHashCode = previousKey.hashCode();
					if ((currentNDirectory != previousHashCode % 16) || (currentNfile != (previousHashCode / 16) % 16)) {
						throw new IOException("wrong key");
					}
					currentTable.put(previousKey, readString(dataBaseFile, previousOffset, nextOffset));
				}
			
				previousOffset = nextOffset;		
				dataBaseFile.seek(keyPosition);
				readPosition = (int)dataBaseFile.getFilePointer();
			} 
	
			int readHashCode = readKey.hashCode();
			if ((currentNDirectory != readHashCode % 16) || (currentNfile != (readHashCode / 16) % 16)) {
				throw new IOException("wrong key");
			}
			currentTable.put(readKey, readString(dataBaseFile, previousOffset, (int)dataBaseFile.length()));
		}
		
		closeFiles();
	}

	public void writeData() throws IOException {	
		clearFileMapDirectory();
		Map<Integer, Integer> offset = new HashMap<Integer, Integer>();
		Map<Integer, Integer> writenPosition = new HashMap<Integer, Integer>();
		
		for (String key: getCurrentTable().keySet()) {
			int hashcode = key.hashCode();
			int ndirectory = hashcode % 16;
			int nfile = (hashcode / 16) % 16;
			putFile(ndirectory, nfile);
			Integer countedOffset = offset.get(hashcode);
			if (countedOffset == null) {
				countedOffset = 0;
			}
			offset.put(hashcode, countedOffset + 5 + key.getBytes("UTF-8").length);
		}
		
		for (Map.Entry<String, String> currentPair : getCurrentTable().entrySet()) {
			int hashcode = currentPair.getKey().hashCode();
			int ndirectory = hashcode % 16;
			int nfile = (hashcode / 16) % 16;
			RandomAccessFile currentFile = baseFiles.get(Integer.toString(ndirectory) + " " + Integer.toString(nfile));
			
			Integer currentWritenPosition = writenPosition.get(hashcode);
			if (currentWritenPosition == null) {
				currentWritenPosition = 0;
			}
			currentFile.seek(currentWritenPosition);
			currentFile.write(currentPair.getKey().getBytes("UTF-8"));
			currentFile.writeByte('\0');
			currentFile.writeInt(offset.get(hashcode));
			writenPosition.put(hashcode, (int)currentFile.getFilePointer());
			currentFile.seek(offset.get(hashcode));
			currentFile.write(currentPair.getValue().getBytes("UTF-8"));
			offset.put(hashcode, (int)currentFile.getFilePointer());
		}
		
		closeFiles();
	}
	
	private Integer getNumber(String name) {
		int number;
		if (!(Character.toString(name.charAt(1)).equals("."))) {
			number = Integer.parseInt(name.substring(0, 2));
		} else {
			number = Integer.parseInt(name.substring(0, 1));
		}
		
		return number;
	}
	
	private Map<String, String> currentTable;
	private Map<String, RandomAccessFile> baseFiles;
	private String directoryPath;
}
