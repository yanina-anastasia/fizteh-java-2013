package ru.fizteh.fivt.students.anastasyev.filemap;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Vector;
import ru.fizteh.fivt.students.anastasyev.shell.Command;

public class FileMap {
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
                throw new IOException("Error in encoding strings");
            }
        }
    }

    public FileMap(String dbDir) {
        fileMap = new File(dbDir);
        try {
            openFileMap();
        } catch (FileNotFoundException e) {
            System.err.println("File not found");
            System.exit(1);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private void read(RandomAccessFile input) throws IOException {
        int keyLength = input.readInt();
        int valueLength = input.readInt();
        byte[] key = new byte[keyLength];
        byte[] value = new byte[valueLength];
        input.read(key);
        input.read(value);
        elementList.add(new Element(key, value));
    }

    private void write(RandomAccessFile output, Element element) throws IOException {
        output.writeInt(element.key.length());
        output.writeInt(element.value.length());
        output.write(element.key.getBytes("UTF-8"));
        output.write(element.value.getBytes("UTF-8"));
    }

    private void openFileMap() throws IOException {
        if (!fileMap.exists()) {
            if (!fileMap.createNewFile()) {
                throw new IOException("Can't create data file db.dat");
            }
        }
        RandomAccessFile input = new RandomAccessFile(fileMap.toString(), "rw");
        if (input.length() == 0) {
            return;
        }
        try {
            while (input.getFilePointer() < input.length()) {
                read(input);
            }
        } catch (OutOfMemoryError e) {
            throw new IOException("db.dat has incorrect format");
        } catch (NegativeArraySizeException e) {
            throw new IOException("db.dat has incorrect format");
        } catch (Exception e) {
            throw new IOException("Error in read db.dat");
        }
        input.close();
    }

    public void saveFileMap() throws IOException {
        RandomAccessFile output;
        try {
            output = new RandomAccessFile(fileMap.toString(), "rw");
            output.setLength(0);
        } catch (FileNotFoundException e) {
            throw new IOException("Can't find file to save");
        } catch (Exception e) {
            throw new IOException("Can't save FileMap");
        }
        for (Element element : elementList) {
            write(output, element);
        }
        output.close();
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
