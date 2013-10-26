package ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap;

import ru.fizteh.fivt.students.nadezhdakaratsapova.filemap.DataTable;
import ru.fizteh.fivt.students.nadezhdakaratsapova.filemap.FileReader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DataLoader {
    public static final int DIR_COUNT = 16;
    public static final int FILE_COUNT = 16;

    public void load(MultiFileHashMapProvider state) throws IOException, IllegalArgumentException {
        if (state.getNextTable() == null) {
            System.out.println("no table");
        } else {
            boolean loadFlag = false;
            if (state.getCurTable() == null) {
                loadFlag = true;
            } else {
                if (!state.getCurTable().getCanonicalFile().equals(state.getNextTable())) {
                    loadFlag = true;
                }
            }
            if (loadFlag) {
                DataWriter dataWriter = new DataWriter();
                dataWriter.writeData(state);
                state.setCurTable(state.getNextTable());
                state.dataStorage = new DataTable(state.getCurTable().getName());

                File[] dirs = state.getCurTable().listFiles();
                if (dirs.length > DIR_COUNT) {
                    throw new IOException("The table includes more than " + DIR_COUNT + " directories");
                }
                for (File d : dirs) {
                    if (!d.isDirectory()) {
                        throw new IOException(state.getCurTable().getName() + " should include only directories");
                    }
                    File[] files = d.listFiles();
                    if (files.length > FILE_COUNT) {
                        throw new IOException("The directory includes more than " + FILE_COUNT + " files");
                    }
                    String dirName = d.getName();
                    char firstChar = dirName.charAt(0);
                    char secondChar;
                    int dirNumber;
                    if (dirName.length() > 1) {
                        secondChar = dirName.charAt(1);
                    } else {
                        throw new IllegalArgumentException("Not allowed name of directory in table");
                    }
                    if (Character.isDigit(firstChar)) {
                        if (Character.isDigit(secondChar)) {
                            dirNumber = Integer.parseInt(dirName.substring(0, 2));
                        } else {
                            dirNumber = Integer.parseInt(dirName.substring(0, 1));
                        }
                    } else {
                        throw new IllegalArgumentException("Not allowed name of directory in table");
                    }
                    if (!dirName.equals(new String(dirNumber + ".dir"))) {
                        throw new IllegalArgumentException("Not allowed name of directory in table");
                    }
                    for (File f : files) {
                        if (!f.isFile()) {
                            throw new IOException("Unexpected directory");
                        }
                        String fileName = f.getName();
                        char firstFileChar = fileName.charAt(0);
                        char secondFileChar;
                        int fileNumber;
                        if (fileName.length() > 1) {
                            secondFileChar = fileName.charAt(1);
                        } else {
                            throw new IllegalArgumentException("Not allowed name of file in table");
                        }
                        if (Character.isDigit(firstFileChar)) {
                            if (Character.isDigit(secondFileChar)) {
                                fileNumber = Integer.parseInt(fileName.substring(0, 2));
                            } else {
                                fileNumber = Integer.parseInt(fileName.substring(0, 1));
                            }
                        } else {
                            throw new IllegalArgumentException("Not allowed name of file in table");
                        }
                        if (!fileName.equals(new String(fileNumber + ".dat"))) {
                            throw new IllegalArgumentException("Not allowed name of file in table");
                        }
                        FileReader fileReader = new FileReader(f, state.dataStorage);
                        while (fileReader.checkingLoadingConditions()) {
                            String key = fileReader.getNextKey();
                            int hashByte = key.getBytes()[0];
                            if (hashByte < 0) {
                                hashByte += 256;
                            }
                            int ndirectory = hashByte % DIR_COUNT;
                            int nfile = (hashByte / DIR_COUNT) % FILE_COUNT;
                            if (ndirectory != dirNumber) {
                                throw new IllegalArgumentException("Wrong key in " + dirName);
                            }
                            if (fileNumber != nfile) {
                                throw new IllegalArgumentException("Wrong key in" + fileName);
                            }
                        }
                        fileReader.putKeysToTable();
                        fileReader.closeResources();
                    }
                }
            }
        }
    }
}
