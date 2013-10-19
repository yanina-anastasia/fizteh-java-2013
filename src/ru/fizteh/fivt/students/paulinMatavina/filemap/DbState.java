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

public class DbState extends State{
    public HashMap<String, String> data;
    public RandomAccessFile dbFile;
    private String path;
    
    public DbState() {
        path = System.getProperty("fizteh.db.dir") + File.separator + "db.dat";
        commands = new HashMap<String, Command>();
        fileCheck();
        data = new HashMap<String, String>();
        try {
            loadData();
        } catch (IOException e) {
            System.err.println("filemap: data loading error");
            System.exit(1);
        }
    }
    
    @Override
    public void exitWithError(int errCode) {
        try {
            commit();
        } catch (IOException e) {
            System.out.println("filemap: error while writing data to the disk");
            errCode = 1;
        } finally {
            try {
                dbFile.close();
            } catch (IOException e) {
                System.err.println("filemap: error in file closing");
                System.exit(1);
            }
        }
        System.exit(errCode);
    }
    
    private void fileCheck() {
        File dbTempFile = new File(path);
        if (!dbTempFile.exists()) {
            try {
                dbTempFile.createNewFile();
            } catch (IOException e) {
                System.err.println("filemap: unable to create a database file");
                System.exit(1);
            }
        }
        try {
            dbFile = new RandomAccessFile(path, "rw");
        } catch (FileNotFoundException e) {
            System.err.println("filemap: database file does not exist");
            System.exit(1);
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
    
    private void loadData() throws IOException {
        if (dbFile.length() == 0) {
                return;
        } 
        
        int position = 0;
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
            data.put(key, value);
            key = key2;
            startOffset = endOffset;
        } while (position <= firstOffset); 
    }

    public void commit() throws IOException {
        fileCheck();
        int offset = 0;
        long pos = 0;
        
        for (String s : data.keySet()) {
            offset += s.getBytes("UTF-8").length + 5;
        }
        
        for (Map.Entry<String, String> s : data.entrySet()) {
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
}
