package ru.fizteh.fivt.students.inaumov.filemap;

import java.io.*;
import java.util.HashMap;

public class ReadHandler implements Closeable {
    private RandomAccessFile inputFile = null;

    public ReadHandler(String fileName) throws IOException {
        try {
            inputFile = new RandomAccessFile(fileName, "r");
        } catch (FileNotFoundException exception) {

        }
    }

    public static void loadFromFile(String filePath, HashMap<String, String> data) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }

        ReadHandler reader = new ReadHandler(filePath);

        while (!reader.readEnd()) {
            int keyLength = reader.readInteger();
            int valueLength = reader.readInteger();

            String key = reader.readString(keyLength);
            String value = reader.readString(valueLength);

            data.put(key, value);
        }

        reader.close();
    }

    private int readInteger() throws IOException {
        int result = inputFile.readInt();
        //System.out.println("read int: " + result);
        return result;
    }

    private String readString(int stringLength) throws IOException {
        byte[] stringBytes = null;

        if (stringLength <= 0) {
            throw new WrongFileFormatException("Some key or value length is negative in " + inputFile.toString());
        }
        try {
            stringBytes = new byte[stringLength];
        } catch (OutOfMemoryError error) {
            throw new WrongFileFormatException("Some key or value length is too long in " + inputFile.toString());
        }
        inputFile.read(stringBytes);

        return new String(stringBytes, "UTF-8");
    }

    public boolean readEnd() throws IOException {
        if (inputFile == null) {
            //System.err.println("AbstractTable::readEnd(): inputFile == null");
            return true;
        }

        if (inputFile.getFilePointer() <= inputFile.length() - 1) {
            //System.out.println("AbstractTable::readEnd(): fileptr = " + inputFile.getFilePointer());
            //System.out.println("AbstractTable::readEnd(): filelength = " + inputFile.length());
            return false;
        }

        return true;
    }

    public void close() throws IOException {
        inputFile.close();
    }

}
