package ru.fizteh.fivt.students.fedoseev.shell;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractShell implements Shell {
    public Map<String, AbstractCommand> setCommands() {
        final CdCommand CD = new CdCommand("cd", 1);
        final MkdirCommand MKDIR = new MkdirCommand("mkdir", 1);
        final PwdCommand PWD = new PwdCommand("pwd", 0);
        final RmCommand RM = new RmCommand("rm", 1);
        final CpCommand CP = new CpCommand("cp", 2);
        final MvCommand MV = new MvCommand("mv", 2);
        final DirCommand DIR = new DirCommand("dir", 0);
        final ExitCommand EXIT = new ExitCommand("exit", 0);
        final Map<String, AbstractCommand> COMMANDS = new HashMap<String, AbstractCommand>() {{
            put(CD.getCmdName(), CD);
            put(MKDIR.getCmdName(), MKDIR);
            put(PWD.getCmdName(), PWD);
            put(RM.getCmdName(), RM);
            put(CP.getCmdName(), CP);
            put(MV.getCmdName(), MV);
            put(DIR.getCmdName(), DIR);
            put(EXIT.getCmdName(), EXIT);
        }};
        return COMMANDS;
    }

    public class ShellState {
        private File curState;

        public void setCurState(File dir) {
            curState = dir;
        }

        public File getCurState() {
            return curState;
        }
    }

    protected ShellState state = new ShellState();

    public AbstractShell(File dir) {
        state.setCurState(dir);
    }

    public abstract void run() throws IOException, InterruptedException;

    public void runCommands(String cmd, int end) throws IOException {
        Map<String, AbstractCommand> commands = setCommands();

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
