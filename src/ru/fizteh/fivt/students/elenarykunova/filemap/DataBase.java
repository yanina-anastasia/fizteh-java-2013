package ru.fizteh.fivt.students.elenarykunova.filemap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DataBase {

    public HashMap<String, String> data =  new HashMap<String, String>();
    public RandomAccessFile dataFile;
    
    public DataBase(String fileName) {
        String dir = System.getProperty("fizteh.db.dir");
        String filePath = null;
        File tmpDir = new File(dir);
        if (!tmpDir.exists()) {
            System.err.println("can't open directory");
            System.exit(1);
        } else if (tmpDir.isDirectory()) {
            filePath = dir + File.separator + fileName;
            File tmpFile = new File(filePath);
            if (!tmpFile.exists()) {
                try {
                    tmpFile.createNewFile();
                } catch (IOException e) {
                    System.err.println("can't create file");
                    System.exit(1);
                }
            }
        } else {
            filePath = dir;
        }
        
        try {
            dataFile = new RandomAccessFile(filePath, "rw");
            load(dataFile);
        } catch (FileNotFoundException e) {
            System.err.println("can't access file");
            System.exit(1);
        }
    }
    
    public void load(RandomAccessFile dataFile) {
        try {
            if (dataFile.length() == 0) {
                return;
            }
            long currPtr = 0;
            long firstOffset = 0;
            long newOffset = 0;
            boolean firstTime = true;
            String key = null;
            String value = null;
                        
            while (currPtr < firstOffset || firstTime) {
                dataFile.seek(currPtr);
                key = dataFile.readUTF();
                dataFile.readByte();
                
                newOffset = dataFile.readInt();
                if (firstTime) {
                    firstOffset = newOffset;
                    firstTime = false;
                }
                currPtr = dataFile.getFilePointer();

                dataFile.seek(newOffset);
                value = dataFile.readUTF();
                data.put(key, value);
            }
        } catch (IOException e) {
            System.err.println("can't read values from file");
            System.exit(1);
        }
    }
    
    public String put(String key, String value) {
        return data.put(key, value);
    }
    
    public String get(String key) {
        return data.get(key);
    }
    
    public String remove(String key) {
        return data.remove(key);
    }
    
    private int getLength(String str) throws UnsupportedEncodingException {
        int curr = 0;
        curr = str.getBytes("UTF-8").length + 2;
        return curr;
    }
    
    protected void closeDataFile() {
        try {
            dataFile.close();
        } catch (IOException e) {
            System.err.println("can't close file");
            System.exit(1);
        }
    }
    public void commitChanges() {
        int offset = 0;
        Set<Map.Entry<String, String>> mySet = data.entrySet();
        for (Map.Entry<String, String> myEntry : mySet) {
            try {
                offset += getLength(myEntry.getKey()) + 1 + 4;
            } catch (UnsupportedEncodingException e) {
                System.err.println("can't write to file");
                closeDataFile();
            }
        }
        int currOffset = offset;
        try {
            dataFile.setLength(0);
            dataFile.seek(0);
            for (Map.Entry<String, String> myEntry : mySet) {
                dataFile.writeUTF(myEntry.getKey());
                dataFile.writeByte(0);
                dataFile.writeInt(currOffset);
                currOffset += getLength(myEntry.getValue());
            }
            for (Map.Entry<String, String> myEntry : mySet) {
                dataFile.writeUTF(myEntry.getValue());
            }
            closeDataFile();
        } catch (IOException e1) {
            System.err.println("can't write to file");
            closeDataFile();
            System.exit(1);
        }
    }
}
