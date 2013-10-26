package ru.fizteh.fivt.students.baldindima.filemap;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;



public class DataBaseFile {
	protected final Map<String, String> currentTable = new HashMap<String, String>();
    protected File dataBaseFile;
    protected String fileName;
    private int fileNumber;
    private int directoryNumber;
	public DataBaseFile(String fullName, int newDirectoryNumber, int newFileNumber) throws IOException {
		fileName = fullName;
		
        dataBaseFile = new File(fileName);
        try {
            if (!dataBaseFile.exists()) {
                if (!dataBaseFile.createNewFile()) {
                    throw new IOException("Cannot create " + fileName);
                }
            }
        } catch (IOException e) {
        	throw new IOException("Open file error! " + e.getMessage());
        }
        fileNumber = newFileNumber;
        directoryNumber = newDirectoryNumber;
        read();
        check();
		
	}
	public boolean check() throws IOException {
		for (Map.Entry<String, String> curPair : getCurrentTable().entrySet()) {
            if (!((Math.abs(curPair.getKey().getBytes("UTF-8")[0]) % 16) == directoryNumber) 
            		&& ((Math.abs(curPair.getKey().getBytes("UTF-8")[0] / 16) % 16 == fileNumber))) {
                throw new IOException("Wrong file format key[0] =  " 
            		+ String.valueOf(Math.abs(curPair.getKey().getBytes("UTF-8")[0]))
                        + " in file " + fileName);
            }
        }
        return true;
    }

	public Map<String, String> getCurrentTable() {
        return currentTable;
    }

    

   

    public void read() throws IOException {
   //     open(shell, fileFunctions);
        RandomAccessFile randomDataBaseFile = new RandomAccessFile(fileName, "rw");
    	if (randomDataBaseFile.length() == 0) {
    		randomDataBaseFile.close();
            return;
        }

        while (randomDataBaseFile.getFilePointer() < randomDataBaseFile.length() - 1) {
            int keyLength = randomDataBaseFile.readInt();
            int valueLength = randomDataBaseFile.readInt();
            if ((keyLength <= 0) || (valueLength <= 0)){
            	randomDataBaseFile.close();
            	throw new IOException("wrong format");
            }
                
            byte[] key;
            byte[] value;
            try {
                key = new byte[keyLength];
                value = new byte[valueLength];
            } catch (OutOfMemoryError e) {
            	randomDataBaseFile.close();
                throw new IOException("too large key or value");
            }
            randomDataBaseFile.read(key);
            randomDataBaseFile.read(value);
            String keyString = new String(key, "UTF-8");
            String valueString = new String(value, "UTF-8");
            currentTable.put(keyString, valueString);
        }
        randomDataBaseFile.close();

    }

    public void write() throws IOException {
    	RandomAccessFile randomDataBaseFile = new RandomAccessFile(fileName, "rw");
    	randomDataBaseFile.getChannel().truncate(0);
        for (Map.Entry<String, String> curPair : getCurrentTable().entrySet()) {
            randomDataBaseFile.writeInt(curPair.getKey().getBytes("UTF-8").length);
            randomDataBaseFile.writeInt(curPair.getValue().getBytes("UTF-8").length);
            randomDataBaseFile.write(curPair.getKey().getBytes("UTF-8"));
            randomDataBaseFile.write(curPair.getValue().getBytes("UTF-8"));
        }
        randomDataBaseFile.close();
    }

    public String put(final String keyString, final String valueString) {
        return getCurrentTable().put(keyString, valueString);

    }

    public String get(final String keyString) {
        return getCurrentTable().get(keyString);
    }

    public String remove(final String keyString) {
        return getCurrentTable().remove(keyString);
    }


}
