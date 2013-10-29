package ru.fizteh.fivt.students.nlevashov.filemap;

import ru.fizteh.fivt.students.nlevashov.mode.Mode;
import ru.fizteh.fivt.students.nlevashov.shell.Shell;

import java.util.Vector;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.regex.Pattern;
import java.nio.file.DirectoryStream;

public class FileMap {
    public static Vector<String> parse(String str, String separators) {
        String[] tokens = str.split(separators);
        Vector<String> tokensWithoutEmptyStrings = new Vector<String>();
        for (int i = 0; i < tokens.length; i++) {
            if (!tokens[i].equals("")) {
                tokensWithoutEmptyStrings.add(tokens[i]);
            }
        }
        return tokensWithoutEmptyStrings;
    }

    static Path addrPath;
    static Path currentTable = null;
    static Table[][] parts = new Table[16][16];

    public static void openCurrentTable(Path tablePath) throws Exception {
        currentTable = tablePath;
        for (int i = 0; i < 16; i++) {
            Path dir = currentTable.resolve(Integer.toString(i) + ".dir");
            for (int j = 0; j < 16; j++) {
                parts[i][j] = new Table(dir.resolve(Integer.toString(j) + ".dat"));
            }
        }
    }

    public static void closeCurrentTable() throws Exception {
        if (currentTable != null) {
            for (int i = 0; i < 16; i++) {
                String directoryName = Integer.toString(i) + ".dir";
                if (!Files.exists(currentTable.resolve(directoryName))) {
                    Shell.cd(currentTable.toString());
                    Shell.mkdir(directoryName);
                }
                boolean flag = true;
                for (int j = 0; j < 16; j++) {
                    if (parts[i][j].isEmpty()) {
                        Files.deleteIfExists(parts[i][j].getAddress());
                    } else {
                        parts[i][j].refresh();
                        flag = false;
                    }
                }
                if (flag) {
                    Shell.cd(currentTable.toString());
                    Shell.rm(directoryName);
                }
            }
            currentTable = null;
        }
    }

    public static void create(String tableName) throws Exception {
        if (Files.exists(addrPath.resolve(tableName))) {
            throw new Exception(tableName + " exists");
        } else {
            Shell.cd(addrPath.toString());
            Shell.mkdir(tableName);
            System.out.println("created");
        }
    }

    public static void drop(String tableName) throws Exception {
        if (Files.exists(addrPath.resolve(tableName))) {
            if ((currentTable != null) && (currentTable.getFileName().toString().equals(tableName))) {
                closeCurrentTable();
            }
            Shell.cd(addrPath.toString());
            Shell.rm(tableName);
            System.out.println("dropped");
        } else {
            throw new Exception(tableName + " not exists");
        }
    }

    public static void use(String tableName) throws Exception {
        Path choosenTable = addrPath.resolve(tableName);
        if (Files.exists(choosenTable)) {
            closeCurrentTable();
            openCurrentTable(choosenTable);
            System.out.println("using " + tableName);
        } else {
            throw new Exception(tableName + " not exists");
        }
    }

    public static void put(String key, String value) {
        int hash = key.hashCode();
        hash = hash * Integer.signum(hash);
        String putString = parts[hash % 16][hash / 16 % 16].put(key, value);
        if (putString == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(putString);
        }
    }

    public static void get(String key) {
        int hash = key.hashCode();
        hash = hash * Integer.signum(hash);
        String getString = parts[hash % 16][hash / 16 % 16].get(key);
        if (getString == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(getString);
        }
    }

    public static void remove(String key) {
        int hash = key.hashCode();
        hash = hash * Integer.signum(hash);
        if (parts[hash % 16][hash / 16 % 16].remove(key) == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }

    public static void checkRootDirectory() throws Exception {
        if (!Files.exists(addrPath)) {
            throw new Exception("Directory \"" + addrPath.toString() + "\" doesn't exists");
        }
        if (!Files.isDirectory(addrPath)) {
            throw new Exception("\"" + addrPath.toString() + "\" isn't a directory");
        }

        Pattern levelPattern = Pattern.compile("([0-9]|1[0-5])\\.dir");
        Pattern partPattern = Pattern.compile("([0-9]|1[0-5])\\.dat");

        DirectoryStream<Path> tables = Files.newDirectoryStream(addrPath);
        for (Path table : tables) {
            if (!Files.isDirectory(table)) {
                throw new Exception("there is object which is not a directory in root directory");
            }
            DirectoryStream<Path> levels = Files.newDirectoryStream(table);
            for (Path level : levels) {
                if (!Files.isDirectory(level)) {
                    throw new Exception("there is object which is not a directory in table \""
                            + table.getFileName() + "\"");
                }
                if (!levelPattern.matcher(level.getFileName().toString()).matches()) {
                    throw new Exception("there is directory with wrong name in table \""
                            + table.getFileName() + "\"");
                }
                DirectoryStream<Path> parts = Files.newDirectoryStream(level);
                for (Path part : parts) {
                    if (Files.isDirectory(part)) {
                        throw new Exception("there is object which is not a file in \""
                                + table.getFileName() + "\\" + level.getFileName() + "\"");
                    }
                    if (!partPattern.matcher(part.getFileName().toString()).matches()) {
                        throw new Exception("there is file with wrong name in \""
                                + table.getFileName() + "\\" + level.getFileName() + "\"");
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        String addr = System.getProperty("fizteh.db.dir");
        if (addr == null) {
            System.err.println("Property \"fizteh.db.dir\" wasn't set");
            System.exit(1);
        }
        addrPath = Shell.makePath(addr).toPath();
        try {
            checkRootDirectory();
            Mode.start(args, new Mode.Executor() {
                public boolean execute(String cmd) throws Exception {
                    Vector<String> tokens = parse(cmd, " ");
                    if (tokens.size() != 0) {
                        switch (tokens.get(0)) {
                            case "create":
                                if (tokens.size() != 2) {
                                    throw new Exception("create: wrong arguments number");
                                }
                                create(tokens.get(1));
                                break;
                            case "drop":
                                if (tokens.size() != 2) {
                                    throw new Exception("drop: wrong arguments number");
                                }
                                drop(tokens.get(1));
                                break;
                            case "use":
                                if (tokens.size() != 2) {
                                    throw new Exception("use: wrong arguments number");
                                }
                                use(tokens.get(1));
                                break;
                            case "put":
                                if (currentTable == null) {
                                    throw new Exception("no table");
                                }
                                if (tokens.size() != 3) {
                                    throw new Exception("put: wrong arguments number");
                                }
                                put(tokens.get(1), tokens.get(2));
                                break;
                            case "get":
                                if (currentTable == null) {
                                    throw new Exception("no table");
                                }
                                if (tokens.size() != 2) {
                                    throw new Exception("get: wrong arguments number");
                                }
                                get(tokens.get(1));
                                break;
                            case "remove":
                                if (currentTable == null) {
                                    throw new Exception("no table");
                                }
                                if (tokens.size() != 2) {
                                    throw new Exception("remove: wrong arguments number");
                                }
                                remove(tokens.get(1));
                                break;
                            case "exit": {
                                //if (currentTable == null) {
                                //    return false;
                                //}
                                //closeCurrentTable();
                                //break;
                                closeCurrentTable();
                                return false;
                            }
                            default:
                                throw new Exception("Wrong command: " + cmd);
                        }
                    }
                    return true;
                }
            });
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
