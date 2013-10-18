package ru.fizteh.fivt.students.vlmazlov.filemap;

import ru.fizteh.fivt.students.vlmazlov.shell.QuietCloser;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.io.RandomAccessFile;

public class DataBaseReader {
	private final RandomAccessFile dataBaseStorage;
	private final FileMap fileMap;

	public DataBaseReader(String directory, String file, FileMap fileMap)
	throws FileNotFoundException {
		File dir = new File(directory);
		if (!dir.exists()) {
			throw new FileNotFoundException("Specified directory doesn't exist");
		}

		File storage = new File(dir, file);
		if ((!storage.exists()) || (0 == storage.length())) {
			throw new StorageNotFoundException();
		}
		dataBaseStorage = new RandomAccessFile(storage, "r");

		this.fileMap = fileMap;
	}
	
	//existence of dataBaseStorage is guaranteed in the constructor

	private String readUTFString(int readingPosition, int length) throws IOException {
		byte[] bytes = new byte[length];

		dataBaseStorage.seek(readingPosition);
		dataBaseStorage.read(bytes);
		return new String(bytes, "UTF-8");
	}

	public void retrieveFromFile() throws IOException {
		//existence of dataBaseStorage is guaranteed in the constructor
		try {
			String key = null;
			int readPosition = 0, initialOffset = -1, prevOffset = -1;

			do {

				dataBaseStorage.seek(readPosition);

				while (dataBaseStorage.getFilePointer() < dataBaseStorage.length()) {
					if (dataBaseStorage.readByte() == '\0') {
						break;
					}
				}

				int keyLen = (int)dataBaseStorage.getFilePointer() - readPosition - 1;

				int curOffset = (int)dataBaseStorage.readInt();
				
				if (prevOffset == -1) {
					initialOffset = curOffset;
				} else {
					fileMap.addToFileMap(key, readUTFString(prevOffset, curOffset - prevOffset));
				}
				prevOffset = curOffset;	
				//read key		
				key = readUTFString(readPosition, keyLen);
				
				readPosition = (int)dataBaseStorage.getFilePointer() + 5;

			} while (readPosition < initialOffset);
			
			fileMap.addToFileMap(key, readUTFString(prevOffset, (int)dataBaseStorage.length() - prevOffset));

		} finally {
			QuietCloser.closeQuietly(dataBaseStorage);
		}
	}
}