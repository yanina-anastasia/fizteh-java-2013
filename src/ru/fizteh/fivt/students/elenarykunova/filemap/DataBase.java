package ru.fizteh.fivt.students.elenarykunova.filemap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ru.fizteh.fivt.students.elenarykunova.shell.Shell;

public class DataBase {

    private HashMap<String, String> data = new HashMap<String, String>();
    private RandomAccessFile dataFile = null;
    private String filePath = null;
    private String tablePath = null;
    private int ndir;
    private int nfile;
    private Filemap table = null;
    private boolean exists = false;
    
    public boolean hasFile() {
        return exists;
    }

    public String getFileName(int ndir, int nfile) {
        return ndir + ".dir" + File.separator + nfile + ".dat";
    }

    public DataBase(Filemap myTable, int numbDir, int numbFile,
            boolean createIfNotExists) throws RuntimeException, ParseException {
        table = myTable;
        tablePath = table.getTablePath();
        ndir = numbDir;
        nfile = numbFile;
        filePath = tablePath + File.separator + getFileName(ndir, nfile);

        File tmpFile = new File(filePath);
        if (!tmpFile.exists() && createIfNotExists) {
            if (!tmpFile.getParentFile().exists()) {
                if (!tmpFile.getParentFile().mkdir()) {
                    System.err.println(filePath + ": can't create file");
                    System.exit(1);
                }
            }
            try {
                if (!tmpFile.createNewFile()) {
                    System.err.println(filePath + ": can't create file");
                    System.exit(1);
                }
            } catch (IOException e) {
                System.err.println(filePath + ": can't create file");
                System.exit(1);
            }
        }

        if (tmpFile.exists()) {
            try {
                dataFile = new RandomAccessFile(filePath, "r");
                exists = true;
                load(dataFile, table);
            } catch (FileNotFoundException e1) {
                throw new RuntimeException(filePath + ": can't find file", e1);
            } catch (IOException e3) {
                throw new RuntimeException(filePath + ": error in loading file", e3);
            } finally {
                try {
                    closeDataFile();
                } catch (Throwable e5) {
                    System.err.println("can't close file " + filePath);
                }
            }
        }
    }

    public void checkOffset(long offset, long currPtr) throws IOException {
        if (offset < currPtr || offset > dataFile.length()) {
            IOException e = new IOException();
            throw e;
        }
    }

    public boolean isCorrectPlace(String key) {
        int hashcode = Math.abs(key.hashCode());
        int currNumbDir = hashcode % 16;
        int currNumbFile = hashcode / 16 % 16;
        return (currNumbDir == ndir && currNumbFile == nfile);
    }

    public String getKeyFromFile() throws IOException {
        byte ch = 0;
        ArrayList<Byte> v = new ArrayList<Byte>();
        ch = dataFile.readByte();
        while (ch != 0) {
            v.add(ch);
            ch = dataFile.readByte();
        }
        byte[] res = new byte[v.size()];
        for (int i = 0; i < v.size(); i++) {
            res[i] = v.get(i).byteValue();
        }
        String result = new String(res, StandardCharsets.UTF_8);
        if (!isCorrectPlace(result)) {
            IOException e = new IOException();
            throw e;
        }
        return result;
    }

    public String getValueFromFile(long nextOffset) throws IOException {
        int beginPtr = (int) dataFile.getFilePointer();
        byte[] res = new byte[(int) (nextOffset - beginPtr)];
        dataFile.read(res);
        String result = new String(res, StandardCharsets.UTF_8);
        return result;
    }

    public void load(RandomAccessFile dataFile, Filemap table)
            throws RuntimeException, IOException, ParseException {
        try {
            if (dataFile.length() == 0) {
                return;
            }

            long currPtr = 0;
            long firstOffset = 0;
            long newOffset = 0;
            long nextOffset = 0;
            String keyFirst = "";
            String keySecond = "";
            String value;

            dataFile.seek(currPtr);
            keyFirst = getKeyFromFile();

            newOffset = dataFile.readInt();
            currPtr = dataFile.getFilePointer();
            checkOffset(newOffset, currPtr);
            firstOffset = newOffset;
            do {
                dataFile.seek(currPtr);
                if (currPtr < firstOffset) {
                    keySecond = getKeyFromFile();
                    nextOffset = dataFile.readInt();
                    currPtr = dataFile.getFilePointer();
                    checkOffset(nextOffset, currPtr);
                } else if (currPtr == firstOffset) {
                    nextOffset = dataFile.length();
                    currPtr++;
                }
                if (nextOffset < newOffset) {
                    IOException e1 = new IOException();
                    throw e1;
                }

                dataFile.seek(newOffset);
                value = getValueFromFile(nextOffset);

                data.put(keyFirst, value);
                MyStoreable val;
                try {
                    val = (MyStoreable) table.getProvider().deserialize(table,
                            value);
                } catch (ParseException e) {
                    throw new ParseException(filePath
                            + " can't deserialize values from file " + e.getMessage(), e.getErrorOffset());
                }
                table.getHashMap().put(keyFirst, val);

                keyFirst = keySecond;
                newOffset = nextOffset;
            } while (currPtr <= firstOffset);
        } catch (IOException e1) {
            throw new IOException(filePath
                    + " can't read values from file", e1);
        } catch (OutOfMemoryError e2) {
            throw new RuntimeException(filePath
                    + " can't read values from file: out of memory", e2);
        }
    }

    public String put(String key, String value) {
        return data.put(key, value);
    }

    public String get(String key) {
        return data.get(key);
    }

    public String remove(String key) {
        return data.remove(key);
    }

    private int getLength(String str) {
        int curr = 0;

        curr = str.getBytes(StandardCharsets.UTF_8).length;
        return curr;
    }

    protected void closeDataFile() throws RuntimeException {
        try {
            if (dataFile != null) {
                dataFile.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(filePath + " can't close file", e);
        }
    }

    public void commitChanges() throws IOException, RuntimeException {
        try {
            try {
                dataFile = new RandomAccessFile(filePath, "rw");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(filePath
                        + " can't get access to file", e);
            }
            if (data == null || data.isEmpty()) {
                closeDataFile();
                Shell sh = new Shell(tablePath, false);
                if (sh.rm(filePath) != Shell.ExitCode.OK) {
                    throw new RuntimeException(filePath + " can't delete file");
                }
                exists = false;
                return;
            }

            int offset = 0;
            Set<Map.Entry<String, String>> mySet = data.entrySet();
            for (Map.Entry<String, String> myEntry : mySet) {
                offset += getLength(myEntry.getKey()) + 1 + 4;
            }
            int currOffset = offset;
            dataFile.setLength(0);
            dataFile.seek(0);
            for (Map.Entry<String, String> myEntry : mySet) {
                dataFile.write(myEntry.getKey().getBytes());
                dataFile.writeByte(0);
                dataFile.writeInt(currOffset);
                currOffset += getLength(myEntry.getValue());
            }
            for (Map.Entry<String, String> myEntry : mySet) {
                dataFile.write(myEntry.getValue().getBytes());
            }
        } finally {
            try {
                closeDataFile();
            } catch (Throwable e5) {
                System.err.println("can't close file " + filePath);
            }
        }
    }
}
