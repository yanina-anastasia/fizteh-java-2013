package ru.fizteh.fivt.students.anastasyev.filemap;

import java.io.*;
import java.util.ArrayList;
import java.util.Vector;
import ru.fizteh.fivt.students.anastasyev.shell.Command;
import ru.fizteh.fivt.students.anastasyev.shell.State;

public class FileMap extends State {
    private File fileMap;
    private ArrayList<Element> elementList = new ArrayList<Element>();
    private Vector<Command> commands = new Vector<Command>();

    private class Element {
        private String key;
        private String value;

        public Element(String newKey, String newValue) {
            key = newKey;
            value = newValue;
        }

        public Element(byte[] newKey, byte[] newValue) throws IOException {
            try {
                key = new String(newKey, "UTF-8");
                value = new String(newValue, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new IOException("Error in encoding strings in db.dat");
            }
        }
    }

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
            throw new IOException("db.dat has incorrect format");
        }
        try {
            byte[] key = new byte[keyLength];
            byte[] value = new byte[valueLength];
            input.read(key);
            input.read(value);
            if (key.length != keyLength || value.length != valueLength) {
                throw new IOException("Error in read string in db.dat");
            }
            elementList.add(new Element(key, value));
        } catch (OutOfMemoryError e) {
            throw new IOException("db.dat has incorrect format");
        }
    }

    private void write(RandomAccessFile output, Element element) throws IOException {
        output.writeInt(element.key.getBytes("UTF-8").length);
        output.writeInt(element.value.getBytes("UTF-8").length);
        output.write(element.key.getBytes("UTF-8"));
        output.write(element.value.getBytes("UTF-8"));
    }

    private void openFileMap() throws Exception {
        if (!fileMap.exists()) {
            if (!fileMap.createNewFile()) {
                throw new IOException("Can't create data file db.dat");
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
            throw new FileNotFoundException("db.dat - File not found");
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        } catch (Exception e) {
            throw new Exception("In db.dat something goes very-very wrong");
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    System.err.println("Can't close file db.dat");
                }
            }
        }
    }

    public void saveFileMap() throws IOException {
        RandomAccessFile output = null;
        try {
            output = new RandomAccessFile(fileMap.toString(), "rw");
            output.setLength(0);
            for (Element element : elementList) {
                write(output, element);
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
                    System.err.println("Can't close file db.dat");
                }
            }
        }
    }

    private int find(String key) {
        for (int i = 0; i < elementList.size(); ++i) {
            if (elementList.get(i).key.equals(key)) {
                return i;
            }
        }
        return -1;
    }

    public String put(String newKey, String newValue) throws IOException {
        int index = find(newKey);
        if (index == -1) {
            elementList.add(new Element(newKey, newValue));
            return "new";
        }
        String str = "old " + elementList.get(index).value;
        elementList.get(index).value = newValue;
        return str;
    }

    public String get(String key) {
        int index = find(key);
        if (index == -1) {
            return "not found";
        }
        return elementList.get(index).value;
    }

    public String remove(String key) {
        int index = find(key);
        if (index == -1) {
            return "not found";
        }
        elementList.remove(index);
        return "removed";
    }

    public final Vector<Command> getCommands() {
        return commands;
    }

    public final void addCommand(Command command) {
        commands.add(command);
    }
}
