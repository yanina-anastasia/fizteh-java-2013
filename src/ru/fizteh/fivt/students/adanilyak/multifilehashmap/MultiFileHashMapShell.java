package ru.fizteh.fivt.students.adanilyak.multifilehashmap;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.adanilyak.commands.*;
import ru.fizteh.fivt.students.adanilyak.tools.ShellLogic;
import ru.fizteh.fivt.students.adanilyak.userinterface.GenericCmdList;
import ru.fizteh.fivt.students.adanilyak.userinterface.GenericShell;

import java.io.File;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 16:40
 */
public class MultiFileHashMapShell extends GenericShell {
    public MultiFileHashMapShell(String[] args) {
        if (System.getProperty("fizteh.db.dir") == null) {
            System.err.println("Data Base directory is not set");
            System.exit(3);
        }
        try {
            TableManagerCreator tableManagerCreator = new TableManagerCreator();
            TableProvider tableManager = tableManagerCreator.create(System.getProperty("fizteh.db.dir"));
            DataBaseGlobalState state = new DataBaseGlobalState(tableManager);
            runMFHMShell(args, makeUpCmdList(state));
        } catch (Exception exc) {
            System.err.println(exc.getMessage());
            System.exit(1);
        }
    }

    private void runMFHMShell(String[] args, GenericCmdList cmdList) {
        if (args.length == 0) {
            ShellLogic.interactiveMode(System.in, cmdList.getCmdList(), System.out, System.err);
        } else {
            ShellLogic.packageMode(args, cmdList.getCmdList(), System.out, System.err);
        }
    }

    private GenericCmdList makeUpCmdList(DataBaseGlobalState state) {
        GenericCmdList stockShellCmdList = new GenericCmdList();
        stockShellCmdList.addCommand(new CmdPut(state));
        stockShellCmdList.addCommand(new CmdGet(state));
        stockShellCmdList.addCommand(new CmdRemove(state));
        stockShellCmdList.addCommand(new CmdUse(state));
        stockShellCmdList.addCommand(new CmdCreate(state));
        stockShellCmdList.addCommand(new CmdDrop(state));
        stockShellCmdList.addCommand(new CmdExit(state));
        stockShellCmdList.addCommand(new CmdCommit(state));
        stockShellCmdList.addCommand(new CmdRollback(state));
        stockShellCmdList.addCommand(new CmdSize(state));
        return stockShellCmdList;
    }
}
