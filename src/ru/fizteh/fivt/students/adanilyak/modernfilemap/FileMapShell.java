package ru.fizteh.fivt.students.adanilyak.modernfilemap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.adanilyak.commands.*;
import ru.fizteh.fivt.students.adanilyak.tools.tlShellLogic;
import ru.fizteh.fivt.students.adanilyak.userinterface.uiCmdList;
import ru.fizteh.fivt.students.adanilyak.userinterface.uiShell;
import java.io.File;

/**
 * User: Alexander
 * Date: 21.10.13
 * Time: 11:21
 */
public class FileMapShell extends uiShell {
    public FileMapShell(String[] args, String datFileName) {
        File datFile = new File(System.getProperty("fizteh.db.dir"), datFileName);
        try {
            Table fmState = new FmTableSingleFileStorage(datFile);
            runFmShell(args, makeUpCmdList(fmState));
        } catch (Exception exc) {
            System.err.println(exc.getMessage());
            System.exit(1);
        }
    }

    private void runFmShell (String[] args, uiCmdList cmdList) {
        if (args.length == 0) {
            tlShellLogic.interactiveMode(System.in, cmdList.getCmdList(), System.out, System.err);
        } else {
            tlShellLogic.packageMode(args, cmdList.getCmdList(), System.out, System.err);
        }
    }

    private uiCmdList makeUpCmdList(Table fmState) {
        uiCmdList stockFmShellCmdList = new uiCmdList();
        stockFmShellCmdList.addCommand(new CmdFmPut(fmState));
        stockFmShellCmdList.addCommand(new CmdFmGet(fmState));
        stockFmShellCmdList.addCommand(new CmdFmRemove(fmState));
        stockFmShellCmdList.addCommand(new CmdFmExit(fmState));
        return stockFmShellCmdList;
    }
}
