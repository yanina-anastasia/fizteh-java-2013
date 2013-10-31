package ru.fizteh.fivt.students.eltyshev.filemap;

import ru.fizteh.fivt.students.eltyshev.shell.Shell;
import ru.fizteh.fivt.students.eltyshev.shell.commands.Command;
import ru.fizteh.fivt.students.eltyshev.shell.commands.HelpCommand;
import ru.fizteh.fivt.students.eltyshev.filemap.base.commands.*;

import java.util.ArrayList;
import java.util.List;

public class FileMapMain {
    public static void main(String[] args) {
        Shell<FileMapShellState> shell = new Shell<FileMapShellState>();

        List<Command<?>> commands = new ArrayList<Command<?>>();

        commands.add(new PutCommand());
        commands.add(new GetCommand());
        commands.add(new RemoveCommand());
        commands.add(new CommitCommand());
        commands.add(new RollbackCommand());
        commands.add(new ExitCommand());
        commands.add(new HelpCommand<FileMapShellState>(commands));

        shell.setCommands(commands);

        FileMapShellState shellState = new FileMapShellState();
        String databaseDirectory = System.getProperty("fizteh.db.dir");
        shellState.table = new SingleFileTable(databaseDirectory, "master");
        shell.setShellState(shellState);
        shell.start();
    }
}
