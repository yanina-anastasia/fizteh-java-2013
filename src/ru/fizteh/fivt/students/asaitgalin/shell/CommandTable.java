package ru.fizteh.fivt.students.asaitgalin.shell;

import ru.fizteh.fivt.students.asaitgalin.shell.commands.Command;

import java.util.HashMap;
import java.util.Map;

public class CommandTable {
    private Map<String, Command> table = new HashMap<String, Command>();

    public void appendCommand(Command cmd) {
        table.put(cmd.getName(), cmd);
    }

    public void executeCommands(String commandsLine) throws UnknownCommandException {
        String[] commands = commandsLine.split(";");
        for (String s: commands) {
            s = s.trim();
            String[] cmdArgs = s.split("\\s+", 2);
            Command cmd = table.get(cmdArgs[0]);
            if (cmd != null) {
                if (cmdArgs.length == 2) {
                    cmd.execute(cmdArgs[1]);
                } else {
                    cmd.execute(null);
                }
            } else {
                throw new UnknownCommandException(cmdArgs[0]);
            }
        }
    }

}
