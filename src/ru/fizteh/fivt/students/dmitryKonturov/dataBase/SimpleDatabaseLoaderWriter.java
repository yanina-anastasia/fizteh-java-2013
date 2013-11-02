package ru.fizteh.fivt.students.dmitryKonturov.dataBase;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *  Loads simpleDatabase from a file
 */

public class SimpleDatabaseLoaderWriter {

    private static void readSomeBytes(InputStream input, byte[] toRead, int len) throws DatabaseException {
        int wasRead = 0;
        try {
            while (wasRead < len) {
                int tmp = input.read(toRead, wasRead, len - wasRead);
                if (tmp == -1) {
                    throw new DatabaseException("Input was shorter then demanded length");
                }
                wasRead += tmp;
            }
        } catch (IOException e) {
            throw new DatabaseException("Input/output problems" + (e.getMessage() != null ? ": " + e.getMessage() : ""));
        }
    }

    private static int readInt(InputStream input) throws DatabaseException {
        byte[] number = new byte[4];
        readSomeBytes(input, number, 4);
        return ByteBuffer.wrap(number).getInt();
    }

    private static String readString(InputStream input, int len, long restToRead) throws DatabaseException {
        if (len < 0) {
            throw new DatabaseException("Invalid database file", "Negative string length");
        } else if ((long) len > restToRead) {
            throw new DatabaseException("Invalid database file", "Length of file to read is greater than unread part of file");
        }
        byte[] stringInBytes = new byte[len];
        readSomeBytes(input, stringInBytes, len);
        return new String(stringInBytes, StandardCharsets.UTF_8);
    }

    public static void databaseLoadFromFile(SimpleDatabase dataBase, Path fileToRead) throws DatabaseException {
        InputStream input;
        long fileLength;
        try {
            if (!Files.exists(fileToRead)) {
                return;
            }
            fileLength = Files.size(fileToRead);
            input = Files.newInputStream(fileToRead);
        } catch (UnsupportedOperationException unsuppExc) {
            String reason = "Could not load file: Your system does not support converting Path to InputStream";
            if (unsuppExc.getMessage() != null) {
                reason += ": " + unsuppExc.getMessage();
            }
            throw new DatabaseException("databaseLoader", reason);
        } catch (IOException ioExc) {
            String reason = String.format("Could not load file: Input/Output problems with %s", fileToRead.toString());
            if (ioExc.getMessage() != null) {
                reason += ": " + ioExc.getMessage();
            }
            throw new DatabaseException("databaseLoader", reason);
        } catch (SecurityException secExc) {
            String reason = String.format("Could not load file: Security problems with %s", fileToRead.toString());
            if (secExc.getMessage() != null) {
                reason += ": " + secExc.getMessage();
            }
            throw new DatabaseException("databaseLoader", reason);
        }

        try {
            HashMap<String, String> tmpBase = new HashMap<>();
            while (fileLength > 0) {
                int keyLen = readInt(input);
                int valueLen = readInt(input);
                fileLength -= 8;
                String key = readString(input, keyLen, fileLength);
                fileLength -= keyLen;
                String value = readString(input, valueLen, fileLength);
                fileLength -= valueLen;
                tmpBase.put(key, value);
            }
            Set<Map.Entry<String, String>> mapSet = tmpBase.entrySet();
            for (Map.Entry<String, String> entry : mapSet) {
                dataBase.put(entry.getKey(), entry.getValue());
            }
        } finally {
            try {
                input.close();
            } catch (IOException ignored) {
                // So what???
            }
        }
    }

    private static void writeKeyValuePair(OutputStream output, String key, String value) throws DatabaseException {
        try {
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            byte[] valueBytes = value.getBytes(StandardCharsets.UTF_8);
            byte[] keyLen = ByteBuffer.allocate(4).putInt(keyBytes.length).array();
            byte[] valueLen = ByteBuffer.allocate(4).putInt(valueBytes.length).array();
            output.write(keyLen);
            output.write(valueLen);
            output.write(keyBytes);
            output.write(valueBytes);
        } catch (IOException ioe) {
            throw new DatabaseException("Write data to file: Input/output problems" + ioe.getMessage());
        } catch (Exception e) {
            //Some unimportant exceptions ignored
        }
    }

    public static void databaseWriteToFile(SimpleDatabase database, Path fileToWrite) throws DatabaseException {
        OutputStream output;

        try {
            output = Files.newOutputStream(fileToWrite);
        } catch (UnsupportedOperationException unsuppExc) {
            String reason = "Could not load file: Your system does not support converting Path to OutputStream";
            if (unsuppExc.getMessage() != null) {
                reason += ": " + unsuppExc.getMessage();
            }
            throw new DatabaseException("databaseLoader", reason);
        } catch (IOException ioExc) {
            String reason = String.format("Could not write file: Input/Output problems with %s", fileToWrite.toString());
            if (ioExc.getMessage() != null) {
                reason += ": " + ioExc.getMessage();
            }
            throw new DatabaseException("databaseLoader", reason);
        } catch (SecurityException secExc) {
            String reason = String.format("Could not write file: Security problems with %s", fileToWrite.toString());
            if (secExc.getMessage() != null) {
                reason += ": " + secExc.getMessage();
            }
            throw new DatabaseException("databaseLoader", reason);
        }

        try {
            Set<Map.Entry<String, Object>> databaseSet = database.getEntries();

            for (Map.Entry<String, Object> entry : databaseSet) {
                String key = entry.getKey();
                String valueString;
                Object valueObject = entry.getValue();
                if (valueObject instanceof String) {
                    valueString = (String) valueObject;
                } else {
                    valueString = valueObject.toString();
                }
                writeKeyValuePair(output, key, valueString);
            }
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                // So what??
            }
        }

    }
}
