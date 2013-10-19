package ru.fizteh.fivt.students.ichalovaDiana.filemap;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.Vector;

import ru.fizteh.fivt.students.ichalovaDiana.shell.Command;
import ru.fizteh.fivt.students.ichalovaDiana.shell.Interpreter;

public class FileMap {

    private static final int OFFSET_BYTES = 4;

    static RandomAccessFile dbFile;
    static Hashtable<String, String> database = new Hashtable<String, String>();

    private static Hashtable<String, Command> commands = new Hashtable<String, Command>();
    private static Interpreter interpreter;

    static {
        commands.put("put", new Put());
        commands.put("get", new Get());
        commands.put("remove", new Remove());
        commands.put("exit", new Exit());

        interpreter = new Interpreter(commands);

        try {
            dbFile = new RandomAccessFile(Paths.get(System.getProperty("fizteh.db.dir"))
                    .resolve("db.dat").toFile(), "rw");
            getDataFromFile();
        } catch (Exception e) {
            System.out.println("Error while opening database: "
                    + ((e.getMessage() != null) ? e.getMessage() : "unkonown error"));
            try {
                if (dbFile != null) {
                    dbFile.close();
                }
            } catch (IOException e1) {
                System.out.println("Error while closing database: "
                    + ((e1.getMessage() != null) ? e1.getMessage() : "unkonown error"));
            }
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        interpreter.run(args);
        
        int exitCode = 0;
        
        try {
            saveChanges();
        } catch (Exception e) {
            exitCode = 1;
            System.out.println("Error while saving changes: " + e.getMessage()); 
        }
        
        try {
            if (dbFile != null) {
                dbFile.close();
            }
        } catch (IOException e) {
            System.out.println("Error while closing database: "
                + ((e.getMessage() != null) ? e.getMessage() : "unkonown error"));
        } 
        
        System.exit(exitCode);
    }

    static void getDataFromFile() throws IOException {
        String key;
        String value;
        Vector<Byte> tempKey = new Vector<Byte>();
        int tempOffset1;
        int tempOffset2;
        long currentPosition;
        byte tempByte;
        byte[] tempArray;

        dbFile.seek(0);
        while (dbFile.getFilePointer() != dbFile.length()) {
            tempByte = dbFile.readByte();
            if (tempByte != '\0') {
                tempKey.add(tempByte);
            } else {
                tempOffset1 = dbFile.readInt();
                currentPosition = dbFile.getFilePointer();
                while (dbFile.readByte() != '\0'
                        && dbFile.getFilePointer() != dbFile.length());
                if (dbFile.getFilePointer() == dbFile.length()) {
                    tempOffset2 = (int) dbFile.length();
                } else {
                    tempOffset2 = dbFile.readInt();
                }
                dbFile.seek(tempOffset1);
                tempArray = new byte[tempOffset2 - tempOffset1];
                dbFile.readFully(tempArray);
                value = new String(tempArray, "UTF-8");
                tempArray = new byte[tempKey.size()];
                for (int i = 0; i < tempKey.size(); ++i) {
                    tempArray[i] = tempKey.elementAt(i).byteValue();
                }
                key = new String(tempArray, "UTF-8");
                database.put(key, value);
                tempKey.clear();
                dbFile.seek(currentPosition);
            }
        }
    }

    static void saveChanges() throws IOException {
        int currentOffset = 0;
        long returnPosition;
        String value;

        dbFile.setLength(0);

        for (String key : database.keySet()) {
            currentOffset += key.getBytes("UTF-8").length + OFFSET_BYTES + 1;
        }

        for (String key : database.keySet()) {
            dbFile.write(key.getBytes("UTF-8"));
            dbFile.writeByte(0);
            dbFile.writeInt(currentOffset);
            value = database.get(key);
            returnPosition = dbFile.getFilePointer();
            dbFile.seek(currentOffset);
            dbFile.write(value.getBytes("UTF-8"));

            currentOffset += value.getBytes("UTF-8").length;
            dbFile.seek(returnPosition);
        }
    }
}

class Put extends Command {
    static final int ARG_NUM = 3;

    @Override
    protected void execute(String... arguments) throws Exception {
        try {
            if (arguments.length < ARG_NUM) {
                throw new IllegalArgumentException("Illegal number of arguments");
            }

            String key = arguments[1];

            String value;

            StringBuilder concatArgs = new StringBuilder();
            for (int i = 2; i < arguments.length; ++i) {
                concatArgs.append(arguments[i]).append(" ");
            }
            value = concatArgs.toString();

            if (key.contains("\0") || value.contains("\0")) {
                throw new IllegalArgumentException("null byte in key or value");
            }

            String oldValue = FileMap.database.put(key, value);

            if (oldValue != null) {
                System.out.println("overwrite");
                System.out.println(oldValue);
            } else {
                System.out.println("new");
            }

        } catch (Exception e) {
            throw new Exception(arguments[0] + ": " + e.getMessage());
        }
    }
}

class Get extends Command {
    static final int ARG_NUM = 2;

    @Override
    protected void execute(String... arguments) throws Exception {
        try {
            if (arguments.length != ARG_NUM) {
                throw new IllegalArgumentException("Illegal number of arguments");
            }

            String key = arguments[1];

            String value = FileMap.database.get(key);

            if (value != null) {
                System.out.println("found");
                System.out.println(value);
            } else {
                System.out.println("not found");
            }

        } catch (Exception e) {
            throw new Exception(arguments[0] + ": " + e.getMessage());
        }
    }
}

class Remove extends Command {
    static final int ARG_NUM = 2;

    @Override
    protected void execute(String... arguments) throws Exception {
        try {
            if (arguments.length != ARG_NUM) {
                throw new IllegalArgumentException("Illegal number of arguments");
            }

            String key = arguments[1];

            String value = FileMap.database.remove(key);

            if (value != null) {
                System.out.println("removed");
            } else {
                System.out.println("not found");
            }

        } catch (Exception e) {
            throw new Exception(arguments[0] + ": " + e.getMessage());
        }
    }
}

class Exit extends Command {
    static final int ARG_NUM = 1;

    @Override
    protected void execute(String... arguments) throws Exception {
        try {
            if (arguments.length != ARG_NUM) {
                throw new IllegalArgumentException("Illegal number of arguments");
            }

            FileMap.saveChanges();
            FileMap.dbFile.close();
            System.out.println("exit");
            System.exit(0);

        } catch (Exception e) {
            throw new Exception(arguments[0] + ": " + e.getMessage());
        }
    }
}
