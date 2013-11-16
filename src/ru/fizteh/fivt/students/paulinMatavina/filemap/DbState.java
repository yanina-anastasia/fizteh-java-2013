package ru.fizteh.fivt.students.paulinMatavina.filemap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class DbState extends State {
    public HashMap<String, String> data;
    public HashMap<String, String> initial;
    public RandomAccessFile dbFile;
    public String path;
    private int foldNum;
    private int fileNum;
    
    public DbState(String dbPath, int folder, int file) {
        foldNum = folder;
        fileNum = file;
        path = dbPath;
        try {
            loadData();
        } catch (IOException e) {
            System.err.println("filemap: data loading error");
            throw new DbExitException(1);
        }
    }
    
    private void fileCheck() {
        File dbTempFile = new File(path);
        if (!dbTempFile.exists()) {
            try {
                dbTempFile.createNewFile();
            } catch (IOException e) {
                System.err.println("filemap: unable to create a database file");
                throw new DbExitException(1);
            }
        }
        try {
            dbFile = new RandomAccessFile(path, "rw");
        } catch (FileNotFoundException e) {
            System.err.println("filemap: database file does not exist " + path);
            throw new DbExitException(1);
        }
        return;
    }
    
    private String byteVectToStr(Vector<Byte> byteVect) throws IOException {
        byte[] byteKeyArr = new byte[byteVect.size()];
        for (int i = 0; i < byteVect.size(); ++i) {
            byteKeyArr[i] = byteVect.elementAt(i);
        }
        
        try {
            return new String(byteKeyArr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.err.println("filemap: UTF-8 is unsupported by system");
            System.exit(1);
        }
        return "";
    }
    
    private String getKeyFromFile(int offset) throws IOException {
        dbFile.seek(offset);
        byte tempByte = dbFile.readByte();
        Vector<Byte> byteVect = new Vector<Byte>();
        while (tempByte != '\0') {  
            byteVect.add(tempByte);
            tempByte = dbFile.readByte();
        }        
        
        return byteVectToStr(byteVect);
    }
    
    private String getValueFromFile(int offset, int endOffset) throws IOException {
        if (offset < 0 || endOffset < 0) {
            System.err.println("filemap: reading database: wrong file format");
            System.exit(1);
        }
        dbFile.seek(offset);
        byte tempByte;
        Vector<Byte> byteVect = new Vector<Byte>();
        while (dbFile.getFilePointer() < dbFile.length()
                    && (int) dbFile.getFilePointer() < endOffset) { 
            tempByte = dbFile.readByte();
            byteVect.add(tempByte);
        }        
        
        return byteVectToStr(byteVect);
    }
    
    public int loadData() throws IOException {
        data = new HashMap<String, String>();
        initial = new HashMap<String, String>();
        dbFile = null;
        int result = 0;
        int position = 0;
        try {
            fileCheck();
            if (dbFile.length() == 0) {
                dbFile.close();
                return 0;
            } 
            
            String key = getKeyFromFile(position);
            int startOffset = dbFile.readInt();
            int endOffset = 0;
            int firstOffset = startOffset;
            String value = "";
            String key2 = "";
            
            do {  
                position += key.getBytes().length + 5;
                if (position < firstOffset) {   
                    key2 = getKeyFromFile(position);
                    endOffset = dbFile.readInt();
                    value = getValueFromFile(startOffset, endOffset);
                    
                } else {
                    value = getValueFromFile(startOffset, (int) dbFile.length());
                }
                
                if (key.getBytes().length > 0) {
                    if (getFolderNum(key) != foldNum || getFileNum(key) != fileNum) {
                        throw new IOException("wrong key in file");
                    }
                    result++;
                    data.put(key, value);
                }
                
                key = key2;
                startOffset = endOffset;
            } while (position <= firstOffset); 
            
            initial = new HashMap<String, String>(data);
        } finally {
          if (dbFile != null) {
            try {
              dbFile.close();
            } catch (Throwable e) {
              // ignore
            }
          }
        }
        
        return result;
    }
    
    public int getChangeNum() {
        int result = 0;
        for (Map.Entry<String, String> s : data.entrySet()) {
            String was = initial.get(s.getKey());
            String became = s.getValue();
            if ((was != null && !was.equals(became)) 
                    || (was == null && became != null)) {
                result++;
            }
        }
        return result;
    }

    public void commit() throws IOException {
        initial = new HashMap<String, String>(data);
        dbFile = null;
        try {
            fileCheck();
            int offset = 0;
            long pos = 0;
            for (Map.Entry<String, String> s : data.entrySet()) {
                if (s.getValue() != null) {
                    offset += s.getKey().getBytes("UTF-8").length + 5;
                } 
            }
            for (Map.Entry<String, String> s : data.entrySet()) {
                if (s.getValue() != null) {
                    dbFile.seek(pos);
                    dbFile.write(s.getKey().getBytes("UTF-8"));
                    dbFile.write("\0".getBytes("UTF-8"));
                    dbFile.writeInt(offset);
                    pos = (int) dbFile.getFilePointer();
                    dbFile.seek(offset);
                    byte[] value = s.getValue().getBytes("UTF-8");
                    dbFile.write(value);
                    offset += value.length;
                }
            }
        } finally {
            if (dbFile != null) {
                try {
                  dbFile.close();
                } catch (Throwable e) {
                  // ignore
                }
            }
        }
    }
    
    public int getFolderNum(String key) {
        return (Math.abs(key.getBytes()[0]) % 16);
    }
    
    public int getFileNum(String key) {
        return ((Math.abs(key.getBytes()[0]) / 16) % 16);
    }
    
    public String put(String[] args) {
        String key = args[0];
        String value = args[1];
        return data.put(key, value);
    }
    
    public String get(String[] args) {
        String key = args[0];
        if (data.containsKey(key)) {
            return data.get(key);
        } else {
            return null;
        }
    }
    
    public String remove(String[] args) {
        String key = args[0];
        String value = data.get(key);
        data.put(key, null);
        return value;
    }
    
    public int size() {
        int result = 0;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (entry.getValue() != null) {
                result++;
            }
        }
        return result;
    }
}
