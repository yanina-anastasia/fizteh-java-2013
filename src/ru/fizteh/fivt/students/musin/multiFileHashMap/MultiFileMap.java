package ru.fizteh.fivt.students.musin.multiFileHashMap;

import ru.fizteh.fivt.students.musin.filemap.FileMap;
import ru.fizteh.fivt.students.musin.shell.Shell;

import java.io.File;
import java.util.ArrayList;

public class MultiFileMap {
    File location;
    FileMap[][] map;
    final int size;

    public MultiFileMap(File location, int size) {
        this.location = location;
        this.size = size;
        map = new FileMap[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                String relative = String.format("%d.dir/%d.dat", i, j);
                File path = new File(location, relative);
                map[i][j] = new FileMap(path);
            }
        }
    }

    public void clear() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                map[i][j].clear();
            }
        }
    }

    public File getFile() {
        return location;
    }

    public boolean validate() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (String key : map[i][j].getKeysList()) {
                    int hashCode = key.hashCode();
                    int dir = (hashCode % 16 + 16) % 16;
                    int file = ((hashCode / 16 % 16) + 16) % 16;
                    if (dir != i || file != j) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean loadFromDisk() {
        clear();
        if (!location.getParentFile().exists() || !location.getParentFile().isDirectory()) {
            System.err.println("Unable to create a table in specified directory: directory doesn't exist");
            return false;
        }
        if (!location.exists()) {
            return true;
        }
        if (location.exists() && !location.isDirectory()) {
            System.err.println("Specified location is not a directory");
            return false;
        }
        for (int dir = 0; dir < size; dir++) {
            String relative = String.format("%d.dir", dir);
            File directory = new File(location, relative);
            if (directory.exists() && !directory.isDirectory()) {
                System.err.printf("%s is not a directory\n", relative);
                return false;
            }
            if (directory.exists()) {
                for (int file = 0; file < size; file++) {
                    File db = map[dir][file].getFile();
                    if (db.exists()) {
                        if (!map[dir][file].loadFromDisk()) {
                            System.err.printf("Error in file %d.dir/%d.dat\n", dir, file);
                            return false;
                        }
                    }
                }
            }
        }
        if (!validate()) {
            System.err.println("Wrong data format: key distribution among files is incorrect");
            return false;
        }
        return true;
    }

    public boolean writeToDisk() throws Exception {
        if (location.exists() && !location.isDirectory()) {
            System.err.println("Database can't be written to the specified location");
            return false;
        }
        if (!location.exists()) {
            if (!location.mkdir()) {
                System.err.println("Unable to create a directory for database");
                return false;
            }
        }
        for (int dir = 0; dir < size; dir++) {
            boolean dirRequired = false;
            for (int file = 0; file < size; file++) {
                if (!map[dir][file].empty()) {
                    dirRequired = true;
                    break;
                }
            }
            String relative = String.format("%d.dir", dir);
            File directory = new File(location, relative);
            if (directory.exists() && !directory.isDirectory()) {
                System.err.printf("%s is not a directory\n", relative);
                return false;
            }
            if (!directory.exists() && dirRequired) {
                if (!directory.mkdir()) {
                    System.err.printf("Can't create directory %s\n", relative);
                    return false;
                }
            }
            if (directory.exists()) {
                for (int file = 0; file < size; file++) {
                    File db = map[dir][file].getFile();
                    if (map[dir][file].empty()) {
                        if (db.exists()) {
                            if (!db.delete()) {
                                System.err.printf("Can't delete file %s\n", db.getCanonicalPath());
                            }
                        }
                    } else {
                        if (!map[dir][file].writeToDisk()) {
                            System.err.printf("Error in file %d.dir/%d.dat\n", dir, file);
                            return false;
                        }
                    }
                }
                if (directory.listFiles().length == 0) {
                    if (!directory.delete()) {
                        System.err.printf("Can't delete directory %s\n", directory.getCanonicalPath());
                    }
                }
            }
        }
        return true;
    }

    public String put(String key, String value) {
        int hashCode = key.hashCode();
        int dir = (hashCode % 16 + 16) % 16;
        int file = ((hashCode / 16 % 16) + 16) % 16;
        return map[dir][file].put(key, value);
    }

    public String get(String key) {
        int hashCode = key.hashCode();
        int dir = (hashCode % 16 + 16) % 16;
        int file = ((hashCode / 16 % 16) + 16) % 16;
        return map[dir][file].get(key);
    }

    public boolean remove(String key) {
        int hashCode = key.hashCode();
        int dir = (hashCode % 16 + 16) % 16;
        int file = ((hashCode / 16 % 16) + 16) % 16;
        return map[dir][file].remove(key);
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
