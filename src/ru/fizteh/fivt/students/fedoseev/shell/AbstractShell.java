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
        final CdCommand CD = new CdCommand();
        final MkdirCommand MKDIR = new MkdirCommand();
        final PwdCommand PWD = new PwdCommand();
        final RmCommand RM = new RmCommand();
        final CpCommand CP = new CpCommand();
        final MvCommand MV = new MvCommand();
        final DirCommand DIR = new DirCommand();
        final ExitCommand EXIT = new ExitCommand();

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
