package ru.fizteh.fivt.students.elenarykunova.filemap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import ru.fizteh.fivt.students.elenarykunova.shell.Shell;

public class DataBase {

    private HashMap<String, String> data = new HashMap<String, String>();
    private RandomAccessFile dataFile = null;
    private String filePath = null;
    private String tablePath = null;
    private int ndir;
    private int nfile;

    public boolean hasFile() {
        return (dataFile != null);
    }

    public String getFileName(int ndir, int nfile) {
        return ndir + ".dir" + File.separator + nfile + ".dat";
    }

    public DataBase(String currTablePath, int numbDir, int numbFile, HashMap<String, String> map,
            boolean createIfNotExists) throws RuntimeException {
        tablePath = currTablePath;
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
                load(dataFile, map);
            } catch (RuntimeException e) {
                throw e;
            } catch (FileNotFoundException e) {
                throw new RuntimeException(filePath + ": file not found", e);
            } finally {
                try {
                    closeDataFile();
                } catch (Throwable e5) {
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
        Vector<Byte> v = new Vector<Byte>();
        ch = dataFile.readByte();
        while (ch != 0) {
            v.add(ch);
            ch = dataFile.readByte();
        }
        byte[] res = new byte[v.size()];
        for (int i = 0; i < v.size(); i++) {
            res[i] = v.elementAt(i).byteValue();
        }
        String result = new String(res, "UTF-8");
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
        String result = new String(res, "UTF-8");
        return result;
    }

    public void load(RandomAccessFile dataFile, HashMap<String, String> map) throws RuntimeException {
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
                map.put(keyFirst, value);

                keyFirst = keySecond;
                newOffset = nextOffset;
            } while (currPtr <= firstOffset);
        } catch (IOException e) {
            throw new RuntimeException(filePath + " can't read values from file");
        } catch (OutOfMemoryError e) {
            throw new RuntimeException(filePath + " can't read values from file: out of memory");
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

    private int getLength(String str) throws UnsupportedEncodingException {
        int curr = 0;

        curr = str.getBytes("UTF-8").length;
        return curr;
    }

    protected void closeDataFile() throws RuntimeException {
        try {
            if (dataFile != null) {
                dataFile.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(filePath + " can't close file");
        }
    }

    public void commitChanges() throws IOException, RuntimeException  {
        IOException e1 = new IOException();
        try {
            try {
                dataFile = new RandomAccessFile(filePath, "rw");
            } catch (FileNotFoundException e) {
                throw new RuntimeException(filePath + " can't get access to file", e); 
            }
            if (data == null || data.isEmpty()) {
                try {
                    closeDataFile();
                } catch (RuntimeException e2) {
                    throw e2;
                }
                Shell sh = new Shell(tablePath, false);
                if (sh.rm(filePath) != Shell.ExitCode.OK) {
                    throw new RuntimeException(filePath + " can't delete file");
                }
                return;
            }
    
            int offset = 0;
            Set<Map.Entry<String, String>> mySet = data.entrySet();
            for (Map.Entry<String, String> myEntry : mySet) {
                try {
                    offset += getLength(myEntry.getKey()) + 1 + 4;
                } catch (UnsupportedEncodingException e) {
                    throw e1;
                }
            }
            int currOffset = offset;
            try {
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
                try {
                    closeDataFile();
                } catch (RuntimeException e3) {
                    throw e3;
                }
            } catch (IOException e2) {
                throw e2;
            }
        } catch (Throwable e) {
        } finally {
            try {
                closeDataFile();
            } catch (Throwable e5) {
            }
        }
    }
}
