package ru.fizteh.fivt.students.dobrinevski.multiFileHashMap;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.HashMap;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.nio.ByteBuffer;
import java.io.FileInputStream;



public class MyHashMap {
    public File curTable = null;
    private HashMap<Integer, HashMap<String, String> > dataBase = null;

    MyHashMap() {
    dataBase = new HashMap<Integer, HashMap<String, String> >();
        for(int i = 0; i < 256; i++) {
            dataBase.put(i, new HashMap<String, String>());
        }
    }

    private void parseFile(File dbFile, int firstControlValue, int secondControlValue) throws Exception {
        FileInputStream fstream = new FileInputStream(dbFile);
        while (fstream.available() > 0) {
            Map.Entry<String, String> newEntry = parseEntry(fstream);
            Integer hashCode = newEntry.getKey().hashCode();
            Integer nDirectory = hashCode % 16;
            Integer nFile = hashCode / 16 % 16;
            if(firstControlValue != nDirectory || secondControlValue != nFile) {
                throw new Exception("Error: bad file");
            }
            dataBase.get(nDirectory * 16 + nFile).put(newEntry.getKey(), newEntry.getValue());
        }
        dbFile.delete();
    }

    private static Map.Entry<String, String> parseEntry(FileInputStream fstream) throws Exception {
        byte[] Buf = new byte[4];
        Read(fstream, Buf, 4);
        int keySize = ByteBuffer.wrap(Buf).getInt();
        Read(fstream, Buf, 4);
        int valueSize = ByteBuffer.wrap(Buf).getInt();
        if (keySize <= 0 || valueSize <= 0 || keySize > (1 << 24) || valueSize > (1 << 24)) {
            throw new Exception("Error: bad file");
        }
        byte[] kBuf = new byte[keySize];
        Read(fstream, kBuf, keySize);
        byte[] vBuf = new byte[valueSize];
        Read(fstream, vBuf, valueSize);
        return new AbstractMap.SimpleEntry<String, String>(new String(kBuf, "UTF-8"), new String(vBuf, "UTF-8"));
    }

    private static void Read(FileInputStream fstream, byte[] buf, int readCount) throws Exception {
        int readed = 0;
        if (readCount < 0) {
            throw new Exception("Error: bad file");
        }
        while (readed < readCount) {
            int readNow = fstream.read(buf, readed, readCount - readed);
            if (readNow < 0) {
                throw new Exception("Error: bad file");
            }
            readed += readNow;
        }
    }

    public void use(String args) throws Exception {
        Path dbsDir = Paths.get(System.getProperty("fizteh.db.dir")).resolve(args).normalize();
        if(Files.notExists(dbsDir) || !Files.isDirectory(dbsDir)) {
            System.out.println("tablename not exists");
            return;
        }
        if(curTable != null) {
            writeOut();
        }
        curTable = new File(System.getProperty("fizteh.db.dir") + File.separator + args);
        System.out.println("using " + args);
    }

    public void get(String args) throws Exception {
        if(curTable == null) {
            System.out.println("no table");
            return;
        }
        Integer hashCode = args.hashCode();
        Integer nDirectory = hashCode % 16;
        Integer nFile = hashCode / 16 % 16;

        File dbFile = new File(curTable.getCanonicalPath() + File.separator + nDirectory.toString()
                + File.separator + nFile.toString() + ".dat");
        if(dbFile.exists() && dbFile.isFile()) {
            parseFile(dbFile, nDirectory, nFile);
        }
        String Value = dataBase.get(nDirectory * 16 + nFile).get(args);
        if(Value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(Value);
        }

    }

    public void put(String[] args) throws  Exception {
        if(curTable == null) {
            System.out.println("no table");
            return;
        }
        Integer hashCode = args[1].hashCode();
        Integer nDirectory = hashCode % 16;
        Integer nFile = hashCode / 16 % 16;

        File dbFile = new File(curTable.getCanonicalPath() + File.separator + nDirectory.toString() + ".dir"
                + File.separator + nFile.toString() + ".dat");
        if(dbFile.exists() && dbFile.isFile()) {
            parseFile(dbFile, nDirectory, nFile);
        }
        String value = dataBase.get(nDirectory * 16 + nFile).put(args[1], args[2]);
        System.out.println(value == null ? args[2] : "overwrite\n" + value);

    }

    public void remove(String args) throws  Exception {
        if(curTable == null) {
            System.out.println("no table");
            return;
        }
        Integer hashCode = args.hashCode();
        Integer nDirectory = hashCode % 16;
        Integer nFile = hashCode / 16 % 16;

        File dbFile = new File(curTable.getCanonicalPath() + File.separator + nDirectory.toString() + ".dir"
                + File.separator + nFile.toString() + ".dat");
        if(dbFile.exists() && dbFile.isFile()) {
            parseFile(dbFile, nDirectory, nFile);
        }
        if(dataBase.get(nDirectory * 16 + nFile).remove(args) == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }

    public void writeOut() throws IOException {
        for(Integer i = 0; i < 16; i++ ) {
            for(Integer j = 0; j < 16; j++) {
                if(!dataBase.get(i * 16 + j).isEmpty()) {
                    String way = curTable.getCanonicalPath()
                            + File.separator + i.toString();
                    File workFile = new File(way);
                    if(!workFile.exists()) {
                        workFile.mkdir();
                    }
                    File workFile2 = new File(way + File.separator + j.toString() + ".dat");
                    FileOutputStream fstream = new FileOutputStream(workFile2);
                    for (Map.Entry<String, String> entry : dataBase.get(16 * i + j).entrySet()) {
                        writeEntry(entry, fstream);
                    }
                    dataBase.get(i * 16 + j).clear();
                }
            }
        }
    }

    public void exit() throws IOException {
             writeOut();
    }

    private static void writeEntry(Map.Entry<String, String> e, FileOutputStream fstream) throws IOException {
        byte[] keyBuf = e.getKey().getBytes("UTF-8");
        byte[] valueBuf = e.getValue().getBytes("UTF-8");
        fstream.write(ByteBuffer.allocate(4).putInt(keyBuf.length).array());
        fstream.write(ByteBuffer.allocate(4).putInt(valueBuf.length).array());
        fstream.write(keyBuf);
        fstream.write(valueBuf);
    }
}
