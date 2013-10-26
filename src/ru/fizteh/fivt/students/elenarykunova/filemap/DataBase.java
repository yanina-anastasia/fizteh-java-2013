package ru.fizteh.fivt.students.elenarykunova.filemap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class DataBase {

    public HashMap<String, String> data =  new HashMap<String, String>();
    public RandomAccessFile dataFile;
    String filePath = null;
    
    public DataBase(String fileName) {
        String dir = System.getProperty("fizteh.db.dir");
        if (dir == null) {
            System.err.println(dir + ": no directory");
            System.exit(1);
        }
        File tmpDir = new File(dir);
        if (!tmpDir.exists()) {
            System.err.println(dir + ": can't open directory");
            System.exit(1);
        } else if (tmpDir.isDirectory()) {
            filePath = dir + File.separator + fileName;
            File tmpFile = new File(filePath);
            if (!tmpFile.exists()) {
                try {
                    if (!tmpFile.createNewFile()) {
                        System.err.println(filePath + ": can't create file");
                        System.exit(1);
                    }
                } catch (IOException e) {
                    System.err.println(filePath + ": can't create file");
                    System.exit(1);
                }
            }
        } else {
            System.err.println(dir + ": isn't a directory");
            System.exit(1);
        }
        
        try {
            dataFile = new RandomAccessFile(filePath, "r");
            load(dataFile);
            closeDataFile();
        } catch (FileNotFoundException | IllegalArgumentException e2) {
            System.err.println(filePath + ": can't get access to file");
            closeDataFile();
            System.exit(1);
        } 
    }
    
    public void checkOffset(long offset, long currPtr) throws IOException {
        if (offset < currPtr || offset > dataFile.length()) {
            IOException e = new IOException();
            throw e;
        }
    }

    public String getKeyFromFile() throws IOException {
        byte ch = 0;
        Vector<Byte> v = new Vector<Byte>();
        ch = dataFile.readByte();
        while (ch != 0) {
            v.add(ch);
            ch = dataFile.readByte();
        }
        byte[] res = new byte[v.size()];
        for (int i = 0; i < v.size(); i++) {
            res[i] = v.elementAt(i).byteValue();
        }
        String result = new String(res, "UTF-8");       
        return result;
    }
    
    public String getValueFromFile(long nextOffset) throws IOException {
        int beginPtr = (int) dataFile.getFilePointer();
        byte [] res = new byte[(int) (nextOffset - beginPtr)];
        dataFile.read(res);
        String result = new String(res, "UTF-8");
        return result;
    }
    
    public void load(RandomAccessFile dataFile) {
        try {
            if (dataFile.length() == 0) {
                return;
            }
            
            long currPtr = 0;
            long firstOffset = 0;
            long newOffset = 0;
            long nextOffset = 0;
            String keyFirst = "";
            String keySecond = "";
            String value;
            
            dataFile.seek(currPtr);
            keyFirst = getKeyFromFile();
            
            newOffset = dataFile.readInt();
            currPtr = dataFile.getFilePointer();
            checkOffset(newOffset, currPtr);    
            firstOffset = newOffset;
            do {
                dataFile.seek(currPtr);
                if (currPtr < firstOffset) {
                    keySecond = getKeyFromFile();
                    nextOffset = dataFile.readInt();
                    currPtr = dataFile.getFilePointer();
                    checkOffset(nextOffset, currPtr);
                } else if (currPtr == firstOffset) {
                    nextOffset = dataFile.length();
                    currPtr++;
                }
                if (nextOffset < newOffset) {
                    IOException e1 = new IOException();
                    throw e1;
                }
   
                dataFile.seek(newOffset);
                value = getValueFromFile(nextOffset);
                
                data.put(keyFirst, value);
                
                keyFirst = keySecond;
                newOffset = nextOffset;
            } while (currPtr <= firstOffset);              
        } catch (IOException | OutOfMemoryError e) {
            closeDataFile();
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
        curr = str.getBytes("UTF-8").length;
        return curr;
    }
    
    protected void closeDataFile() {
        try {
            if (dataFile != null) {
                dataFile.close();
            }
        } catch (IOException e) {
            System.err.println("can't close file");
            System.exit(1);
        }
    }
    
    public void commitChanges() {
        try {
            dataFile = new RandomAccessFile(filePath, "rw");
            int offset = 0;
            Set<Map.Entry<String, String>> mySet = data.entrySet();
            for (Map.Entry<String, String> myEntry : mySet) {
                try {
                    offset += getLength(myEntry.getKey()) + 1 + 4;
                } catch (UnsupportedEncodingException e) {
                    System.err.println("can't write to file");
                    closeDataFile();
                    System.exit(1);
                }
            }
            int currOffset = offset;
            try {
                dataFile.setLength(0);
                dataFile.seek(0);
                for (Map.Entry<String, String> myEntry : mySet) {
                    dataFile.write(myEntry.getKey().getBytes());
                    dataFile.writeByte(0);
                    dataFile.writeInt(currOffset);
                    currOffset += getLength(myEntry.getValue());
                }
                for (Map.Entry<String, String> myEntry : mySet) {
                    dataFile.write(myEntry.getValue().getBytes());
                }
                closeDataFile();
            } catch (IOException e1) {
                System.err.println("can't write to file");
                closeDataFile();
                System.exit(1);
            }
        } catch (FileNotFoundException | IllegalArgumentException e2) {
            System.err.println("can't get access to file");
            closeDataFile();
            System.exit(1);
        }
    }
}
