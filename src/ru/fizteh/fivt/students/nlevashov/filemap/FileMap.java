package ru.fizteh.fivt.students.nlevashov.filemap;

import ru.fizteh.fivt.students.nlevashov.mode.Mode;
import ru.fizteh.fivt.students.nlevashov.shell.Shell;
import ru.fizteh.fivt.storage.strings.*;
import ru.fizteh.fivt.students.nlevashov.factory.*;

import java.util.Vector;
import java.nio.file.Path;
import java.io.IOException;

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

    static Table currentTable = null;
    static TableProvider provider;

    public static void create(String tableName) throws IOException {
        if (provider.createTable(tableName) == null) {
            throw new IOException(tableName + " exists");
        } else {
            System.out.println("created");
        }
    }

    public static void drop(String tableName) throws IOException {
        try {
            provider.removeTable(tableName);
            if (tableName.equals(currentTable.getName())) {
                currentTable = null;
            }
            System.out.println("dropped");
        } catch (IllegalStateException e) {
            throw new IOException(tableName + " not exists");
        }
    }

    public static void use(String tableName) throws IOException {
        Table newCurrentTable = provider.getTable(tableName);
        if (newCurrentTable == null) {
            throw new IOException(tableName + " not exists");
        } else {
            currentTable = newCurrentTable;
            System.out.println("using " + tableName);
        }
    }

    public static void put(String key, String value) throws IOException {
        if (currentTable == null) {
            throw new IOException("no table");
        }
        String putString = currentTable.put(key, value);
        if (putString == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(putString);
        }
    }

    public static void get(String key) throws IOException {
        if (currentTable == null) {
            throw new IOException("no table");
        }
        String getString = currentTable.get(key);
        if (getString == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(getString);
        }
    }

    public static void remove(String key) throws IOException {
        if (currentTable == null) {
            throw new IOException("no table");
        }
        if (currentTable.remove(key) == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }

    public static void size() throws IOException {
        if (currentTable == null) {
            throw new IOException("no table");
        }
        System.out.println(currentTable.size());
    }

    public static void commit() throws IOException {
        if (currentTable == null) {
            throw new IOException("no table");
        }
        System.out.println(currentTable.commit());
    }

    public static void rollback() throws IOException {
        if (currentTable == null) {
            throw new IOException("no table");
        }
        System.out.println(currentTable.rollback());
    }

    public static void main(String[] args) {
        try {
            String addr = System.getProperty("fizteh.db.dir");
            if (addr == null) {
                System.err.println("Property \"fizteh.db.dir\" wasn't set");
                System.exit(1);
            }
            Path addrPath = Shell.makePath(addr).toPath();
            TableProviderFactory factory = new MyTableProviderFactory();
            provider = factory.create(addrPath.toString());
            Mode.start(args, new Mode.Executor() {
                public boolean execute(String cmd) throws IOException {
                    Vector<String> tokens = parse(cmd, " ");
                    if (tokens.size() != 0) {
                        switch (tokens.get(0)) {
                            case "create":
                                if (tokens.size() != 2) {
                                    throw new IOException("create: wrong arguments number");
                                }
                                create(tokens.get(1));
                                break;
                            case "drop":
                                if (tokens.size() != 2) {
                                    throw new IOException("drop: wrong arguments number");
                                }
                                drop(tokens.get(1));
                                break;
                            case "use":
                                if (tokens.size() != 2) {
                                    throw new IOException("use: wrong arguments number");
                                }
                                use(tokens.get(1));
                                break;
                            case "put":
                                if (tokens.size() != 3) {
                                    throw new IOException("put: wrong arguments number");
                                }
                                put(tokens.get(1), tokens.get(2));
                                break;
                            case "get":
                                if (tokens.size() != 2) {
                                    throw new IOException("get: wrong arguments number");
                                }
                                get(tokens.get(1));
                                break;
                            case "remove":
                                if (tokens.size() != 2) {
                                    throw new IOException("remove: wrong arguments number");
                                }
                                remove(tokens.get(1));
                                break;
                            case "size":
                                if (tokens.size() != 1) {
                                    throw new IOException("get: wrong arguments number");
                                }
                                size();
                                break;
                            case "commit":
                                if (tokens.size() != 1) {
                                    throw new IOException("get: wrong arguments number");
                                }
                                commit();
                                break;
                            case "rollback":
                                if (tokens.size() != 1) {
                                    throw new IOException("get: wrong arguments number");
                                }
                                rollback();
                                break;
                            case "exit":
                                return false;
                            default:
                                throw new IOException("Wrong command: " + cmd);
                        }
                    }
                    return true;
                }
            });
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
