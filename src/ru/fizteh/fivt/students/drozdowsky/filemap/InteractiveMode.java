package ru.fizteh.fivt.students.drozdowsky.filemap;

import ru.fizteh.fivt.students.drozdowsky.Database.FileMap;
import ru.fizteh.fivt.students.drozdowsky.shell.ShellUtils;
import java.util.Scanner;

public class InteractiveMode {
    private FileMap db;

    public InteractiveMode(FileMap db) {
        this.db = db;
    }

    public void start() {
        Scanner in = new Scanner(System.in);
        while (true) {
            String[] args = ShellUtils.scanArgs(in);
            if (args.length != 0) {
                PacketMode pm = new PacketMode(db, args, false);
                pm.start();
            }
        }
    }
}
