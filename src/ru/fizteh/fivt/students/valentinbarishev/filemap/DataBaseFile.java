package ru.fizteh.fivt.students.valentinbarishev.filemap;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.io.File;
import java.io.FileNotFoundException;

public class DataBaseFile {

    static final byte OLD_NODE = 1;
    static final byte NEW_NODE = 2;
    static final byte MODIFIED_NODE = 3;


    public final class Node {
        private byte status;
        private byte[] key;
        private byte[] value;

        public int getZeroByte() {
            return Math.abs(key[0]);
        }

        public Node(final byte[] newKey, final byte[] newValue) {
            status = NEW_NODE;
            key = newKey;
            value = newValue;
        }

        public Node(final RandomAccessFile inputFile) throws IOException {
            status = OLD_NODE;
            try {
                int keyLength = inputFile.readInt();
                int valueLength = inputFile.readInt();
                if ((keyLength <= 0) || (valueLength <= 0)) {
                    throw new DataBaseWrongFileFormat("Wrong file format! " + file.getName());
                }
                try {
                    key = new byte[keyLength];
                    value = new byte[valueLength];
                } catch (OutOfMemoryError e) {
                    throw new DataBaseWrongFileFormat("Some key or value are too large in " + file.getName());
                }
                inputFile.read(key);
                inputFile.read(value);
            } catch (Exception e) {
                throw new DataBaseWrongFileFormat("Wrong file format! " + file.getName());
            }
        }

        public void setKey(final byte[] newKey) {
            key = newKey;
        }

        public void setValue(final byte[] newValue) {
            if (status == OLD_NODE) {
                status = MODIFIED_NODE;
            }
            value = newValue;
        }

        public void write(final RandomAccessFile outputFile) throws IOException {
            outputFile.writeInt(key.length);
            outputFile.writeInt(value.length);
            outputFile.write(key);
            outputFile.write(value);
        }

        public byte getStatus() {
            return status;
        }

        public void setStatus(byte newStatus) {
            status = newStatus;
        }

    }

    protected final String fileName;
    protected File file;
    protected List<Node> data;
    private int fileNumber;
    private int direcotryNumber;

    public DataBaseFile(final String newFileName, final int newDirectoryNumber, final int newFileNumber) {
        fileName = newFileName;
        file = new File(fileName);
        data = new ArrayList<Node>();
        fileNumber = newFileNumber;
        direcotryNumber = newDirectoryNumber;
        open();
        load();
        check();
    }

    public boolean check() {
        for (Node node : data) {
            if (!((node.getZeroByte() % 16 == direcotryNumber) && ((node.getZeroByte() / 16) % 16 == fileNumber))) {
                throw new DataBaseWrongFileFormat("Wrong file format key[0] =  " + String.valueOf(node.getZeroByte())
                        + " in file " + fileName);
            }
        }
        return true;
    }

    private void open() {
        try {
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    throw new DataBaseException("Cannot create " + fileName);
                }
            }
        } catch (IOException e) {
            throw new DataBaseException("Open file error! " + e.getMessage());
        }
    }

    private void load() {
        try {
            RandomAccessFile inputFile = new RandomAccessFile(fileName, "rw");
            while (inputFile.getFilePointer() < inputFile.length() - 1) {
                data.add(new Node(inputFile));
            }
            inputFile.close();
        } catch (FileNotFoundException e) {
            throw new DataBaseException("File not found!");
        } catch (IOException e) {
            throw new DataBaseException("File load error!");
        }
    }

    public void save() {
        try {
            if (data.size() == 0) {
                if ((file.exists()) && (!file.delete())) {
                    throw new DataBaseException("Cannot delete a file!");
                }
            } else {
                RandomAccessFile outputFile = new RandomAccessFile(fileName, "rw");
                try {
                    for (Node node : data) {
                        node.write(outputFile);
                    }
                    outputFile.setLength(outputFile.getFilePointer());
                } finally {
                    outputFile.close();
                }
            }
        } catch (FileNotFoundException e) {
            throw new DataBaseException("File save error!");
        } catch (IOException e) {
            throw new DataBaseException("Write to file error!");
        }
    }


    private int search(final byte[] key) {
        for (int i = 0; i < data.size(); ++i) {
            if (Arrays.equals(data.get(i).key, key)) {
                return i;
            }
        }
        return -1;
    }

    public String put(final String keyStr, final String valueStr) {
        try {
            byte[] key = keyStr.getBytes("UTF-8");
            byte[] value = valueStr.getBytes("UTF-8");

            int index = search(key);
            if (index == -1) {
                data.add(new Node(key, value));
                return null;
            } else {
                String str = new String(data.get(index).value);
                data.get(index).setValue(value);
                return str;
            }
        } catch (UnsupportedEncodingException e) {
            throw new DataBaseException(e.getMessage());
        }

    }

    public String get(final String keyStr) {
        try {
            byte[] key = keyStr.getBytes("UTF-8");
            int index = search(key);
            if (index != -1) {
                return new String(data.get(index).value);
            } else {
                return null;
            }
        } catch (UnsupportedEncodingException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    public String remove(final String keyStr) {
        try {
            byte[] key = keyStr.getBytes("UTF-8");
            int index = search(key);
            if (index == -1) {
                return null;
            } else {
                String result = new String(data.get(index).value);
                data.remove(index);
                return result;
            }
        } catch (UnsupportedEncodingException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    public int getNewKeys() {
        int result = 0;
        for (Node node : data) {
            if ((node.getStatus() == NEW_NODE) || (node.getStatus() == MODIFIED_NODE)) {
                ++result;
            }
        }
        return result;
    }

    public int getSize() {
        return data.size();
    }

    public void commit() {
        try {
            if (data.size() != 0) {
                RandomAccessFile outputFile = new RandomAccessFile(fileName, "rw");
                try {
                    for (Node node : data) {
                        node.write(outputFile);
                        node.setStatus(OLD_NODE);
                    }
                    outputFile.setLength(outputFile.getFilePointer());
                } finally {
                    outputFile.close();
                }
            }
        } catch (FileNotFoundException e) {
            throw new DataBaseException("File save error!");
        } catch (IOException e) {
            throw new DataBaseException("Write to file error!");
        }
    }

    public void rollback() {
        data.clear();
        load();
    }
}
