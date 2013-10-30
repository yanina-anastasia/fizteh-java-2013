package ru.fizteh.fivt.students.dzvonarev.multifilemap;

import ru.fizteh.fivt.students.dzvonarev.shell.CommandInterface;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Exit implements CommandInterface {

    public static void writeInFile(String path, String key, String value) throws IOException {
        RandomAccessFile fileWriter = MultiFileMap.openFileForWrite(path);
        fileWriter.skipBytes((int) fileWriter.length());
        System.out.println("write in file " + path + " " + key + " " + value);
        try {
            if (key == null || value == null) {
                MultiFileMap.closeFile(fileWriter);
                throw new IOException("updating file: error in writing");
            }
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            byte[] valueBytes = value.getBytes(StandardCharsets.UTF_8);
            fileWriter.writeInt(keyBytes.length);
            fileWriter.writeInt(valueBytes.length);
            fileWriter.write(keyBytes);
            fileWriter.write(valueBytes);
        } catch (IOException e) {
            throw new IOException("updating file: error in writing");
        } finally {
            MultiFileMap.closeFile(fileWriter);
        }
    }

    public static void writeMap(HashMap<String, HashMap<String, String>> map, String table) throws IOException {
        HashMap<String, String> fileMap = map.get(table);
        if (fileMap == null) {
            return;
        } else {
            if (fileMap.isEmpty()) {
                return;
            }
        }
        Set fileSet = fileMap.entrySet();
        Iterator<Map.Entry<String, String>> i = fileSet.iterator();
        while (i.hasNext()) {
            Map.Entry<String, String> currItem = i.next();
            String key = currItem.getKey();
            String value = currItem.getValue();
            char b = key.charAt(0);
            int nDirectory = Math.abs(b) % 16;
            int nFile = Math.abs(b) / 16 % 16;
            String rightDir = Integer.toString(nDirectory) + ".dir";
            String rightFile = Integer.toString(nFile) + ".dat";
            String path = System.getProperty("fizteh.db.dir") + File.separator + table +
                    File.separator + rightDir + File.separator + rightFile;
            String dir = System.getProperty("fizteh.db.dir") + File.separator + table +
                    File.separator + rightDir;
            File file = new File(path);
            File fileDir = new File(dir);
            if (!fileDir.exists()) {
                if (!fileDir.mkdir()) {
                    throw new IOException("can't create directory " + dir);
                }
            }
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    throw new IOException("can't create file " + path);
                }
            }
            writeInFile(path, key, value);
        }
    }

    public void execute(Vector<String> args) throws IOException {
        HashMap<String, HashMap<String, String>> myMap = MultiFileMap.getMultiFileMap();
        MultiFileMap.printMultiMap();
        File dir = new File(System.getProperty("fizteh.db.dir"));
        String[] file = dir.list();
        if (file != null) {
            if (file.length != 0) {
                for (String currFile : file) {
                    if (new File(System.getProperty("fizteh.db.dir") + File.separator + currFile).isFile()) {
                        continue;
                    }
                    if (new File(System.getProperty("fizteh.db.dir") + File.separator + currFile).isDirectory() &&
                            !(new File(System.getProperty("fizteh.db.dir") + File.separator + currFile)).isHidden()) {
                        writeMap(myMap, currFile);
                    }
                }
            }
        }
        System.exit(0);
    }

}
