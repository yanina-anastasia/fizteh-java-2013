package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MultiFileMapUtils {
    public static boolean save(MultiDBState myState) {
        if (myState.table == null) {
            return true;
        }
        if (myState.curTableName == "") {
            return true;
        }
        File tablePath = new File(System.getProperty("fizteh.db.dir"), myState.curTableName);
        for (int i = 0; i < 16; i++) {
            String directoryName = String.format("%d.dir", i);
            File path = new File(tablePath, directoryName);
            boolean isDirEmpty = true;
            ArrayList<HashSet<String>> keys = new ArrayList<HashSet<String>>(16);
            for (int j = 0; j < 16; j++) {
                keys.add(new HashSet<String>());
            }
            for (String step : myState.table.keySet()) {
                int nDirectory = getDirectoryNum(step);
                if (nDirectory == i) {
                    int nFile = getFileNum(step);
                    keys.get(nFile).add(step);
                    isDirEmpty = false;
                }
            }

            if (isDirEmpty) {
                continue;
            }

            path.mkdir();
            for (int j = 0; j < 16; j++) {
                File filePath = new File(path, (j + ".dat").toString());
                try {
                    saveTable(myState, keys.get(j), filePath.toString());
                } catch (IOException e) {
                    System.out.println("ERROR SAD");
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean saveTable(MultiDBState myState, Set<String> keys, String path) throws IOException {
        if (keys.isEmpty()) {
            return false;
        }
        RandomAccessFile temp = new RandomAccessFile(path, "rw");
        try {
            long offset = 0;

            temp.setLength(0);
            for (String step : keys) {
                offset += step.getBytes(StandardCharsets.UTF_8).length + 5;
            }
            for (String step : keys) {
                byte[] bytesToWrite = step.getBytes(StandardCharsets.UTF_8);
                temp.write(bytesToWrite);
                temp.writeByte(0);
                temp.writeInt((int) offset);
                offset += myState.table.get(step).getBytes(StandardCharsets.UTF_8).length;
            }
            for (String key : keys) {
                String value = myState.table.get(key);
                temp.write(value.getBytes(StandardCharsets.UTF_8));
            }
            if (temp.length() == 0) {
                new File(path).delete();
            }
        } catch (IOException e) {
            System.err.println("Error while writing file");
            return false;
        } finally {
            try {
                temp.close();
            } catch (IOException t) {
                System.err.println("Error while closing file");
                System.exit(1);
            }
        }
        return true;
    }

    public static int getDirectoryNum(String key) {
        int keyByte = Math.abs(key.getBytes(StandardCharsets.UTF_8)[0]);
        return keyByte % 16;
    }

    public static int getFileNum(String key) {
        int keyByte = Math.abs(key.getBytes(StandardCharsets.UTF_8)[0]);
        return (keyByte / 16) % 16;
    }
}
