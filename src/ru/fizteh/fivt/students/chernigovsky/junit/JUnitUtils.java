package ru.fizteh.fivt.students.chernigovsky.junit;

import ru.fizteh.fivt.students.chernigovsky.filemap.State;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class JUnitUtils {
    public static void writeTable(State state) throws IOException {

         for (Integer directoryNumber = 0; directoryNumber < 16; ++directoryNumber) {
            File tableFolder = new File(state.getCurrentTableProvider().getDbDirectory(), state.getCurrentTable().getName());
            File dir = new File(tableFolder, directoryNumber.toString() + ".dir");

            for (Integer fileNumber = 0; fileNumber < 16; ++fileNumber) {
                HashMap<String, String> currentMap = new HashMap<String, String>();
                for (Map.Entry<String, String> entry : state.getCurrentTable().getEntrySet()) {
                    if (Math.abs(entry.getKey().getBytes("UTF-8")[0]) % 16 == directoryNumber && Math.abs(entry.getKey().getBytes("UTF-8")[0]) / 16 % 16 == fileNumber) {
                        currentMap.put(entry.getKey(), entry.getValue());
                    }
                }

                File file = new File(dir, fileNumber.toString() + ".dat");

                if (currentMap.size() == 0) {
                    if (file.exists()) {
                        if (!file.delete()) {
                            throw new IOException("Delete error");
                        }
                    }
                    continue;
                }

                if (!dir.exists()) {
                    dir.mkdir();
                }
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.getChannel().truncate(0); // Clear file
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);
                try {
                    for (Map.Entry<String, String> entry : currentMap.entrySet()) {
                        dataOutputStream.writeInt(entry.getKey().getBytes("UTF-8").length);
                        dataOutputStream.writeInt(entry.getValue().getBytes("UTF-8").length);
                        dataOutputStream.write(entry.getKey().getBytes("UTF-8"));
                        dataOutputStream.write(entry.getValue().getBytes("UTF-8"));
                    }
                } finally {
                    dataOutputStream.close();
                }

            }

            if (dir.exists() && dir.list().length == 0) {
                if (!dir.delete()) {
                    throw new IOException("Delete");
                }
            }
        }
    }
}
