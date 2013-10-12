package ru.fizteh.fivt.students.asaitgalin.filemap;

import ru.fizteh.fivt.students.asaitgalin.utils.StringUtils;

import java.io.RandomAccessFile;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileSerializer {
    private RandomAccessFile stream;
    private boolean isStreamValid;
    private long firstValueOffset = -1;

    private String nextKey;
    private long nextValueOffset;

    public FileSerializer(String name) throws IOException {
        stream = new RandomAccessFile(name, "rw");
        isStreamValid = true;
        readNext();
    }

    public boolean hasNextEntry() throws IOException {
        return isStreamValid && (stream.getFilePointer() <= firstValueOffset);
    }

    private void readNext() {
        List<Byte> list = new ArrayList<>();
        try {
            byte readByte = stream.readByte();
            while (readByte != 0) {
                list.add(readByte);
                readByte = stream.readByte();
            }
            nextKey = StringUtils.getStringFromArray(list, "UTF-8");
            nextValueOffset = stream.readInt();

            if (firstValueOffset == -1) {
                firstValueOffset = nextValueOffset;
            }

        } catch (IOException ioe) {
            isStreamValid = false;
        }
    }

    public void readNextEntry(Map<String, String> container) throws IOException {
        try {
            long offset = nextValueOffset;
            String key = nextKey;
            readNext();
            long savedPos = stream.getFilePointer();
            stream.seek(offset);
            byte[] valueData;
            if (key != null && !isStreamValid) {
                valueData = new byte[(int)(stream.length() - offset)];
            } else {
                valueData = new byte[(int)(nextValueOffset - offset)];
            }
            stream.read(valueData);
            String value = new String(valueData, "UTF-8");
            stream.seek(savedPos);
            container.put(key, value);
        } catch (EOFException eofe) {
            isStreamValid = false;
        }
    }

}
