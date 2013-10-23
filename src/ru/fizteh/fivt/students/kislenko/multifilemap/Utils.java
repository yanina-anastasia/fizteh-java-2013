package ru.fizteh.fivt.students.kislenko.multifilemap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class Utils {
    final static private int MAX_FILE_SIZE = 100000000 / 256;

//    static public void readTable(Table table) throws IOException {
//        File tableDir = new File (table.getName());
//        if (tableDir.listFiles() != null) {
//            for (File dir : tableDir.listFiles()) {
//                if (dir.listFiles() != null) {
//                    for (File file : dir.listFiles()) {
//                        RandomAccessFile f = new RandomAccessFile(file, "r");
//                        readFile(table, f);
//                        f.close();
//                    }
//                }
//            }
//        }
//    }

    static public void connectFile(Table table, String key) throws IOException {
        byte nDirectory = getDirNumber(key);
        byte nFile = getFileNumber(key);
        if (!table.isUsing(nDirectory, nFile) && table.getPath().toFile().exists()) {
            File temp = new File(table.getPath().resolve(nDirectory + ".dir").resolve(nFile + ".dat").toString());
            if (temp.exists()) {
                RandomAccessFile file = new RandomAccessFile(temp, "r");
                readFile(table, file);
                file.close();
                table.setUsing(nDirectory, nFile, true);
            }
        }
    }

    static public void readFile(Table table, RandomAccessFile datafile) throws IOException {
        int keyLength;
        int valueLength;
        String key;
        String value;
        if (datafile.length() > MAX_FILE_SIZE) {
            throw new IOException("Too big datafile.");
        }
        while (datafile.getFilePointer() != datafile.length()) {
            keyLength = datafile.readInt();
            if (keyLength < 1 || keyLength > datafile.length() - datafile.getFilePointer() + 4) {
                datafile.close();
                throw new IOException("Incorrect key length in input.");
            }
            valueLength = datafile.readInt();
            if (valueLength < 1 || valueLength > datafile.length() - datafile.getFilePointer() + 4) {
                datafile.close();
                throw new IOException("Incorrect value length in input.");
            }

            byte[] keySymbols = new byte[keyLength];
            byte[] valueSymbols = new byte[valueLength];
            datafile.read(keySymbols);
            datafile.read(valueSymbols);
            key = new String(keySymbols, StandardCharsets.UTF_8);
            value = new String(valueSymbols, StandardCharsets.UTF_8);
            table.put(key, value);
        }
    }

    public static void fillTable(Table table) throws IOException {
        if (table == null) {
            return;
        }
        File[] dirs = new File[16];
        File[][] files = new File[16][16];
        RandomAccessFile[][] database = new RandomAccessFile[16][16];
        for (int i = 0; i < 16; ++i) {
            dirs[i] = table.getPath().resolve(i + ".dir").toFile();
            if (!dirs[i].exists()) {
                dirs[i].mkdir();
            }
            for (int j = 0; j < 16; ++j) {
                files[i][j] = table.getPath().resolve(i + ".dir").resolve(j + ".dat").toFile();
                if (!files[i][j].exists()) {
                    files[i][j].createNewFile();
                }
                database[i][j] = new RandomAccessFile(files[i][j], "rw");
                database[i][j].setLength(0);
            }
        }
        Set<String> keySet = table.getMap().keySet();
        for (String key : keySet) {
            byte b = key.getBytes()[0];
            int dirNumber = b % 16;
            int fileNumber = b / 16 % 16;
            if (database[dirNumber][fileNumber].getFilePointer() > MAX_FILE_SIZE) {
                closeDescriptors(database);
                deleteUnnecessaryFiles(dirs, files);
                throw new IOException("Too big database file.");
            }
            database[dirNumber][fileNumber].writeInt(key.getBytes(StandardCharsets.UTF_8).length);
            database[dirNumber][fileNumber].writeInt(table.get(key).getBytes(StandardCharsets.UTF_8).length);
            database[dirNumber][fileNumber].write(key.getBytes(StandardCharsets.UTF_8));
            database[dirNumber][fileNumber].write(table.get(key).getBytes(StandardCharsets.UTF_8));
        }
        closeDescriptors(database);
        deleteUnnecessaryFiles(dirs, files);
    }

    private static void deleteUnnecessaryFiles(File[] dirs, File[][] files) throws IOException {
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                RandomAccessFile f = new RandomAccessFile(files[i][j], "rw");
                if (f.length() == 0) {
                    f.close();
                    files[i][j].delete();
                }
            }
        }
        for (File dir : dirs) {
            if (dir.listFiles().length == 0) {
                dir.delete();
            }
        }
    }

    private static void closeDescriptors(RandomAccessFile[][] files) throws IOException {
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                files[i][j].close();
            }
        }
    }

    public static byte getDirNumber(String key) {
        return (byte) (key.getBytes()[0] % 16);
    }

    public static byte getFileNumber(String key) {
        return (byte) (key.getBytes()[0] / 16 % 16);
    }
}