package ru.fizteh.fivt.students.dsalnikov.multifilemap;

import ru.fizteh.fivt.students.dsalnikov.shell.Command;

import java.io.File;
import java.io.IOException;

public class UseCommand implements Command {


    @Override
    public void execute(Object state, String[] args) throws IOException {
        if (args.length != 2) {
            throw new IllegalArgumentException("wrong usage of command use: 1 argument expected");
        }
        MultiFileMapState mfms = (MultiFileMapState) state;
        File f = new File(mfms.workingdirectory, args[1]);
        if (!f.exists()) {
            System.out.println("table " + args[1] + " not exists");
        } else {
            if (!mfms.getFlag()) {
                Table temp = new Table(f.getAbsolutePath());
                mfms.usingTable(temp);
            } else {
                if (f.getCanonicalPath() == args[1]) {
                    System.out.println("This table is already loaded");
                    return;
                } else {
                    Table ttable = mfms.getTable();
                    ttable.flush();
                    Table newtable = new Table(f.getAbsolutePath());
                    mfms.usingTable(newtable);
                }
            }
        }
    }

    @Override
    public String getName() {
        return "use";
    }

    @Override
    public int getArgsCount() {
        return 1;
    }
}
