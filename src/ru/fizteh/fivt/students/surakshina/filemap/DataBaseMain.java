package ru.fizteh.fivt.students.surakshina.filemap;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import ru.fizteh.fivt.students.surakshina.shell.Command;
import ru.fizteh.fivt.students.surakshina.shell.CommandExit;
import ru.fizteh.fivt.students.surakshina.shell.Shell;


public class DataBaseMain {
    public static void main(String[] args) {
        NewTableProviderFactory factory = new NewTableProviderFactory();
        String workingDirectory = System.getProperty("fizteh.db.dir");
        NewTableProvider provider = null;
        try {
           provider = (NewTableProvider) factory.create(workingDirectory);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
        TableState state = new TableState(new File(workingDirectory), provider);
        Set<Command> commands = tableCommands(state);
        Shell shell = new Shell(state, commands);
        shell.startWork(args);
 
    }
    
    public static Set<Command> tableCommands(TableState state) {
        Set<Command> tableCommands = new HashSet<Command>();
        tableCommands.add(new CommandCommit(state));
        tableCommands.add(new CommandRollback(state));
        tableCommands.add(new CommandGet(state));
        tableCommands.add(new CommandPut(state));
        tableCommands.add(new CommandRemove(state));
        tableCommands.add(new CommandExit(state));
        tableCommands.add(new CommandUse(state));
        tableCommands.add(new CommandCreate(state));
        tableCommands.add(new CommandDrop(state));
        return tableCommands;

    }
}
