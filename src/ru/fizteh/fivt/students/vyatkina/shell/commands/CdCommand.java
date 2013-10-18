package ru.fizteh.fivt.students.vyatkina.shell.commands;

import ru.fizteh.fivt.students.vyatkina.shell.Command;
import ru.fizteh.fivt.students.vyatkina.shell.FileManager;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CdCommand implements Command {

    private final FileManager fileManager;

    public CdCommand (FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    public void execute (String[] args) throws IllegalArgumentException {
        Path newDirectory = Paths.get (args [0]);
        fileManager.changeCurrentDirectory (newDirectory);
    }

    @Override
    public String getName () {
        return "cd";
    }

    @Override
    public int getArgumentCount () {
        return 1;
    }
}
