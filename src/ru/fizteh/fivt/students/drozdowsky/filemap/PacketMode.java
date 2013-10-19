package ru.fizteh.fivt.students.drozdowsky.filemap;

import java.util.ArrayList;
import java.io.File;

public class PacketMode {
    private File db;
    String[] args;
    boolean exitOnFailure;

    public PacketMode(String[] args, File db, boolean exitOnFailure) {
        this.db = db;
        this.args = args;
        this.exitOnFailure = exitOnFailure;
    }

    public void start() {
        args[args.length - 1] = args[args.length - 1] + ";";

        ArrayList<String> tempArgs = new ArrayList<String>();
        for (String arg : args) {
            int last = -1;
            for (int i = 0; i < arg.length(); i++) {
                if (arg.charAt(i) == ';') {
                    tempArgs.add(arg.substring(last + 1, i));
                    last = i;
                    if (!Utils.executeCommand(tempArgs.toArray(new String[tempArgs.size()]), db)) {
                        if (exitOnFailure) {
                            System.exit(1);
                        }
                    }
                    tempArgs.clear();
                }
            }
            if (last + 1 != arg.length()) {
                tempArgs.add(arg.substring(last + 1, arg.length()));
            }
        }
    }
}
