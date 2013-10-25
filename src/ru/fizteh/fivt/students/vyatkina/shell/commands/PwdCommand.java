package ru.fizteh.fivt.students.vyatkina.shell.commands;


import ru.fizteh.fivt.students.vyatkina.shell.Command;
import ru.fizteh.fivt.students.vyatkina.shell.FileManager;

import java.io.PrintStream;
import java.util.concurrent.ExecutionException;

public class PwdCommand implements Command {

    private final FileManager fileManager;

    public PwdCommand (FileManager fileManager) {
       this.fileManager = fileManager;
    }

    @Override
    public void execute (String[] args) throws ExecutionException {
       try {
       System.out.println (fileManager.getCurrentDirectoryString ());
       } catch (RuntimeException e) {
           throw new ExecutionException (e.fillInStackTrace ());
       }
    }

    @Override
    public String getName () {
        return "pwd";
    }

    @Override
    public int getArgumentCount () {
        return 0;
    }
}
