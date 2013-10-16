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

	public DataBaseReader(String directory, String file, FileMap _fileMap)
	throws IOException, FileNotFoundException {
		File dir = new File(directory);
		if (!dir.exists()) {
			throw new FileNotFoundException("Specified directory doesn't exist");
		}

		File storage = new File(dir, file);
		if (!storage.exists()) {
			throw new IOException();
		}
		dataBaseStorage = new RandomAccessFile(storage, "r");

		fileMap = _fileMap;
	}
	
	//existence of dataBaseStorage is guaranteed in the constructor

	public void retrieveFromFile() throws IOException {
		//existence of dataBaseStorage is guaranteed in the constructor
		try {
			String key, value = null;
			int readPosition = 0, initialOffset = -1;

			while ((-1 == initialOffset) || (readPosition < initialOffset)) {

				key = dataBaseStorage.readUTF();
				dataBaseStorage.readByte();

				int curOffset = (int)dataBaseStorage.readInt();
				if (initialOffset == -1) {
					initialOffset = curOffset;
				}

				readPosition = (int)dataBaseStorage.getFilePointer();
				dataBaseStorage.seek(curOffset);

				value = dataBaseStorage.readUTF();
				fileMap.addToFileMap(key, value);
				dataBaseStorage.seek(readPosition);
			}
		} finally {
			QuietCloser.closeQuietly(dataBaseStorage);
		}
	}
}