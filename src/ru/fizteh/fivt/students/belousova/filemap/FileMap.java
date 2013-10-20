package ru.fizteh.fivt.students.belousova.filemap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.belousova.shell.Command;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileMap {

    public static Map<String, Command> commandList = new HashMap<String, Command>();

    public static void main(String[] args) {
        File data = new File(System.getProperty("fizteh.db.dir"), "db.dat");
        try {
            Table state = new SingleFileTable(data);
            FileMapShell shell = new FileMapShell();
            shell.run(args, state);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(2);
        }
    }

    private static void makeCommandList(Table state) {
        addCommand(new CommandGet(state));
        addCommand(new CommandPut(state));
        addCommand(new CommandRemove(state));
        addCommand(new CommandExit(state));
    }

    private static void addCommand(Command command) {
        commandList.put(command.getName(), command);
    }
}
