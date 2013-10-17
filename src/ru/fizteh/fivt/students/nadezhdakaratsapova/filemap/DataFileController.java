package ru.fizteh.fivt.students.nadezhdakaratsapova.filemap;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DataFileController {

    void loadDataFromFile(File file, DataTable table) {
        try {
            DataInputStream inStream = new DataInputStream(new FileInputStream(file));
            int i = 0;
            long fileLength = file.length();
            List<Byte> key = new ArrayList<Byte>();
            byte curByte = 1;

            while ((i < fileLength) && ((curByte = inStream.readByte()) != 0)) {
                key.add(curByte);
                ++i;
            }
            if (curByte != 0) {
                throw new IOException("not allowable format of data");
            }
            List<Integer> offsets = new ArrayList<Integer>();
            List<String> keysToMap = new ArrayList<String>();
            int offset1 = inStream.readInt();
            int arraySize = key.size();
            byte[] keyInBytes = new byte[arraySize];
            for (int j = 0; j < arraySize; ++j) {
                keyInBytes[j] = key.get(j);
            }
            Integer prevOffset = offset1;
            offset1 -= arraySize;
            key.clear();
            if (offset1 > fileLength) {
                throw new IOException("too big offset");
            }

            while (i < fileLength) {
                while ((i < fileLength) && (((curByte = inStream.readByte()) != 0)) | (offset1 != i)) {
                    key.add(curByte);
                    ++i;
                }

                if (i == fileLength) {
                    throw new IOException("not allowable format of data");
                }
                if (i == offset1) {
                    int j = 0;
                    int offsetsSize = offsets.size();
                    while (j < offsetsSize) {
                        byte[] b = new byte[offsets.get(j)];
                        inStream.read(b, i, i + offsets.get(j));
                        table.add(keysToMap.get(j), new String(b, "UTF-8"));
                        i += offsets.get(j);
                        ++j;
                    }
                    int lastOffset = (int) (fileLength - i);
                    byte[] b = new byte[lastOffset];
                    for (int k = 0; i < fileLength; ++k, ++i) {
                        b[k] = inStream.readByte();
                    }
                    table.add(new String(keyInBytes, "UTF-8"), new String(b, "UTF*8"));
                }
                int intSize = 4;
                Integer offset = inStream.readInt();
                i += intSize;
                offset1 -= offset;
                arraySize = key.size();
                Integer offsetValue = offset - prevOffset;
                keysToMap.add(new String(keyInBytes, "UTF-8"));
                offsets.add(offsetValue);
                keyInBytes = new byte[arraySize];
                for (int j = 0; j < arraySize; ++j) {
                    keyInBytes[j] = key.get(j);
                }
                key.clear();
                ++i;

            }
        } catch (FileNotFoundException e) {
            System.err.println(file.getName() + " was not found");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }


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
