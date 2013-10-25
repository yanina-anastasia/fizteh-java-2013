package ru.fizteh.fivt.students.nadezhdakaratsapova.multifilehashmap;

import ru.fizteh.fivt.students.nadezhdakaratsapova.filemap.FileReader;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class DataLoader {
    public static final int DIR_COUNT = 16;
    public static final int FILE_COUNT = 16;

    public void load(MultiFileHashMapState state) throws IOException {
        if (state.getCurTable() == null) {
            System.out.println("no table");
        } else {
            if (!state.getCurTable().equals(state.getNextTable())) {
                DataWriter dataWriter = new DataWriter();
                dataWriter.writeData(state);
                FileReader fileReader = new FileReader();
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
                    for (File f : files) {
                        if (!f.isFile()) {
                            throw new IOException("Unexpected directory");
                        }
                        fileReader.loadDataFromFile(f, state.dataStorage);

                    }

                }
                state.setCurTable(state.getNextTable());
            }
        }
    }
}
