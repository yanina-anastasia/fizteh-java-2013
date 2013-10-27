package ru.fizteh.fivt.students.mescherinilya.filemap;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Set;


public class CommandCommit implements Command {

    RandomAccessFile database;

    @Override
    public String getName() {
        return "commit";
    }

    @Override
    public int getArgsCount() {
        return 0;
    }

    @Override
    public void execute(String[] args) throws IOException {
        try {
            try {

                database = new RandomAccessFile(FileMap.databaseLocation, "rw");

                database.setLength(0);

                int offset = 0;

                Set<String> keySet = FileMap.storage.keySet();
                for (String key : keySet) {
                    offset += key.getBytes(StandardCharsets.UTF_8).length + 5;
                }

                ArrayList<String> values = new ArrayList<String>();
                for (String key : keySet) {
                    database.write(key.getBytes(StandardCharsets.UTF_8));
                    database.write('\0');
                    database.writeInt(offset);
                    String value = FileMap.storage.get(key);
                    values.add(value);
                    offset += value.getBytes(StandardCharsets.UTF_8).length;
                }

                for (String value : values) {
                    database.write(value.getBytes(StandardCharsets.UTF_8));
                }

            } finally {
                database.close();
            }
        } catch (IOException e) {
            throw new IOException("Can't write to the file! " + e.getMessage());
        }

    }
}
