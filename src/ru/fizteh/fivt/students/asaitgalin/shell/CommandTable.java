package ru.fizteh.fivt.students.asaitgalin.shell;

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

public class CommandTable {
    private Map<String, Command> table = new HashMap<String, Command>();

    public void appendCommand(Command cmd) {
        table.put(cmd.getName(), cmd);
    }

    public void executeCommandLine(String commandsLine) throws IOException {
        String[] commands = commandsLine.trim().split("\\s*;\\s*");
        for (String s: commands) {
            String[] cmdArgs = s.split("\\s+");
            Command cmd = table.get(cmdArgs[0]);
            if (cmd != null) {
                if (cmdArgs.length - 1 != cmd.getArgsCount()) {
                    throw new IOException(cmd.getName() + ": wrong argument count");
                }
                cmd.execute(cmdArgs);
            } else {
                throw new IOException(cmdArgs[0] + ": unrecognized command");
            }
        }
    }

}
