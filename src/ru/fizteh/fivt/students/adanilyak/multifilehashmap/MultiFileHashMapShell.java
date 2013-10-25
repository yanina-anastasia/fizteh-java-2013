package ru.fizteh.fivt.students.adanilyak.multifilehashmap;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.adanilyak.commands.*;
import ru.fizteh.fivt.students.adanilyak.tools.ShellLogic;
import ru.fizteh.fivt.students.adanilyak.userinterface.uiCmdList;
import ru.fizteh.fivt.students.adanilyak.userinterface.uiShell;

import java.io.File;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 16:40
 */
public class MultiFileHashMapShell extends uiShell {
    public MultiFileHashMapShell(String[] args) {
        try {
            TableProvider tableManager = new TableManager(new File(System.getProperty("fizteh.db.dir")));
            DataBaseGlobalState state = new DataBaseGlobalState(tableManager);
            runMFHMShell(args, makeUpCmdList(state));
        } catch (Exception exc) {
            System.err.println(exc.getMessage());
            System.exit(1);
        }
    }

    private void runMFHMShell(String[] args, uiCmdList cmdList) {
        if (args.length == 0) {
            ShellLogic.interactiveMode(System.in, cmdList.getCmdList(), System.out, System.err);
        } else {
            ShellLogic.packageMode(args, cmdList.getCmdList(), System.out, System.err);
        }
    }

    private uiCmdList makeUpCmdList(DataBaseGlobalState fmState) {
        uiCmdList stockShellCmdList = new uiCmdList();
        stockShellCmdList.addCommand(new CmdPut(fmState));
        stockShellCmdList.addCommand(new CmdGet(fmState));
        stockShellCmdList.addCommand(new CmdRemove(fmState));
        stockShellCmdList.addCommand(new CmdUse(fmState));
        stockShellCmdList.addCommand(new CmdCreate(fmState));
        stockShellCmdList.addCommand(new CmdDrop(fmState));
        stockShellCmdList.addCommand(new CmdMFHMExit(fmState));
        return stockShellCmdList;
    }
}
