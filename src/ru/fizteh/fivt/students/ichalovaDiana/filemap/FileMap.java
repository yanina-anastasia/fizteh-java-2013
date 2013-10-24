package ru.fizteh.fivt.students.ichalovaDiana.filemap;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Hashtable;
import java.util.Vector;

import ru.fizteh.fivt.students.ichalovaDiana.shell.Command;
import ru.fizteh.fivt.students.ichalovaDiana.shell.Interpreter;

public class FileMap {

    private static Hashtable<String, Command> commands = new Hashtable<String, Command>();
    private static Interpreter interpreter;

    private static Path dbDir;
    private static String currentTableName;

    static {
        try {
            
            dbDir = Paths.get(System.getProperty("fizteh.db.dir"));
    
            if (!Files.isDirectory(dbDir)) {
                throw new Exception(dbDir + " doesn't exist or is not a directory");
            }
            
        } catch (Exception e) {
            System.out.println("Error while opening database: "
                    + ((e.getMessage() != null) ? e.getMessage() : "unkonown error"));
            System.exit(1);
            
        }

        commands.put("create", new Create());
        commands.put("drop", new Drop());
        commands.put("use", new Use());
        commands.put("put", new Put());
        commands.put("get", new Get());
        commands.put("remove", new Remove());
        commands.put("exit", new Exit());

        interpreter = new Interpreter(commands);
    }

    public static void main(String[] args) {
        try {
            interpreter.run(args);
        } catch (Exception e) {
            System.out.println("Error while running: " + e.getMessage());
            
        }
    }
    
    static class DirectoryAndFileNumberCalculator {
        
        static int getnDirectory(String key) {
            int firstByte = Math.abs(key.getBytes()[0]);
            int nDirectory = firstByte % 16;
            return nDirectory;
        }
        
        static int getnFile(String key) {
            int firstByte = Math.abs(key.getBytes()[0]);
            int nFile = firstByte / 16 % 16;
            return nFile;
        }
    }

    static class Create extends Command {
        static final int ARG_NUM = 2;

        @Override
        protected void execute(String... arguments) throws Exception {
            try {

                if (arguments.length != ARG_NUM) {
                    throw new IllegalArgumentException("Illegal number of arguments");
                }

                String tableName = arguments[1];
                Path tablePath = FileMap.dbDir.resolve(tableName);

                if (!Files.exists(tablePath)) {
                    Files.createDirectory(tablePath);
                    System.out.println("created");
                } else if (Files.isDirectory(tablePath)) {
                    System.out.println(tableName + " exists");
                } else {
                    throw new Exception(tableName + " exists and has illegal format");
                }

            } catch (Exception e) {
                throw new Exception(arguments[0] + ": " + e.getMessage());
            }
        }
    }

    static class Drop extends Command {
        static final int ARG_NUM = 2;

        @Override
        protected void execute(String... arguments) throws Exception {
            try {

                if (arguments.length != ARG_NUM) {
                    throw new IllegalArgumentException("Illegal number of arguments");
                }

                String tableName = arguments[1];
                Path tablePath = FileMap.dbDir.resolve(arguments[1]);

                if (Files.isDirectory(tablePath)) {
                    delete(tablePath);
                    System.out.println("dropped");
                    if (FileMap.currentTableName.equals(tableName)) {
                        FileMap.currentTableName = null;
                    }
                } else if (!Files.exists(tablePath)) {
                    System.out.println(tableName + " not exists");
                } else {
                    throw new Exception(tableName + " exists and has illegal format");
                }

            } catch (Exception e) {
                throw new Exception(arguments[0] + ": " + e.getMessage());
            }
        }

        private void delete(Path path) throws IOException {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException e)
                        throws IOException {
                    if (e == null) {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    } else {
                        throw e;
                    }
                }
            });
        }
    }

    static class Use extends Command {
        static final int ARG_NUM = 2;

        @Override
        protected void execute(String... arguments) throws Exception {
            try {

                if (arguments.length != ARG_NUM) {
                    throw new IllegalArgumentException("Illegal number of arguments");
                }

                String tableName = arguments[1];
                Path tablePath = FileMap.dbDir.resolve(arguments[1]);

                if (Files.isDirectory(tablePath)) {
                    FileMap.currentTableName = tableName;
                    System.out.println("using " + tableName);
                } else if (!Files.exists(tablePath)) {
                    System.out.println(tableName + " not exists");
                } else {
                    throw new Exception(tableName + " exists and has illegal format");
                }

            } catch (Exception e) {
                throw new Exception(arguments[0] + ": " + e.getMessage());
            }
        }
    }
    

    static class Put extends Command {
        static final int ARG_NUM = 3;

        @Override
        protected void execute(String... arguments) throws Exception {
            try {

                if (arguments.length < ARG_NUM) {
                    throw new IllegalArgumentException("Illegal number of arguments");
                }

                String key = arguments[1];

                StringBuilder concatArgs = new StringBuilder();
                for (int i = 2; i < arguments.length; ++i) {
                    concatArgs.append(arguments[i]).append(" ");
                }
                String value = concatArgs.toString();

                if (key.contains("\0") || value.contains("\0")) {
                    throw new IllegalArgumentException("null byte in key or value");
                }
                
                if (FileMap.currentTableName == null) {
                    System.out.println("no table");
                    return;
                }
                
                int nDirectory = DirectoryAndFileNumberCalculator.getnDirectory(key);
                int nFile = DirectoryAndFileNumberCalculator.getnFile(key);
                
                FileDatabase currentDatabase = new FileDatabase(dbDir.resolve(FileMap.currentTableName)
                        .resolve(Integer.toString(nDirectory) + ".dir").resolve(Integer.toString(nFile) + ".dat"));

                currentDatabase.put(key, value);
                
                currentDatabase.close();
                
            } catch (Exception e) {
                throw new Exception(arguments[0] + ": " + e.getMessage());
            }
        }
    }

    static class Get extends Command {
        static final int ARG_NUM = 2;

        @Override
        protected void execute(String... arguments) throws Exception {
            try {

                if (arguments.length != ARG_NUM) {
                    throw new IllegalArgumentException("Illegal number of arguments");
                }

                String key = arguments[1];
                
                if (FileMap.currentTableName == null) {
                    System.out.println("no table");
                    return;
                }

                int nDirectory = DirectoryAndFileNumberCalculator.getnDirectory(key);
                int nFile = DirectoryAndFileNumberCalculator.getnFile(key);
                
                FileDatabase currentDatabase = new FileDatabase(dbDir.resolve(FileMap.currentTableName)
                        .resolve(Integer.toString(nDirectory) + ".dir").resolve(Integer.toString(nFile) + ".dat"));

                currentDatabase.get(key);
                
                currentDatabase.close();

            } catch (Exception e) {
                throw new Exception(arguments[0] + ": " + e.getMessage());
            }
        }
    }

    static class Remove extends Command {
        static final int ARG_NUM = 2;

        @Override
        protected void execute(String... arguments) throws Exception {
            try {

                if (arguments.length != ARG_NUM) {
                    throw new IllegalArgumentException("Illegal number of arguments");
                }

                String key = arguments[1];
                
                if (FileMap.currentTableName == null) {
                    System.out.println("no table");
                    return;
                }

                int nDirectory = DirectoryAndFileNumberCalculator.getnDirectory(key);
                int nFile = DirectoryAndFileNumberCalculator.getnFile(key);
                
                FileDatabase currentDatabase = new FileDatabase(dbDir.resolve(FileMap.currentTableName)
                        .resolve(Integer.toString(nDirectory) + ".dir").resolve(Integer.toString(nFile) + ".dat"));

                currentDatabase.remove(key);
                System.out.println("AAA");
                currentDatabase.close();

            } catch (Exception e) {
                throw new Exception(arguments[0] + ": " + e.getMessage());
            }
        }
    }

    static class Exit extends Command {
        static final int ARG_NUM = 1;

        @Override
        protected void execute(String... arguments) throws Exception {
            try {

                if (arguments.length != ARG_NUM) {
                    throw new IllegalArgumentException("Illegal number of arguments");
                }

                System.out.println("exit");
                System.exit(0);

            } catch (Exception e) {
                throw new Exception(arguments[0] + ": " + e.getMessage());
            }
        }
    }
    
}


class FileDatabase {
    private static final int OFFSET_BYTES = 4;

    Path dbFilePath;
    RandomAccessFile dbFile;
    Hashtable<String, String> database = new Hashtable<String, String>();

    FileDatabase(Path dbFilePath) {
        try {
            this.dbFilePath = dbFilePath;
            Files.createDirectories(dbFilePath.getParent());
            
            dbFile = new RandomAccessFile(dbFilePath.toFile(), "rw");
            getDataFromFile();
            
        } catch (Exception e) {
            
            System.out.println("Error while opening database file: "
                    + ((e.getMessage() != null) ? e.getMessage() : "unkonown error"));
            try {
                if (dbFile != null) {
                    dbFile.close();
                }
            } catch (IOException e1) {
                System.out
                        .println("Error while closing database: "
                                + ((e1.getMessage() != null) ? e1.getMessage()
                                        : "unkonown error"));
            }
            System.exit(1);
            
        }
    }

    void put(String key, String value) throws Exception {
    
        String oldValue = database.put(key, value);

        saveChanges();
        
        if (oldValue != null) {
            System.out.println("overwrite");
            System.out.println(oldValue);
        } else {
            System.out.println("new");
        }
    }
    
    void get(String key) throws Exception {

        String value = database.get(key);

        if (value != null) {
            System.out.println("found");
            System.out.println(value);
        } else {
            System.out.println("not found");
        }
        
    }
    
    void remove(String key) throws Exception {
        
        String value = database.remove(key);

        if (value != null) {
            saveChanges();
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
        
    }

    void getDataFromFile() throws IOException {
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

    void saveChanges() throws IOException {
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
    
    void close() throws Exception {
        if (dbFile.length() == 0) {
            dbFile.close();
            Files.delete(dbFilePath);
            if (dbFilePath.getParent().toFile().list().length == 0) {
                Files.delete(dbFilePath.getParent());
            }
        }
        dbFile.close();
    }
}

