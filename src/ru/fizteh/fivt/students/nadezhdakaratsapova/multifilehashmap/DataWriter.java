package ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap;

import ru.fizteh.fivt.students.nadezhdakaratsapova.filemap.DataTable;
import ru.fizteh.fivt.students.nadezhdakaratsapova.filemap.FileWriter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class DataWriter {
    public static final int DIR_COUNT = 16;
    public static final int FILE_COUNT = 16;

    public void writeData(MultiFileHashMapProvider state) throws IOException {
        if (state.getCurTable() != null) {
            Set<String> keys = state.dataStorage.getKeys();
            if (!keys.isEmpty()) {
                for (int i = 0; i < DIR_COUNT; ++i) {
                    File dir = new File(state.getCurTable(), new String(i + ".dir"));
                    for (int j = 0; j < FILE_COUNT; ++j) {
                        DataTable keysToFile = new DataTable();
                        File file = new File(dir, new String(j + ".dat"));
                        for (String key : keys) {
                            int hashByte = Math.abs(key.getBytes()[0]);
                            int ndirectory = hashByte % DIR_COUNT;
                            int nfile = (hashByte / DIR_COUNT) % FILE_COUNT;
                            if ((ndirectory == i) && (nfile == j)) {
                                if (!dir.getCanonicalFile().exists()) {
                                    dir.getCanonicalFile().mkdir();
                                }

                                if (!file.getCanonicalFile().exists()) {
                                    file.getCanonicalFile().createNewFile();
                                }
                                keysToFile.put(key, state.dataStorage.get(key));
                                keysToFile.commit();
                            }
                        }

                        if (!keysToFile.isEmpty()) {
                            FileWriter fileWriter = new FileWriter();
                            fileWriter.writeDataToFile(file.getCanonicalFile(), keysToFile);
                        } else {
                            if (file.getCanonicalFile().exists()) {
                                file.getCanonicalFile().delete();
                            }
                        }
                    }
                    if (dir.getCanonicalFile().listFiles() == null) {
                        dir.delete();
                    }
                }
            }
        }
    }
}
