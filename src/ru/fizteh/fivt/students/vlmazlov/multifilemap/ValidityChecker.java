package ru.fizteh.fivt.students.vlmazlov.multifilemap;

import java.io.File;

public class ValidityChecker {

	public static final int MAX_KEY_LEN = 1<<20, MIN_KEY_LEN = 1;
	public static final int MAX_VALUE_LEN = 1<<20, MIN_VALUE_LEN = 1;

	public static void checkMultiFileStorageName(File toCheck, int maxValue) throws ValidityCheckFailedException {
		if (!toCheck.getName().matches("\\d\\d?.d((ir)|(at))")) {
			throw new ValidityCheckFailedException(toCheck.getPath() + " is not a valid name");
		}

		String[] tokens = toCheck.getName().split("\\.");
	
		if (Integer.parseInt(tokens[0]) >= maxValue) {
			throw new ValidityCheckFailedException(toCheck.getPath() + " is not a valid name");
		}
	}

	public static void checkKeyStorageAffiliation(String key, int fileNum, int dirNum, int maxFileNum, int maxDirNum)
	throws ValidityCheckFailedException {
		if ((key.getBytes()[0] % maxDirNum != dirNum) || (key.getBytes()[0] / maxFileNum % maxFileNum != fileNum)) {
			throw new ValidityCheckFailedException(key + " is in the wrong storage");
		}
	}

	public static void checkMultiTableRoot(File root) throws ValidityCheckFailedException {
		if (!root.isDirectory()) {
			throw new ValidityCheckFailedException(root.getPath() + " doesn't denote a directory");
		}

		for (File entry : root.listFiles()) {
			if (!entry.isDirectory()) {
				throw new ValidityCheckFailedException("Root directory contains file " + entry.getName()); 
			}
		}
	}

	public static void checkMultiFileMapRoot(File root) throws ValidityCheckFailedException {
		if (!root.isDirectory()) {
			throw new ValidityCheckFailedException(root.getPath() + " doesn't denote a directory");
		}

		for (File directory : root.listFiles()) {
			if (!directory.isDirectory()) {
				throw new ValidityCheckFailedException(root.getPath() + " contains file " + directory.getName()); 
			}

			for (File file : directory.listFiles()) {
				if (!file.isFile()) {
					throw new ValidityCheckFailedException(file.getName() + " doesn't denote a file"); 
				}
			}
		}
	}

	public static void checkFileMapRoot(File root) throws ValidityCheckFailedException {
		if (!root.isDirectory()) {
			throw new ValidityCheckFailedException(root.getPath() + " doesn't denote a directory");
		}
	}

	public static void checkFileMapKey(String key) throws ValidityCheckFailedException {
		if ((key.length() < MIN_KEY_LEN) || (key.length() > MAX_KEY_LEN)) {
			throw new ValidityCheckFailedException(key + " is not a valid key");
		}
	}

	public static void checkFileMapOffset(int offset) throws ValidityCheckFailedException {
		if (offset < 0) {
			throw new ValidityCheckFailedException(offset + " is negative, therefore, not a valid offset");
		}
	}

	public static void checkFileMapValue(String value) throws ValidityCheckFailedException {
		if ((value.length() < MIN_VALUE_LEN) || (value.length() > MAX_VALUE_LEN)) {
			throw new ValidityCheckFailedException(value + " is not a valid value");
		}
	}
}