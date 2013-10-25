package ru.fizteh.fivt.students.drozdowsky.filemap;

import ru.fizteh.fivt.students.drozdowsky.Database.FileMap;
import ru.fizteh.fivt.students.drozdowsky.shell.Parser;
import java.util.ArrayList;

public class PacketMode {
    private FileMap db;
    private String[] args;
    private boolean exitOnFailure;
    private static final String[] NOWSCOMMANDS = {"put"};

    public PacketMode(FileMap db, String[] args, boolean exitOnFailure) {
        this.db = db;
        this.args = args;
        this.exitOnFailure = exitOnFailure;
    }

    public void start() {
        ArrayList<String[]> commands = Parser.parse(args, NOWSCOMMANDS);

        for (String[] command : commands) {
            if (!FilemapUtils.executeCommand(db, command)) {
                if (exitOnFailure) {
                    db.close();
                    System.exit(1);
                } else {
                    return;
                }
            }
        }
    }
}
