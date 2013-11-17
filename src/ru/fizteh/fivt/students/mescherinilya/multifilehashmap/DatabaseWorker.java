package ru.fizteh.fivt.students.mescherinilya.multifilehashmap;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class DatabaseWorker {

    static Map<String, String> storage;
    static File currentTable;

    private static void readFile(String dirName, String fileName) throws Exception {

        RandomAccessFile database = new RandomAccessFile(currentTable.getAbsoluteFile()
                + File.separator + dirName + File.separator + fileName, "r");

        int ndirectory = Integer.parseInt(dirName.substring(0, dirName.length() - 4));
        int nfile = Integer.parseInt(fileName.substring(0, fileName.length() - 4));

        if (database.length() == 0) {
            return;
        }

        ArrayList<Integer> offsets = new ArrayList<Integer>();
        ArrayList<String> keys = new ArrayList<String>();

        try {
            do {
                ArrayList<Byte> keySymbols = new ArrayList<Byte>();
                byte b = database.readByte();
                while (b != 0) {
                    keySymbols.add(b);
                    b = database.readByte();
                }
                if (keySymbols.size() == 0) {
                    throw new IncorrectFileFormatException("Empty key was detected.");
                }

                byte[] bytes = new byte[keySymbols.size()];
                for (int i = 0; i < bytes.length; ++i) {
                    bytes[i] = keySymbols.get(i);
                }

                String key = new String(bytes, "UTF-8");
                int hashcode = Math.abs(key.hashCode());
                if (ndirectory != hashcode % 16 || nfile != hashcode / 16 % 16) {
                    throw new IncorrectFileFormatException("Key does not match the file.");
                }

                keys.add(key);

                int offset = database.readInt();
                if (offset <= 0 ||
                        !offsets.isEmpty() && offset <= offsets.get(offsets.size() - 1)) {
                    System.out.println(Integer.toHexString(offset) + " " + Integer.toHexString(offsets.get(offsets.size()-1)));
                    throw new IncorrectFileFormatException("Bad offset value");
                }
                offsets.add(offset);


            } while (database.getFilePointer() != offsets.get(0));
        } catch (EOFException e) {
            throw new IncorrectFileFormatException("Suddenly the end of the file was reached");
        }

        offsets.add((int) database.length());

        ArrayList<String> values = new ArrayList<String>();

        for (int i = 0; i < keys.size(); ++i) {
            byte[] bytes = new byte[offsets.get(i+1) - offsets.get(i)];
            database.read(bytes);
            values.add(new String(bytes, "UTF-8"));
        }

        for (int i = 0; i < keys.size(); ++i) {
            storage.put(keys.get(i), values.get(i));
        }

    }

    static void readDatabase() throws IOException {

        storage = new TreeMap<String, String>();

        for (Integer i = 0; i < 16; ++i) {
            String dirName = i.toString() + ".dir";
            File currentDir = new File(currentTable.getAbsoluteFile() + File.separator + dirName);
            if (!currentDir.exists()) {
                continue;
            }
            if (!currentDir.isDirectory()) {
                throw new IOException("The table is damaged! The file " + dirName + " is not a directory!");
            }
            if (!currentDir.canRead()) {
                throw new IOException("Can't read data from the directory " + dirName + " :(");
            }

            for (Integer j = 0; j < 16; ++j) {
                String fileName = j.toString() + ".dat";
                File currentFile = new File(currentDir.getAbsoluteFile() + File.separator + fileName);
                if (!currentFile.exists()) {
                    continue;
                }
                if (!currentFile.isFile()) {
                    throw new IOException("The table is damaged! The file "
                            + dirName + File.separator + fileName + " is not a normal file!");
                }
                if (!currentFile.canRead()) {
                    throw new IOException("Can't read data from the file " + dirName + File.separator
                            + fileName + " :(");
                }

                try {
                    readFile(dirName, fileName);
                } catch (IncorrectFileFormatException e) {
                    throw new IOException("Bad file format in file " + dirName + File.separator + fileName
                            + ": " + e.getMessage());
                } catch (Exception e) {
                    throw new IOException("Unknown error has occured while reading the file "
                            + dirName + File.separator + fileName);
                }

            }
        }



    }

    static void writeDatabase() throws IOException {

        if (currentTable == null) {
            return;
        }

        ArrayList<TreeMap<String, String>> dirStorage = new ArrayList<TreeMap<String, String>>();
        for (int i = 0; i < 16; ++i) {
            dirStorage.add(new TreeMap<String, String>());
        }

        Set<String> keySet = storage.keySet();
        for (String key : keySet) {
            int hashcode = Math.abs(key.hashCode());
            dirStorage.get(hashcode % 16).put(key, storage.get(key));
        }

        for (Integer i = 0; i < 16; ++i) {

            String dirName = i.toString() + ".dir";
            File currentDir = new File(currentTable.getAbsoluteFile() + File.separator + dirName);
            if (dirStorage.get(i).isEmpty()) {
                if (currentDir.exists() && !currentDir.delete()) {
                    throw new IOException("Couldn't delete the directory " + dirName);
                }
            } else {
                if (currentDir.exists() && !currentDir.isDirectory()) {
                    throw new IOException("Table " + currentTable + " is damaged! The file " + dirName
                            + " is not a directory!");
                }
                if (!currentDir.exists() && !currentDir.mkdir()) {
                    throw new IOException("Couldn't create the directory " + dirName);
                }

                ArrayList<TreeMap<String, String>> fileStorage = new ArrayList<TreeMap<String, String>>();
                for (int j = 0; j < 16; ++j) {
                    fileStorage.add(new TreeMap<String, String>());
                }

                keySet = dirStorage.get(i).keySet();
                for (String key : keySet) {
                    int hashcode = Math.abs(key.hashCode());
                    fileStorage.get(hashcode / 16 % 16).put(key, dirStorage.get(i).get(key));
                }

                for (Integer j = 0; j < 16; ++j) {
                    String fileName = j.toString() + ".dat";
                    File currentFile = new File(currentDir.getAbsoluteFile() + File.separator + fileName);

                    if (fileStorage.get(j).isEmpty()) {
                        if (currentFile.exists() && !currentFile.delete()) {
                            throw new IOException("Couldn't delete the file " + dirName + File.separator + fileName);
                        }
                    } else {
                        if (currentFile.exists() && !currentFile.isFile()) {
                            throw new IOException("Table " + currentTable + " is damaged! The file "
                                    + dirName + File.separator + fileName + " is not a normal file!");
                        }
                        if (!currentFile.exists() && !currentFile.createNewFile()) {
                            throw new IOException("Couldn't create the file " + dirName + File.separator + fileName);
                        }

                        RandomAccessFile file = null;
                        try {
                            file = new RandomAccessFile(currentFile, "rw");

                            file.setLength(0);

                            int offset = 0;

                            keySet = fileStorage.get(j).keySet();
                            for (String key : keySet) {
                                offset += key.getBytes(StandardCharsets.UTF_8).length + 5;
                            }

                            ArrayList<String> values = new ArrayList<String>();
                            for (String key : keySet) {
                                file.write(key.getBytes(StandardCharsets.UTF_8));
                                file.write('\0');
                                file.writeInt(offset);
                                String value = fileStorage.get(j).get(key);
                                values.add(value);
                                offset += value.getBytes(StandardCharsets.UTF_8).length;
                            }

                            for (String value : values) {
                                file.write(value.getBytes(StandardCharsets.UTF_8));
                            }


                        } catch (Exception e) {
                            throw new IOException("Couldn't write to the file " + dirName + File.separator + fileName);
                        } finally {
                            if (file != null) {
                                file.close();
                            }
                        }
                    }
                }
            }
        }

    }


}
