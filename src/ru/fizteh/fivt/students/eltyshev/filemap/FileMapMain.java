package ru.fizteh.fivt.students.eltyshev.filemap;

import ru.fizteh.fivt.students.eltyshev.shell.Shell;
import ru.fizteh.fivt.students.eltyshev.shell.commands.Command;
import ru.fizteh.fivt.students.eltyshev.shell.commands.HelpCommand;

import ru.fizteh.fivt.students.eltyshev.filemap.base.commands.*;

import java.util.ArrayList;

public class FileMapMain {
    public static void main(String[] args)
    {
        Shell<FileMapShellState> shell = new Shell<FileMapShellState>();

        ArrayList<Command> commands = new ArrayList<Command>();

        Command<FileMapShellState> command = new PutCommand();
        commands.add(command);

        command = new GetCommand();
        commands.add(command);

        command = new RemoveCommand();
        commands.add(command);

        command = new CommitCommand();
        commands.add(command);

        command = new RollbackCommand();
        commands.add(command);

        command = new ExitCommand();
        commands.add(command);

        command = new HelpCommand<FileMapShellState>(commands);
        commands.add(command);

        shell.setCommands(commands);

        FileMapShellState shellState = new FileMapShellState();
        String databaseDirectory = System.getProperty("fizteh.db.dir");
        shellState.table = new SingleFileTable(databaseDirectory, "master");
        shell.setShellState(shellState);
        shell.start();
    }
}
