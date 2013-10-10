package ru.fizteh.fivt.students.fedoseev.shell;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractShell implements Shell {
    private static final CdCommand CD = new CdCommand("cd", 1);
    private static final MkdirCommand MKDIR = new MkdirCommand("mkdir", 1);
    private static final PwdCommand PWD = new PwdCommand("pwd", 0);
    private static final RmCommand RM = new RmCommand("rm", 1);
    private static final CpCommand CP = new CpCommand("cp", 2);
    private static final MvCommand MV = new MvCommand("mv", 2);
    private static final DirCommand DIR = new DirCommand("dir", 0);
    private static final ExitCommand EXIT = new ExitCommand("exit", 0);
    protected static final Map<String, Command> COMMANDS = new HashMap<String, Command>() {{
        put(CD.getCmdName(), CD);
        put(MKDIR.getCmdName(), MKDIR);
        put(PWD.getCmdName(), PWD);
        put(RM.getCmdName(), RM);
        put(CP.getCmdName(), CP);
        put(MV.getCmdName(), MV);
        put(DIR.getCmdName(), DIR);
        put(EXIT.getCmdName(), EXIT);
    }};

    public class ShellState {
        private File curState;

        public void setCurState(File dir) {
            curState = dir;
        }

        public File getCurState() {
            return curState;
        }
    }

    public interface Command {
        public String getCmdName();

        public Integer getArgsCount();

        public abstract void execute(String[] input, ShellState state) throws IOException;
    }

    protected ShellState state = new ShellState();

    public AbstractShell(File dir) {
        state.setCurState(dir);
    }

    public String join(String[] items, String sep) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String item : items) {
            if (!first) {
                sb.append(sep);
            }
            first = false;
            sb.append(item);
        }
        return sb.toString();
    }

    public String[] getCommandArguments(String inputString) {
        int begin;

        if ((begin = inputString.indexOf(" ")) == -1) {
            return new String[0];
        }

        String[] args = inputString.substring(begin + 1, inputString.length()).trim().split("\\s+");
        for (String s : args) {
            s = s.trim();
        }

        return args;
    }

    public abstract void run() throws IOException, InterruptedException;
}
