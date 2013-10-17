package ru.fizteh.fivt.students.nadezhdakaratsapova.filemap;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileReader {
    void loadDataFromFile(File file, DataTable table) {
        try {
            if (file.length() != 0) {
                DataInputStream inStream = new DataInputStream(new FileInputStream(file));
                int i = 0;
                long fileLength = file.length();
                List<Byte> key = new ArrayList<Byte>();
                byte curByte = 1;
                int intSize = 4;
                int separatorSize = 1;
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
                i += intSize;
                ++i;
                int arraySize = key.size();
                byte[] keyInBytes = new byte[arraySize];
                for (int j = 0; j < arraySize; ++j) {
                    keyInBytes[j] = key.get(j);
                }
                Integer prevOffset = offset1;
                offset1 -= arraySize + separatorSize + intSize;
                key.clear();
                if (offset1 > fileLength) {
                    throw new IOException("too big offset");
                }
                while (i < fileLength) {
                    while ((i < fileLength) && ((((offset1 != 0)) && (curByte = inStream.readByte()) != '\0'))) {
                        key.add(curByte);
                        ++i;
                    }
                    if (i == fileLength) {
                        throw new IOException("not allowable format of data");
                    }
                    if ((offset1 == 0) && (!key.isEmpty())) {
                        throw new IOException("the last offset had to be before values");
                    }
                    if (offset1 == 0) {
                        int j = 0;
                        if (!offsets.isEmpty()) {
                            int offsetsSize = offsets.size();
                            while (j < offsetsSize) {
                                byte[] b = new byte[offsets.get(j)];
                                inStream.read(b, 0, offsets.get(j));
                                table.add(keysToMap.get(j), new String(b, StandardCharsets.UTF_8));
                                i += offsets.get(j);
                                ++j;
                            }
                        }
                        int lastOffset = (int) (fileLength - i);
                        byte[] b = new byte[lastOffset];
                        for (int k = 0; i < fileLength; ++k, ++i) {
                            b[k] = inStream.readByte();
                        }
                        table.add(new String(keyInBytes, StandardCharsets.UTF_8), new String(b, StandardCharsets.UTF_8));
                    } else {
                        Integer offset = inStream.readInt();
                        i += intSize;
                        arraySize = key.size();
                        offset1 -= arraySize + separatorSize + intSize;
                        Integer offsetValue = offset - prevOffset;
                        keysToMap.add(new String(keyInBytes, StandardCharsets.UTF_8));
                        offsets.add(offsetValue);
                        keyInBytes = new byte[arraySize];
                        for (int j = 0; j < arraySize; ++j) {
                            keyInBytes[j] = key.get(j);
                        }
                        key.clear();
                        ++i;
                    }

                }
                inStream.close();
            }
        } catch (FileNotFoundException e) {
            System.err.println(file.getName() + " was not found");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
