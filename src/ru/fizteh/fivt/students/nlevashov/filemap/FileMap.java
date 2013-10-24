package ru.fizteh.fivt.students.nlevashov.filemap;

import ru.fizteh.fivt.students.nlevashov.mode.Mode;

import java.util.Vector;
import java.io.File;
import java.nio.file.Path;

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

    static Table t;

    public static void put(String key, String value) {
        String putString = t.put(key, value);
        if (putString == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(putString);
        }
    }

    public static void get(String key) {
        String getString = t.get(key);
        if (getString == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(getString);
        }
    }

    public static void remove(String key) {
        if (t.remove(key) == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }

    public static void main(String[] args) {
        String path = System.getProperty("fizteh.db.dir");
        if (path == null) {
            System.err.println("Property \"fizteh.db.dir\" wasn't set");
            System.exit(1);
        }
        File f = new File(path);
        Path addr = f.toPath().resolve("db.dat");
        try {
            t = new Table(addr);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        try {
            Mode.start(args, new Mode.Executor() {
                public boolean execute(String cmd) throws Exception {
                    Vector<String> tokens = parse(cmd, " ");
                    if (tokens.size() != 0) {
                        switch (tokens.get(0)) {
                            case "put":
                                if (tokens.size() != 3) {
                                    throw new Exception("put: wrong arguments number");
                                }
                                put(tokens.get(1), tokens.get(2));
                                break;
                            case "get":
                                if (tokens.size() != 2) {
                                    throw new Exception("get: wrong arguments number");
                                }
                                get(tokens.get(1));
                                break;
                            case "remove":
                                if (tokens.size() != 2) {
                                    throw new Exception("remove: wrong arguments number");
                                }
                                remove(tokens.get(1));
                                break;
                            case "exit": {
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

        try {
            t.refresh();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
