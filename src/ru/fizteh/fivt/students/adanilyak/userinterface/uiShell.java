package ru.fizteh.fivt.students.adanilyak.userinterface;

import ru.fizteh.fivt.students.adanilyak.tools.tlShellLogic;
/**
 * User: Alexander
 * Date: 20.10.13
 * Time: 22:26
 */
public class uiShell {
    public uiShell(){

    }

    public uiShell (String[] args, uiCmdList cmdList) {
        if (args.length == 0) {
            tlShellLogic.interactiveMode(System.in, cmdList.getCmdList(), System.out, System.err);
        } else {
            tlShellLogic.packageMode(args, cmdList.getCmdList(), System.out, System.err);
        }
    }
}
