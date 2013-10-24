package ru.fizteh.fivt.students.anastasyev.filemap;

import java.io.*;
import java.util.*;

import ru.fizteh.fivt.students.anastasyev.shell.Command;
import ru.fizteh.fivt.students.anastasyev.shell.State;

public class FileMap extends State {
    private File fileMap;
    private HashMap<String, String> elementHashMap = new HashMap<String, String>();
    private Vector<Command> commands = new Vector<Command>();
    private int ndirectory;
    private int nfile;

    public FileMap(String dbDir) {
        fileMap = new File(dbDir);
        try {
            openFileMap();
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        commands.add(new PutCommand());
        commands.add(new GetCommand());
        commands.add(new RemoveCommand());
        commands.add(new FileMapExitCommand());
    }

    private void read(RandomAccessFile input) throws IOException {
        int keyLength;
        int valueLength;
        try {
            keyLength = input.readInt();
            valueLength = input.readInt();
        } catch (IOException e) {
            throw new IOException("Error in key/value reading");
        }
        if (keyLength <= 0 || valueLength <= 0) {
            throw new IOException(nfile + ".dat has incorrect format");
        }
        try {
            byte[] keyBytes = new byte[keyLength];
            byte[] valueBytes = new byte[valueLength];
            input.read(keyBytes);
            input.read(valueBytes);
            if (keyBytes.length != keyLength || valueBytes.length != valueLength) {
                throw new IOException("Error in read strings in " + nfile + ".dat");
            }
            String key = new String(keyBytes);
            String value = new String(valueBytes);
            elementHashMap.put(key, value);
        } catch (OutOfMemoryError e) {
            throw new IOException(nfile + ".dat has incorrect format");
        }
    }

    public FileMap(String dbDir, int directory, int file) {
        fileMap = new File(dbDir);
        ndirectory = directory;
        nfile = file;
        try {
            openFileMapWithCheck();
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        commands.add(new PutCommand());
        commands.add(new GetCommand());
        commands.add(new RemoveCommand());
        commands.add(new FileMapExitCommand());
    }

    private void openFileMapWithCheck() throws Exception {
        if (!fileMap.exists()) {
            if (!fileMap.createNewFile()) {
                throw new IOException("Can't create data file " + nfile + ".dat");
            }
        }
        RandomAccessFile input = null;
        try {
            input = new RandomAccessFile(fileMap.toString(), "r");
            if (input.length() == 0) {
                return;
            }
            while (input.getFilePointer() < input.length()) {
                readWithCheck(input);
            }
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(nfile + ".dat - File not found");
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } catch (Exception e) {
            throw new Exception("In " + nfile + ".dat something goes very-very wrong");
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    System.err.println("Can't close file " + nfile + ".dat");
                }
            }
        }
    }

    private void readWithCheck(RandomAccessFile input) throws IOException {
        int keyLength;
        int valueLength;
        try {
            keyLength = input.readInt();
            valueLength = input.readInt();
        } catch (IOException e) {
            throw new IOException("Error in key/value reading");
        }
        if (keyLength <= 0 || valueLength <= 0) {
            throw new IOException(nfile + ".dat has incorrect format");
        }
        try {
            byte[] keyBytes = new byte[keyLength];
            byte[] valueBytes = new byte[valueLength];
            input.read(keyBytes);
            input.read(valueBytes);
            if (keyBytes.length != keyLength || valueBytes.length != valueLength) {
                throw new IOException("Error in read strings in " + nfile + ".dat");
            }
            String key = new String(keyBytes);
            if ((key.hashCode() % 16 + 16) % 16 != ndirectory || ((key.hashCode() / 16 % 16) + 16) % 16 != nfile) {
                throw new IOException(nfile + ".dat has incorrect format");
            }
            String value = new String(valueBytes);
            elementHashMap.put(key, value);
        } catch (OutOfMemoryError e) {
            throw new IOException(nfile + ".dat has incorrect format");
        }
    }

    private void write(RandomAccessFile output, String key, String value) throws IOException {
        output.writeInt(key.getBytes("UTF-8").length);
        output.writeInt(value.getBytes("UTF-8").length);
        output.write(key.getBytes("UTF-8"));
        output.write(value.getBytes("UTF-8"));
    }

    private void openFileMap() throws Exception {
        if (!fileMap.exists()) {
            if (!fileMap.createNewFile()) {
                throw new IOException("Can't create data file " + nfile + ".dat");
            }
        }
        RandomAccessFile input = null;
        try {
            input = new RandomAccessFile(fileMap.toString(), "r");
            if (input.length() == 0) {
                return;
            }
            while (input.getFilePointer() < input.length()) {
                read(input);
            }
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(nfile + ".dat - File not found");
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } catch (Exception e) {
            throw new Exception("In " + nfile + ".dat something goes very-very wrong");
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    System.err.println("Can't close file " + nfile + ".dat");
                }
            }
        }
    }

    public void save() throws IOException {
        RandomAccessFile output = null;
        try {
            output = new RandomAccessFile(fileMap.toString(), "rw");
            output.setLength(0);
            Set<Map.Entry<String, String>> hashMapSet = elementHashMap.entrySet();
            for (Map.Entry<String, String> element : hashMapSet) {
                write(output, element.getKey(), element.getValue());
            }
        } catch (FileNotFoundException e) {
            throw new IOException("Can't find file to save");
        } catch (Exception e) {
            throw new IOException("Can't save FileMap");
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    System.err.println("Can't close file " + nfile + ".dat");
                }
            }
        }
    }

    public String put(String newKey, String newValue) throws IOException {
        String oldValue = elementHashMap.put(newKey, newValue);
        if (oldValue == null) {
            return "new";
        }
        String str = "old " + oldValue;
        return str;
    }

    public String get(String key) {
        String value = elementHashMap.get(key);
        if (value == null) {
            return "not found";
        }
        return value;
    }

    public String remove(String key) {
        if (elementHashMap.get(key) == null) {
            return "not found";
        }
        elementHashMap.remove(key);
        return "removed";
    }

    @Override
    public final Vector<Command> getCommands() {
        return commands;
    }

    public final void addCommand(Command command) {
        commands.add(command);
    }

    @Override
    public FileMap getMyState(int hashCode) {
        return this;
    }

    public boolean isEmpty() {
        return elementHashMap.isEmpty();
    }

    public void delete() throws IOException {
        File fileMapParent = fileMap.getParentFile();
        if (!fileMap.delete()) {
            throw new IOException("Can't remove empty fileMap");
        }
        if (fileMapParent.listFiles().length == 0) {
            if (!fileMapParent.delete()) {
                throw new IOException("Can't remove empty fileMaps directory");
            }
        }
    }
}
