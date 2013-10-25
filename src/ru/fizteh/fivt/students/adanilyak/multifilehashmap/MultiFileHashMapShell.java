package ru.fizteh.fivt.students.adanilyak.multifilehashmap;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.adanilyak.commands.*;
import ru.fizteh.fivt.students.adanilyak.tools.tlShellLogic;
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
            TableProvider tableManager = new mfhmTableManager(new File(System.getProperty("fizteh.db.dir")));
            DataBaseGlobalState mfhmState = new DataBaseGlobalState(tableManager);
            runMfhmShell(args, makeUpCmdList(mfhmState));
        } catch (Exception exc) {
            System.err.println(exc.getMessage());
            System.exit(1);
        }
    }

    private void runMfhmShell (String[] args, uiCmdList cmdList) {
        if (args.length == 0) {
            tlShellLogic.interactiveMode(System.in, cmdList.getCmdList(), System.out, System.err);
        } else {
            tlShellLogic.packageMode(args, cmdList.getCmdList(), System.out, System.err);
        }
    }

    private uiCmdList makeUpCmdList(DataBaseGlobalState fmState) {
        uiCmdList stockMfhmShellCmdList = new uiCmdList();
        stockMfhmShellCmdList.addCommand(new CmdPut(fmState));
        stockMfhmShellCmdList.addCommand(new CmdGet(fmState));
        stockMfhmShellCmdList.addCommand(new CmdRemove(fmState));
        stockMfhmShellCmdList.addCommand(new CmdUse(fmState));
        stockMfhmShellCmdList.addCommand(new CmdCreate(fmState));
        stockMfhmShellCmdList.addCommand(new CmdDrop(fmState));
        stockMfhmShellCmdList.addCommand(new CmdExit(fmState));
        return stockMfhmShellCmdList;
    }
}
