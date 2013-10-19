package ru.fizteh.fivt.students.musin.filemap;

import ru.fizteh.fivt.students.musin.shell.Shell;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FileMap {
    File location;
    HashMap<String, String> map;
    final int maxLength;

    public FileMap(File location) {
        this.location = location;
        map = new HashMap<String, String>();
        maxLength = 1 << 24;
    }

    private int readBytes(DataInputStream input, int bytes, byte[] buffer) throws IOException {
        int len = 0;
        while (len != bytes) {
            int k = input.read(buffer, len, bytes - len);
            if (k == -1) {
                return len;
            }
            len += k;
        }
        return len;
    }

    public boolean loadFromDisk() throws FileNotFoundException {
        map.clear();
        if (!location.getParentFile().exists() || !location.getParentFile().isDirectory()) {
            System.err.println("Unable to create a file, directory doesn't exist");
            return false;
        }
        if (!location.exists()) {
            System.err.println("Database file wasn't found");
            return true;
        }
        DataInputStream inputStream = new DataInputStream(new FileInputStream(location));
        byte[] buffer;
        ByteBuffer cast;
        boolean error = false;
        try {
            while (true) {
                buffer = new byte[4];
                int bytesRead = readBytes(inputStream, 4, buffer);
                if (bytesRead == 0) {
                    break;
                }
                if (bytesRead != 4) {
                    System.err.println("Database loading failed: Wrong key length format");
                    error = true;
                    break;
                }
                cast = ByteBuffer.wrap(buffer);
                int keyLength = cast.getInt();
                bytesRead = readBytes(inputStream, 4, buffer);
                if (bytesRead != 4) {
                    System.err.println("Database loading failed: Wrong value length format");
                    error = true;
                    break;
                }
                cast = ByteBuffer.wrap(buffer);
                int valueLength = cast.getInt();
                if (keyLength > maxLength || valueLength > maxLength) {
                    System.err.println("Database loading failed: Field length too big");
                    error = true;
                    break;
                }
                if (keyLength <= 0 || valueLength <= 0) {
                    System.err.println("Database loading failed: Field length should be positive");
                    error = true;
                    break;
                }
                buffer = new byte[keyLength];
                bytesRead = readBytes(inputStream, keyLength, buffer);
                if (bytesRead != keyLength) {
                    System.err.println("Database loading failed: Wrong key length");
                    error = true;
                    break;
                }
                String key = new String(buffer, "UTF-8");
                buffer = new byte[valueLength];
                bytesRead = readBytes(inputStream, valueLength, buffer);
                if (bytesRead != valueLength) {
                    System.err.println("Database loading failed: Wrong value length");
                    error = true;
                    break;
                }
                String value = new String(buffer, "UTF-8");
                map.put(key, value);
            }
        } catch (IOException e) {
            System.err.println(e);
            error = true;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
        return (!error);
    }

    public void writeToDisk() throws Exception {
        if (location.exists() && location.isDirectory()) {
            System.err.println("Database can't be written to the specified location");
            return;
        }
        if (!location.exists()) {
            if (!location.createNewFile()) {
                System.err.println("Database can't be written to the specified location");
                return;
            }
        }
        FileOutputStream outputStream = new FileOutputStream(location);
        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                byte[] key = entry.getKey().getBytes("UTF-8");
                byte[] value = entry.getValue().getBytes("UTF-8");
                outputStream.write(ByteBuffer.allocate(4).putInt(key.length).array());
                outputStream.write(ByteBuffer.allocate(4).putInt(value.length).array());
                outputStream.write(key);
                outputStream.write(value);
            }
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            outputStream.close();
        }
    }

    public String put(String key, String value) {
        return map.put(key, value);
    }

    public String get(String key) {
        return map.get(key);
    }

    public boolean remove(String key) {
        return (map.remove(key) != null);
    }

    ArrayList<String> parseArguments(int argCount, String argString) {
        ArrayList<String> args = new ArrayList<String>();
        int argsRead = 0;
        String last = "";
        int start = 0;
        for (int i = 0; i < argString.length(); i++) {
            if (Character.isWhitespace(argString.charAt(i))) {
                if (start != i) {
                    args.add(argString.substring(start, i));
                    argsRead++;
                }
                start = i + 1;
                if (argsRead == argCount - 1) {
                    last = argString.substring(start, argString.length());
                    break;
                }
            }
        }
        last = last.trim();
        if (!last.equals("")) {
            args.add(last);
        }
        return args;
    }

    private Shell.ShellCommand[] commands = new Shell.ShellCommand[]{
            new Shell.ShellCommand("put", false, new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    args = parseArguments(2, args.get(0));
                    if (args.size() > 2) {
                        System.err.println("put: Too many arguments");
                        return -1;
                    }
                    if (args.size() < 2) {
                        System.err.println("put: Too few arguments");
                        return -1;
                    }
                    String value = put(args.get(0), args.get(1));
                    if (value == null) {
                        System.out.println("new");
                    } else {
                        System.out.printf("overwrite\n%s\n", value);
                    }
                    return 0;
                }
            }),
            new Shell.ShellCommand("get", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 1) {
                        System.err.println("get: Too many arguments");
                        return -1;
                    }
                    if (args.size() < 1) {
                        System.err.println("get: Too few arguments");
                        return -1;
                    }
                    String value = get(args.get(0));
                    if (value == null) {
                        System.out.println("not found");
                    } else {
                        System.out.printf("found\n%s\n", value);
                    }
                    return 0;
                }
            }),
            new Shell.ShellCommand("remove", new Shell.ShellExecutable() {
                @Override
                public int execute(Shell shell, ArrayList<String> args) {
                    if (args.size() > 1) {
                        System.err.println("remove: Too many arguments");
                        return -1;
                    }
                    if (args.size() < 1) {
                        System.err.println("remove: Too few arguments");
                        return -1;
                    }
                    if (remove(args.get(0))) {
                        System.out.println("removed");
                    } else {
                        System.out.println("not found");
                    }
                    return 0;
                }
            })
    };

    public void integrate(Shell shell) {
        for (int i = 0; i < commands.length; i++) {
            shell.addCommand(commands[i]);
        }
    }
}
