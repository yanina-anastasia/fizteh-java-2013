package ru.fizteh.fivt.students.dsalnikov.multifilemap;


import ru.fizteh.fivt.students.dsalnikov.shell.Command;
import ru.fizteh.fivt.students.dsalnikov.shell.Shell;

import java.io.IOException;
import java.util.ArrayList;

public class main {
    public static void main(String[] args) throws IOException {

        MultiFileMapState mfhm = new MultiFileMapState(System.getProperty("fizteh.db.dir"));
        Shell sh = new Shell(mfhm);

        ArrayList<Command> Commands = new ArrayList<Command>();

        MultiGet mg = new MultiGet();
        Commands.add(mg);

        MultiPut mp = new MultiPut();
        Commands.add(mp);

        MultiRemove mr = new MultiRemove();
        Commands.add(mr);

        DropCommand dc = new DropCommand();
        Commands.add(dc);

        CreateNewTableCommand cntc = new CreateNewTableCommand();
        Commands.add(cntc);

        UseCommand uc = new UseCommand();
        Commands.add(uc);

        MultiExitCommand mec = new MultiExitCommand();
        Commands.add(mec);

        sh.setCommands(Commands);

        if (args.length == 0) {
            sh.batchMode();
        } else {
            sh.commandMode(args);
        }


    }
}
