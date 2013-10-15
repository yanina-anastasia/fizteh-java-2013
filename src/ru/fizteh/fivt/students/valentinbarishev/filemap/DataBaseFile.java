package ru.fizteh.fivt.students.valentinbarishev.filemap;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.io.File;
import java.io.FileNotFoundException;

public final class DataBaseFile {

    public final class Node {
        private byte[] key;
        private byte[] value;

        public Node(final byte[] newKey, final byte[] newValue) {
            key = newKey;
            value = newValue;
        }

        public Node(final RandomAccessFile inputFile) throws IOException {
            int keyLength = inputFile.readInt();
            int valueLength = inputFile.readInt();
            key = new byte[keyLength];
            value = new byte[valueLength];
            inputFile.read(key);
            inputFile.read(value);
        }

        public void setKey(final byte[] newKey) {
            key = newKey;
        }

        public void setValue(final byte[] newValue) {
            value = newValue;
        }

        public void write(final RandomAccessFile outputFile)
                throws IOException {
            outputFile.writeInt(key.length);
            outputFile.writeInt(value.length);
            outputFile.write(key);
            outputFile.write(value);
        }

    }

    private final String fileName;
    private File file;
    private List<Node> data;

    public DataBaseFile(final String newFileName) {
        fileName = newFileName;
        file = new File(fileName);
        data = new ArrayList<Node>();
        open();
        load();
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
            RandomAccessFile outputFile = new RandomAccessFile(fileName, "rw");
            for (Node node : data) {
                node.write(outputFile);
            }
            outputFile.setLength(outputFile.getFilePointer() + 1);
            outputFile.close();
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
            boolean result = false;
            byte[] key = keyStr.getBytes("UTF-8");
            byte[] value = valueStr.getBytes("UTF-8");

            int index = search(key);
            if (index == -1) {
                data.add(new Node(key, value));
                return "";
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
                return "";
            }
        } catch (UnsupportedEncodingException e) {
            throw new DataBaseException(e.getMessage());
        }
    }

    public boolean remove(final String keyStr) {
        try {
            byte[] key = keyStr.getBytes("UTF-8");
            int index = search(key);
            if (index == -1) {
                return false;
            } else {
                data.remove(index);
                return true;
            }
        } catch (UnsupportedEncodingException e) {
            throw new DataBaseException(e.getMessage());
        }
    }
}
