package ru.fizteh.fivt.students.belousova.filemap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.belousova.shell.Command;
import ru.fizteh.fivt.students.belousova.utils.ShellUtils;

import java.util.HashMap;
import java.util.Map;

public class FileMapShell {
    private Map<String, Command> commandList = new HashMap<String, Command>();

    public void run(String[] args, Table state) {
        makeCommandList(state);
        if (args.length == 0) {
            ShellUtils.interactiveMode(System.in, commandList);
        } else {
            ShellUtils.batchMode(args, commandList);
        }
    }

    private void makeCommandList(Table state) {
        addCommand(new CommandGet(state));
        addCommand(new CommandPut(state));
        addCommand(new CommandRemove(state));
        addCommand(new CommandExit(state));
    }

    private void addCommand(Command command) {
        commandList.put(command.getName(), command);
    }

}

