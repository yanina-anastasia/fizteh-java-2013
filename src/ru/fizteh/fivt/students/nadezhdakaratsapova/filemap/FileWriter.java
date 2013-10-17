package ru.fizteh.fivt.students.nadezhdakaratsapova.filemap;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FileWriter {
    public void writeDataToFile(File file, DataTable table) {
        try {
            DataOutputStream outStream = new DataOutputStream(new FileOutputStream(file));
            Set<String> keys = table.getKeys();
            List<String> values = new ArrayList<String>(keys.size());
            int intSize = 4;
            int separatorSize = 1;
            Integer offset = 0;
            for (String s : keys) {
                offset += s.getBytes("UTF-8").length + intSize + separatorSize;
            }
            for (String s : keys) {
                byte[] b = s.getBytes("UTF-8");
                outStream.write(b);
                outStream.writeByte(0);
                outStream.writeInt(offset);
                String value = table.getValue(s);
                values.add(value);
                offset += value.getBytes().length;
            }
            for (String v : values) {
                outStream.write(v.getBytes("UTF-8"));
            }
        } catch (FileNotFoundException e) {
            System.err.println(file.getName() + " was not found");
        } catch (IOException e) {
            System.err.println("mistake in record");
        }

    }
}
