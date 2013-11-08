package ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.nadezhdakaratsapova.storeable.StoreableTable;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SignatureController {
    public static final String SIGNATURE_FILE_NAME = "signature.tsv";

    public List<Class<?>> getSignature(File dataDir) throws IllegalArgumentException, IOException {
        File[] files = dataDir.listFiles();
        List<Class<?>> types = null;
        for (File f : files) {
            if (f.getName().equals(SIGNATURE_FILE_NAME)) {
                if (!f.isFile()) {
                    throw new IllegalArgumentException(SIGNATURE_FILE_NAME + " should be a directory");
                }
            }
            DataInputStream inStream = new DataInputStream(new FileInputStream(f));
            long fileLength = f.length();
            if (fileLength == 0) {
                throw new IllegalArgumentException(SIGNATURE_FILE_NAME + " is empty");
            }
            int curPos = 0;
            byte curByte;
            List<Byte> type = new ArrayList<Byte>();
            types = new ArrayList<Class<?>>();
            while (curPos < fileLength) {
                while ((curPos < fileLength) && ((curByte = inStream.readByte()) != ' ')) {
                    type.add(curByte);
                    ++curPos;
                }
                int arraySize = type.size();
                byte[] typeInBytes = new byte[arraySize];
                for (int j = 0; j < arraySize; ++j) {
                    typeInBytes[j] = type.get(j);
                }
                String typeToReturn = new String(typeInBytes, StandardCharsets.UTF_8);
                switch (typeToReturn) {
                    case "int":
                        types.add(Integer.class);
                        break;
                    case "long":
                        types.add(Long.class);
                        break;
                    case "byte":
                        types.add(Byte.class);
                        break;
                    case "float":
                        types.add(Float.class);
                        break;
                    case "double":
                        types.add(Double.class);
                        break;
                    case "boolean":
                        types.add(Boolean.class);
                        break;
                    case "String":
                        types.add(String.class);
                        break;
                    default:
                        throw new IllegalArgumentException("not allowable type of value in " + SIGNATURE_FILE_NAME);
                }
                ++curPos;
            }
        }

        return types;
    }

    public void checkSignatureValidity(List<Class<?>> columnTypes) {
        for (Class<?> cls : columnTypes) {
            if (cls == null) {
                throw new IllegalArgumentException("Not allowed type of signature");
            }
            switch (cls.getSimpleName()) {
                case "Integer":
                case "Long":
                case "Byte":
                case "Float":
                case "Double":
                case "Boolean":
                case "String":
                    break;
                default:
                    throw new IllegalArgumentException("Not allowed type of signature");
            }
        }
    }

    public Object convertStringToAnotherObject(String s, Class<?> cls) {
        Object value = null;
        try {
            switch (cls.getSimpleName()) {
                case "Integer":
                    value = Integer.parseInt(s);
                    break;
                case "Long":
                    value = Long.parseLong(s);
                    break;
                case "Byte":
                    value = Byte.parseByte(s);
                    break;
                case "Float":
                    value = Float.parseFloat(s);
                    break;
                case "Double":
                    value = Double.parseDouble(s);
                    break;
                case "Boolean":
                    value = Boolean.parseBoolean(s);
                    break;
                case "String":
                    value = s;
                    break;
                default:
                    throw new IllegalArgumentException("Not allowed type of signature");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The column required for another type of value");
        }
        return value;
    }

    public Object checkColumnTypeValidity(Storeable value, int columnIndex, Class<?> columnType) {
        Object ret = null;
        if (value.getColumnAt(columnIndex) != null) {
            switch (columnType.getSimpleName()) {
                case "Integer":
                    ret = value.getIntAt(columnIndex);
                    break;
                case "Long":
                    ret = value.getLongAt(columnIndex);
                    break;
                case "Byte":
                    ret = value.getByteAt(columnIndex);
                    break;
                case "Float":
                    ret = value.getFloatAt(columnIndex);
                    break;
                case "Double":
                    ret = value.getDoubleAt(columnIndex);
                    break;
                case "Boolean":
                    ret = value.getBooleanAt(columnIndex);
                    break;
                case "String":
                    ret = value.getStringAt(columnIndex);
                    break;
                default:
                    throw new IllegalArgumentException("Not allowed type of signature");
            }
        }
        return ret;
    }

    public static List<Class<?>> getColumnTypes(Table table) throws IllegalArgumentException {
        if (table == null) {
            throw new IllegalArgumentException("The null table is not allowed");
        }
        int columnCount = table.getColumnsCount();
        List<Class<?>> types = new ArrayList<Class<?>>();
        for (int i = 0; i < columnCount; ++i) {
            types.add(table.getColumnType(i));
        }
        return types;
    }
}
