package ru.fizteh.fivt.students.vlmazlov.filemap;

import ru.fizteh.fivt.students.vlmazlov.shell.QuietCloser;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.io.RandomAccessFile;

public class DataBaseWriter {
	private RandomAccessFile dataBaseStorage;
	private final FileMap fileMap;

	public DataBaseWriter(String directory, String file, FileMap _fileMap) 
	throws FileNotFoundException {
		File dir = new File(directory);
		if (!dir.exists()) {
			throw new FileNotFoundException("Specified directory doesn't exist");
		}

		dataBaseStorage = new RandomAccessFile(new File(dir, file), "rw");
		fileMap = _fileMap;
	}

	private int countOffSet() throws IOException {
		int curOffset = 0;

		Iterator<Map.Entry<String, String>> it = fileMap.getEntriesIterator();

		while (it.hasNext()) {
			curOffset += 2 + it.next().getKey().getBytes("UTF-8").length + 1 + 4;
		}

		return curOffset;

	}

	private void storeKey(String key, int offSet) throws IOException {
		dataBaseStorage.writeUTF(key);
		dataBaseStorage.writeByte('0');
		dataBaseStorage.writeInt(offSet);
	}

	public void storeInFile() throws IOException {	
		
		try {

			Iterator<Map.Entry<String, String>> it = fileMap.getEntriesIterator();

			long curOffset = countOffSet(), writePosition;

			while (it.hasNext()) {
				Map.Entry<String, String> entry = it.next();
				storeKey(entry.getKey(), (int)curOffset);
				writePosition = dataBaseStorage.getFilePointer();

				dataBaseStorage.seek(curOffset);
				dataBaseStorage.writeUTF(entry.getValue());
				curOffset = dataBaseStorage.getFilePointer();
				
				dataBaseStorage.seek(writePosition);
			}

			it = fileMap.getEntriesIterator();

			while (it.hasNext()) {
				dataBaseStorage.writeUTF(it.next().getValue());
			}
		} finally {
			QuietCloser.closeQuietly(dataBaseStorage);
		}
	}
}