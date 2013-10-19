package ru.fizteh.fivt.students.dmitryKonturov.dbTask.fileMap;

import ru.fizteh.fivt.students.dmitryKonturov.shell.ShellEmulator;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class FileMapShell extends ShellEmulator{
    private final Path dbFilePath;
    protected final String dbName;
    private Map<String, String> dbMap;

    private class PutCommand implements ShellCommand {
        @Override
        public String getName() {
            return "put";
        }

        @Override
        public void execute(String[] args) throws ShellException {
            if (args.length != 1) {
                throw new ShellException(getName(), "Bad arguments");
            }

            String[] realArgs;

            realArgs = getKeyValueArgPair(args[0]); // two arguments
            if (realArgs.length != 2) {
                throw new ShellException(getName(), "Not enough arguments");
            }

            String key = realArgs[0];
            String value = realArgs[1];
            String oldValue = dbMap.put(key, value);
            if (oldValue != null) {
                System.out.println("overwrite");
                System.out.println(oldValue);
            } else {
                System.out.println("new");
            }
        }
    }

    private class GetCommand implements ShellCommand {
        @Override
        public String getName() {
            return "get";
        }

        @Override
        public void execute(String[] args) throws ShellException {
            if (args.length != 1) {
                throw new ShellException(getName(), "Bad arguments");
            }
            String[] realArgs;
            realArgs = getKeyValueArgPair(args[0]);
            if (realArgs.length != 1) {
                throw new ShellException(getName(), "Bad arguments");
            }

            String key = realArgs[0];
            String value = dbMap.get(key);
            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("found");
                System.out.println(value);
            }
        }
    }

    private class RemoveCommand implements ShellCommand {
        @Override
        public String getName() {
            return "remove";
        }

        @Override
        public void execute(String[] args) throws ShellException {
            if (args.length != 1) {
                throw new ShellException(getName(), "Bad arguments");
            }
            String[] realArgs;
            realArgs = getKeyValueArgPair(args[0]);

            if (realArgs.length != 1) {
                throw new ShellException(getName(), "Bad arguments");
            }

            String key = realArgs[0];
            String value = dbMap.remove(key);
            if (value != null) {
                System.out.println("removed");
            } else {
                System.out.println("not found");
            }
        }
    }

    private class ExitCommand implements ShellCommand {
        @Override
        public String getName() {
            return "exit";
        }

        @Override
        public void execute(String[] args) {
            try {
                closeDbFile();
            } catch (Exception ignored) {

            }
            System.exit(0);
        }
    }

    private String[] getKeyValueArgPair(String bigArg) throws ShellException {
        String[] tmpStr = bigArg.split("\\s", 2);
        if (tmpStr.length >= 2) {
            tmpStr[1] = tmpStr[1].trim();
        }
        if (tmpStr.length > 2) {
            throw new ShellException("", "Bad arguments");
        }
        return tmpStr;
    }

    @Override
    protected String[] shellParseArguments(String bigArg) {
        bigArg = bigArg.trim();
        String[] args;
        if (bigArg.length() == 0) {
            args = new String[0];
        } else {
            args = new String[1];
            args[0] = bigArg;
        }
        return args;
    }

    @Override
    public void packageMode(String query) throws ShellException {
        super.packageMode(query);
        try {
            closeDbFile();
        } catch (FileNotFoundException e) {
            throw new ShellException("closeDb", "File not found.");
        }
    }

    private void closeDbFile() throws FileNotFoundException, ShellException {
        FileOutputStream output = new FileOutputStream(dbFilePath.toFile());
        try {
            for (Map.Entry<String, String> entry : dbMap.entrySet()) {
                byte[] key = entry.getKey().getBytes("UTF-8");
                byte[] value = entry.getValue().getBytes("UTF-8");
                int keyLen = key.length;
                int valueLen = value.length;
                output.write(ByteBuffer.allocate(4).putInt(keyLen).array());
                output.write(ByteBuffer.allocate(4).putInt(valueLen).array());
                output.write(key);
                output.write(value);
            }
        } catch (UnsupportedEncodingException e) {
            throw new ShellException("dbclose", "utf is unsupported");
        } catch (IOException e) {
            throw new ShellException("dbclose", "Couldn't write to file. Data was lost");
        } finally {
            try{
                output.close();
            } catch (IOException e) {
                System.err.println("Bogus close file. Maybe some data was lost.");
            }
        }
    }

    private int readInt(FileInputStream input) throws IOException {
        byte[] number = new byte[4];
        int wasRead = input.read(number);
        if (wasRead < 4) {
            throw new IOException("Cannot read int");
        }
        return ByteBuffer.wrap(number).getInt();
    }

    private void loadDbFileToMap() throws IOException, ShellException {
        try (FileInputStream input = new FileInputStream(dbFilePath.toFile())) {
            while (input.available() > 0) {
                int keyLen = readInt(input);
                int valueLen = readInt(input);
                byte[] key = new byte[keyLen];
                int numRead = input.read(key);
                if (numRead < keyLen) {
                    throw new ShellException("loadFile", "Not all data was read");
                }
                byte[] value = new byte[valueLen];
                numRead = input.read(value);
                if (numRead < valueLen) {
                    throw new ShellException("loadFile", "Not all data was read");
                }

                String keyStr = new String(key, "UTF-8");
                String valueStr = new String(value, "UTF-8");
                if (dbMap.put(keyStr, valueStr) != null) {
                    throw new ShellException("loadFile", "Not all keys are different");
                }
            }
        } catch (UnsupportedEncodingException e) {
            System.err.println("UTF-8 encoding isn't supported");
            System.exit(10);
        }
    }

    public FileMapShell(Path dbDir, String dbName) throws IOException {
        ShellCommand[] commandList = new ShellCommand[]{new PutCommand(),
                                                        new GetCommand(),
                                                        new RemoveCommand(),
                                                        new ExitCommand()
                                                       };
        replaceCommandList(commandList);
        this.dbName = dbName;
        this.dbFilePath = dbDir.resolve(dbName);
        this.dbMap = new HashMap<>();
        try {
            loadDbFileToMap();
        } catch (ShellException e) {
            System.err.println(e.toString());
        } catch (FileNotFoundException e) {
            System.err.println("File not found");
        }
    }
}
