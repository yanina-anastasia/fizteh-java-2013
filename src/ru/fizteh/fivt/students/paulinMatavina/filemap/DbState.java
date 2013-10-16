package ru.fizteh.fivt.students.paulinMatavina.filemap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class DbState extends State{
    public HashMap<String, String> data;
    public RandomAccessFile dbFile;
    
    public DbState() {
        String path = System.getProperty("fizteh.db.dir") + File.separator + "db.dat";
        commands = new HashMap<String, Command>();
        File dbTempFile = new File(path);
        if (!dbTempFile.exists()) {
            System.err.println("filemap: database file does not exist, trying to create");
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
        data = new HashMap<String, String>();
        try {
            loadData();
        } catch (IOException e) {
            System.err.println("filemap: data loading error");
            System.exit(1);
        }
    }
    
    private void loadData() throws IOException {
        if (dbFile.length() == 0) {
                return;
        }
        
        long currentOffset;
        long firstOffset = 0;
        long pos = 0;
        String key = null;
        String value = null;
        do {
            dbFile.seek(pos);
            key = dbFile.readUTF();
            dbFile.readChar();
            currentOffset = dbFile.readInt();
            if (firstOffset == 0) {
                firstOffset = currentOffset;
            }
            pos = dbFile.getFilePointer();
            dbFile.seek(currentOffset);
            value = dbFile.readUTF();
            data.put(key, value);
        } while (pos < firstOffset);
    }

    public void commit() throws IOException {
        int offset = 0;
        long pos = 0;
        
        Set<String> keys = data.keySet();
        for (String s: keys) {
                offset += s.getBytes("UTF-8").length + 8;
        }
        
        for (Map.Entry<String, String> s: data.entrySet()) {
                dbFile.seek(pos);
                dbFile.writeUTF(s.getKey());
                dbFile.writeChar('\0');
                dbFile.writeInt(offset);
                pos = dbFile.getFilePointer();
                dbFile.seek(offset);
                dbFile.writeUTF(s.getValue());
                offset = (int) dbFile.getFilePointer();
        }
    }
}
