package ru.fizteh.fivt.students.fedoseev.shell;

import ru.fizteh.fivt.students.fedoseev.common.Abstract;
import ru.fizteh.fivt.students.fedoseev.common.AbstractCommand;
import ru.fizteh.fivt.students.fedoseev.common.Utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractShell extends Abstract {
    public AbstractShell(File dir) {
        super(dir);
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

    @Override
    public void runCommands(String cmd, int end) throws IOException {
        Map<String, AbstractCommand> commands = getCommands();

        if (!commands.containsKey(cmd.substring(0, end))) {
            throw new IOException("\"ERROR: not existing command \"" + cmd.substring(0, end) + "\"");
        }

        AbstractCommand command = commands.get(cmd.substring(0, end));

        if (Utils.getCommandArguments(cmd).length != command.getArgsCount()) {
            throw new IOException(command.getCmdName() + " ERROR: \"" + command.getCmdName() +
                    "\" command receives " + command.getArgsCount() + " arguments");
        }

        command.execute(Utils.getCommandArguments(cmd), state);
    }
}
