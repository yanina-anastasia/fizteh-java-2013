package ru.fizteh.fivt.students.vishnevskiy.filemap;

import ru.fizteh.fivt.students.vishnevskiy.filemap.commands.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.io.File;

public class CommandsOperator {
    private Map<String, Command> commandsTable = new HashMap<String, Command>();
    private SingleFileMap singleFileMap;

    private void loadClasses() {
        Exit exit = new Exit();
        commandsTable.put(exit.getName(), exit);
        Put put = new Put();
        commandsTable.put(put.getName(), put);
        Get get = new Get();
        commandsTable.put(get.getName(), get);
        Remove remove = new Remove();
        commandsTable.put(remove.getName(), remove);
    }

    public CommandsOperator() {
        loadClasses();
        singleFileMap = new SingleFileMap(new File(System.getProperty("fizteh.db.dir") + "/db.dat"));
    }

    public int runCommand(String line) {
        try {
            line = line.replaceAll("\\s+", " ").trim();
            String[] commandAndArgs = line.split(" ");
            String commandName = commandAndArgs[0];
            String[] args = Arrays.copyOfRange(commandAndArgs, 1, commandAndArgs.length);
            Command command = commandsTable.get(commandName);
            if (command == null) {
                if (commandName.equals("")) {
                    return 0;
                } else {
                    throw new FileMapException(commandName + ": command not found");
                }

            }
            command.execute(singleFileMap, args);
        } catch (FileMapException e) {
            System.err.println(e.getMessage());
            System.err.flush();
            return 1;
        }
        return 0;
    }
}
