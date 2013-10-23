package ru.fizteh.fivt.students.vlmazlov.filemap;

import ru.fizteh.fivt.students.vlmazlov.shell.QuietCloser;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.ValidityChecker;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.ValidityCheckFailedException;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.io.RandomAccessFile;

public class DataBaseReader {
	private final RandomAccessFile dataBaseStorage;
	private final FileMap fileMap;

	public DataBaseReader(String root, File file, FileMap fileMap)
	throws FileNotFoundException, ValidityCheckFailedException {

		this(new File(root), file, fileMap); 
	}

	public DataBaseReader(String root, String file, FileMap fileMap)
	throws FileNotFoundException, ValidityCheckFailedException {

		this(new File(root), file, fileMap); 
	}

	public DataBaseReader(File root, String file, FileMap fileMap)
	throws FileNotFoundException, ValidityCheckFailedException {
		this(root, new File(root, file), fileMap);
	}

	public DataBaseReader(File root, File storage, FileMap fileMap)
	throws FileNotFoundException, ValidityCheckFailedException {

		if (root == null) {
			throw new FileNotFoundException("Directory not specified");
		}

		ValidityChecker.checkFileMapRoot(root);

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

	public void read() throws IOException, ValidityCheckFailedException {
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
				
				ValidityChecker.checkFileMapOffset(curOffset);				

				if (prevOffset == -1) {
					initialOffset = curOffset;
				} else {
					String value = readUTFString(prevOffset, curOffset - prevOffset);
					
					ValidityChecker.checkFileMapValue(value);

					fileMap.put(key, value);
				}
				prevOffset = curOffset;	
				//read key		
				key = readUTFString(readPosition, keyLen);	
				ValidityChecker.checkFileMapKey(key);

				readPosition = (int)dataBaseStorage.getFilePointer() + 5;

			} while (readPosition < initialOffset);
			
			String value = readUTFString(prevOffset, (int)dataBaseStorage.length() - prevOffset);
			
			ValidityChecker.checkFileMapValue(value);
			
			fileMap.put(key, value);
		} finally {
			QuietCloser.closeQuietly(dataBaseStorage);
		}
	}
}