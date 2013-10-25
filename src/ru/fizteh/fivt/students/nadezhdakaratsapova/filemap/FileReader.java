package ru.fizteh.fivt.students.nadezhdakaratsapova.filemap;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileReader {

    public static final int MAX_FILE_SIZE = 1024 * 1024;

    public void loadDataFromFile(File file, DataTable dataTable) {
        try {
            if (file.length() != 0) {
                DataInputStream inStream = new DataInputStream(new FileInputStream(file));
                int curPos = 0;
                long fileLength = file.length();
                List<Byte> key = new ArrayList<Byte>();
                byte curByte = 1;
                int intSize = 4;
                int separatorSize = 1;
                while ((curPos < fileLength) && ((curByte = inStream.readByte()) != 0)) {
                    key.add(curByte);
                    ++curPos;
                    if (curPos > MAX_FILE_SIZE) {
                        throw new IOException("too big key");
                    }
                }
                if (curByte != 0) {
                    throw new IOException("not allowable format of data");
                }
                List<Integer> offsets = new ArrayList<Integer>();
                List<String> keysToMap = new ArrayList<String>();
                int offset1 = inStream.readInt();
                curPos += intSize;
                ++curPos;
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
                while (curPos < fileLength) {
                    while ((curPos < fileLength) && ((((offset1 != 0)) && (curByte = inStream.readByte()) != '\0'))) {
                        key.add(curByte);
                        ++curPos;
                        if (curPos > MAX_FILE_SIZE) {
                            throw new IOException("too big key");
                        }
                    }
                    if (curPos == fileLength) {
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
                                dataTable.add(keysToMap.get(j), new String(b, StandardCharsets.UTF_8));
                                curPos += offsets.get(j);
                                ++j;
                            }
                        }
                        int lastOffset = (int) (fileLength - curPos);
                        byte[] b = new byte[lastOffset];
                        for (int k = 0; curPos < fileLength; ++k, ++curPos) {
                            b[k] = inStream.readByte();
                        }
                        dataTable.add(new String(keyInBytes, StandardCharsets.UTF_8), new String(b, StandardCharsets.UTF_8));
                    } else {
                        int offset = inStream.readInt();
                        curPos += intSize;
                        arraySize = key.size();
                        offset1 -= arraySize + separatorSize + intSize;
                        int offsetValue = offset - prevOffset;
                        prevOffset = offset;
                        keysToMap.add(new String(keyInBytes, StandardCharsets.UTF_8));
                        offsets.add(offsetValue);
                        keyInBytes = new byte[arraySize];
                        for (int j = 0; j < arraySize; ++j) {
                            keyInBytes[j] = key.get(j);
                        }
                        key.clear();
                        ++curPos;
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
