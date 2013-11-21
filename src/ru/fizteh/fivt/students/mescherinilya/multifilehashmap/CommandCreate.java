package ru.fizteh.fivt.students.mescherinilya.multifilehashmap;

import java.io.File;

public class CommandCreate implements Command {

    public CommandCreate() {
        super();
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public int getArgsCount() {
        return 1;
    }

    @Override
    public void execute(String[] args) throws Exception {

        File newTable = new File(MultiFileHashMap.rootDir + File.separator + args[0]);

        if (!newTable.exists()) {
            if (!newTable.mkdir()) {
                throw new Exception("Couldn't create a new directory.");
            }
            System.out.println("created");
        } else {
            System.out.println(args[0] + " exists");
        }

    }
}
