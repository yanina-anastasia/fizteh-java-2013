package ru.fizteh.fivt.students.dmitryKonturov.dbTask.fileMap;

import ru.fizteh.fivt.students.dmitryKonturov.shell.ShellEmulator;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class FileMapShell extends ShellEmulator {
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

    //-------------------------------------------------------
    /*
    private class CreateCommand implements ShellCommand {
        @Override
        public String getName() {
            return "create";
        }

        @Override
        public void execute(String[] args) {
            //todo
        }
    }

    private class DropCommand implements ShellCommand {
        @Override
        public String getName() {
            return "drop";
        }

        @Override
        public void execute(String[] args) {
            //todo
        }
    }

    private class UseCommand implements ShellCommand {
        @Override
        public String getName() {
            return "use";
        }

        @Override
        public void execute(String[] args) {
            //todo
        }
    }


    */
    //-----------------------------------------------------------------------------
    //-----------------------------------------------------------------------------

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
        } catch (FileNotFoundException ignored) {

        } catch (ShellException e) {
            System.err.println(e.toString());
        }
    }

    @Override
    public void interactiveMode() {
        super.interactiveMode();
        try {
            closeDbFile();
        } catch (FileNotFoundException ignored) {

        } catch (ShellException e) {
            System.err.println(e.toString());
        }
    }

    public void closeDbFile() throws FileNotFoundException, ShellException {
        FileOutputStream output = new FileOutputStream(dbFilePath.toFile());
        try {
            for (Map.Entry<String, String> entry : dbMap.entrySet()) {
                byte[] key = entry.getKey().getBytes(StandardCharsets.UTF_8);
                byte[] value = entry.getValue().getBytes(StandardCharsets.UTF_8);
                int keyLen = key.length;
                int valueLen = value.length;
                output.write(ByteBuffer.allocate(4).putInt(keyLen).array());
                output.write(ByteBuffer.allocate(4).putInt(valueLen).array());
                output.write(key);
                output.write(value);
            }
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

    private void myRead(FileInputStream input, byte[] toRead, int length) throws IOException {
        int wasRead = 0;
        while (wasRead < length) {
            int tmpRead = input.read(toRead, wasRead, length - wasRead);
            if (tmpRead == -1) {
                throw new EOFException();
            }
            wasRead += tmpRead;
        }
    }

    private int readInt(FileInputStream input) throws IOException {
        byte[] number = new byte[4];
        myRead(input, number, 4);
        return ByteBuffer.wrap(number).getInt();
    }

    private void loadDbFileToMap() throws IOException, ShellException {
        FileInputStream input = new FileInputStream(dbFilePath.toFile());
        long bytesToRead;
        try{
            bytesToRead = dbFilePath.toFile().length();
        } catch (SecurityException e) {
            throw new ShellException("Load file", "Access denied");
        }
        while (bytesToRead > 0) {
            int keyLen = readInt(input);
            bytesToRead -= 4;
            if (keyLen < 0) {
                throw new ShellException("Load file", "Negative key length");
            } else if ((long) keyLen > bytesToRead) {
                throw new ShellException("Load file", "Key length is bigger than file length.");
            }
            int valueLen = readInt(input);
            bytesToRead -= 4;
            if (valueLen < 0) {
                throw new ShellException("Load file", "Negative value length");
            } else if ((long) valueLen > bytesToRead) {
                throw new ShellException("Load file", "Value length is greater than the length of rest file");
            }

            if ((long) (keyLen + valueLen) > bytesToRead) {
                throw  new ShellException("Load file", "Incorrect key or value length");
            }

            byte[] key = new byte[keyLen];
            byte[] value = new byte[valueLen];
            myRead(input, key, keyLen);
            myRead(input, value, valueLen);

            bytesToRead -= (long) (keyLen + valueLen);

            String keyStr = new String(key, StandardCharsets.UTF_8);
            String valueStr = new String(value, StandardCharsets.UTF_8);
            if (dbMap.put(keyStr, valueStr) != null) {
                throw new ShellException("loadFile", "Not all keys are different");
            }
        }

    }

    public FileMapShell(Path dbDir, String dbName) throws ShellException {
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
            throw new ShellException("Incorrect input file", e.toString());
        } catch (FileNotFoundException e) {
            System.err.println("File not found: data wasn't read");
        } catch (IOException e) {
            throw new ShellException("Fail to load database", "IOExceptinon");
        }
    }
}
