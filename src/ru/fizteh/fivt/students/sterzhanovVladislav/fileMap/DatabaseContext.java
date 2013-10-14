package ru.fizteh.fivt.students.sterzhanovVladislav.fileMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.nio.ByteBuffer;

public class DatabaseContext {
    
    private HashMap<String, String> dataBase = null;
    private File dbFile; 
    private Path dbDir;

    public String remove(String key) throws IOException {
        String removed = dataBase.remove(key);
        writeOut(dataBase, dbFile);
        return removed;
    }

    public String get(String key) {
        return dataBase.get(key);
    }

    public String put(String key, String value) throws IOException {
        String previousValue = dataBase.put(key, value);
        writeOut(dataBase, dbFile);
        return previousValue;
    }

    public DatabaseContext(Path path) throws Exception {
        dbDir = path;
        if (!dbDir.toFile().isDirectory()) {
            throw new Exception("fizteh.db.dir is not a directory!");
        }
        dbFile = new File(dbDir.toString(), "/db.bat");
        if (!dbFile.exists()) {
            dbFile.createNewFile();
        }
        dataBase = parseDatabase(dbFile);
    } 
    
    private static HashMap<String, String> parseDatabase(File dbFile) 
            throws FileNotFoundException, IOException, Exception {
        HashMap<String, String> map = new HashMap<String, String>();
        FileInputStream fstream = new FileInputStream(dbFile);
        try {
            while (fstream.available() > 4) {
                String key = parseString(fstream);
                String value = parseString(fstream);
                map.put(key, value);
            }
        } finally {
            fstream.close();
        }
        return map;
    }
    
    private static String parseString(FileInputStream fstream) throws Exception {
        if (fstream.available() < 4) {
            throw new Exception("Error: malformed database");
        }
        byte[] sizeBuf = new byte[4];
        fstream.read(sizeBuf);
        int size = ByteBuffer.wrap(sizeBuf).getInt();
        if (fstream.available() < size) {
            throw new Exception("Error: malformed database");
        }
        byte[] stringBuf = new byte[size];
        fstream.read(stringBuf);
        return new String(stringBuf, "UTF-8");
    }
    
    private static void writeOut(HashMap<String, String> map, File dbFile) throws IOException {
        if (dbFile.exists()) {
            dbFile.delete();
        }
        FileOutputStream fstream = new FileOutputStream(dbFile);
        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                writeString(entry.getKey(), fstream);
                writeString(entry.getValue(), fstream);
            } 
        } finally {
            fstream.close();
        }
    }
    
    private static void writeString(String s, FileOutputStream fstream) throws IOException {
        byte[] buf = s.getBytes("UTF-8");
        fstream.write(ByteBuffer.allocate(4).putInt(buf.length).array());
        fstream.write(buf);
    }
}
