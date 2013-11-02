package ru.fizteh.fivt.students.fedoseev.shell;

import ru.fizteh.fivt.students.fedoseev.common.AbstractFrame;
import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AbstractShell extends AbstractFrame<ShellState> {
    public AbstractShell(File dir) {
        state = new ShellState();
        state.setCurState(dir);
    }

    @Override
    public Map<String, AbstractCommand> getCommands() {
        final ShellCdCommand CD = new ShellCdCommand();
        final ShellMkdirCommand MKDIR = new ShellMkdirCommand();
        final ShellPwdCommand PWD = new ShellPwdCommand();
        final ShellRmCommand RM = new ShellRmCommand();
        final ShellCpCommand CP = new ShellCpCommand();
        final ShellMvCommand MV = new ShellMvCommand();
        final ShellDirCommand DIR = new ShellDirCommand();
        final ShellExitCommand EXIT = new ShellExitCommand();

        return new HashMap<String, AbstractCommand>() {{
            put(CD.getCmdName(), CD);
            put(MKDIR.getCmdName(), MKDIR);
            put(PWD.getCmdName(), PWD);
            put(RM.getCmdName(), RM);
            put(CP.getCmdName(), CP);
            put(MV.getCmdName(), MV);
            put(DIR.getCmdName(), DIR);
            put(EXIT.getCmdName(), EXIT);
        }};
    }
}
