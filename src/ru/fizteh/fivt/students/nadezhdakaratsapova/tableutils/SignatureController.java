package ru.fizteh.fivt.students.nadezhdakaratsapova.tableutils;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.nadezhdakaratsapova.storeable.StoreableTable;

import java.io.*;
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
                    type.clear();
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
                            System.out.println(typeToReturn);
                            throw new IllegalArgumentException("not allowable type of value in " + SIGNATURE_FILE_NAME);
                    }
                    ++curPos;
                }
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
            System.out.println(cls);
            System.out.println(s);
            throw new IllegalArgumentException("The column required for another type of value");
        }
        return value;
    }

    public String convertStoreableFieldToString(Storeable value, int columnIndex, Class<?> columnType) {
        String ret = null;
        if (value.getColumnAt(columnIndex) != null) {
            switch (columnType.getSimpleName()) {
                case "Integer":
                    ret = value.getIntAt(columnIndex).toString();
                    break;
                case "Long":
                    ret = value.getLongAt(columnIndex).toString();
                    break;
                case "Byte":
                    ret = value.getByteAt(columnIndex).toString();
                    break;
                case "Float":
                    ret = value.getFloatAt(columnIndex).toString();
                    break;
                case "Double":
                    ret = value.getDoubleAt(columnIndex).toString();
                    break;
                case "Boolean":
                    ret = value.getBooleanAt(columnIndex).toString();
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

    public String getPrimitive(Class<?> cls) {
        String primitiveType = null;
        switch (cls.getSimpleName()) {
            case "Integer":
                primitiveType = "int";
                break;
            case "Long":
                primitiveType = "long";
                break;
            case "Byte":
                primitiveType = "byte";
                break;
            case "Float":
                primitiveType = "float";
                break;
            case "Double":
                primitiveType = "double";
                break;
            case "Boolean":
                primitiveType = "boolean";
                break;
            case "String":
                primitiveType = "String";
                break;
            default:
                throw new IllegalArgumentException("Not allowed type of signature");

        }
        return primitiveType;
    }

    private Class<?> getClassFromPrimitive(String primitiveType) {
        Class<?> cls = null;
        if (primitiveType != null) {
            switch (primitiveType) {
                case "int":
                    cls = Integer.class;
                    break;
                case "long":
                    cls = Long.class;
                    break;
                case "byte":
                    cls = Byte.class;
                    break;
                case "float":
                    cls = Float.class;
                    break;
                case "double":
                    cls = Double.class;
                    break;
                case "boolean":
                    cls = Boolean.class;
                    break;
                case "String":
                    cls = String.class;
                    break;
                default:
                    throw new IllegalArgumentException("not allowable type of value in " + SIGNATURE_FILE_NAME);

            }
        }
        return cls;
    }

    public void writeSignatureToFile(File file, List<Class<?>> columnTypes) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(file));
        for (Class<?> cls : columnTypes) {
            outputStream.write((getPrimitive(cls) + ' ').getBytes(StandardCharsets.UTF_8));

        }
        outputStream.close();
    }

    public void checkValueForTable(int columnIndex, Table table, Storeable value) throws IndexOutOfBoundsException, ColumnFormatException {
        if (value.getColumnAt(columnIndex) != null) {
            switch (table.getColumnType(columnIndex).getSimpleName()) {
                case "Integer":
                    value.getIntAt(columnIndex);
                    break;
                case "Long":
                    value.getLongAt(columnIndex);
                    break;
                case "Byte":
                    value.getByteAt(columnIndex);
                    break;
                case "Float":
                    value.getFloatAt(columnIndex);
                    break;
                case "Double":
                    value.getDoubleAt(columnIndex);
                    break;
                case "Boolean":
                    value.getBooleanAt(columnIndex);
                    break;
                case "String":
                    value.getStringAt(columnIndex);
                    break;
                default:
                    throw new ColumnFormatException("Not allowed type of signature");
            }
        }
    }

    public static List<Class<?>> getSignatureFromArgs(String args[]) throws IOException {
        SignatureController signatureController = new SignatureController();
        int argsCount = args.length;
        if ((args[2].charAt(0) != '(') || (args[argsCount - 1].charAt(args[argsCount - 1].length() - 1) != ')')) {
            throw new IOException("The wrong type of command arguments. They should be in brackets");
        }
        List<Class<?>> types = new ArrayList<>();
        String firstType;
        if ((argsCount - 1) == 1) {
            firstType = new String(args[2].substring(1, args[1].length() - 2));
            types.add(signatureController.getClassFromPrimitive(firstType.trim()));
            return types;
        }
        String lastType = null;
        if (args[argsCount - 1].length() == 1) {
            if (args[argsCount - 1] != ")") {
                throw new IOException("The wrong type of command arguments. They should be in brackets");
            }
        } else {
            lastType = new String(args[argsCount - 1].substring(0, args[argsCount - 1].length() - 1));
        }
        firstType = new String(args[2].substring(1));
        types.add(signatureController.getClassFromPrimitive(firstType.trim()));
        for (int i = 3; i < argsCount - 1; ++i) {
            types.add(signatureController.getClassFromPrimitive(args[i].trim()));
        }
        if (lastType != null) {
            types.add(signatureController.getClassFromPrimitive(lastType.trim()));
        }
        return types;
    }
}

