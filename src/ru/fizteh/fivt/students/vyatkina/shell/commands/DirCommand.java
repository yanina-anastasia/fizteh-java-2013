package ru.fizteh.fivt.students.vyatkina.shell.commands;

import ru.fizteh.fivt.students.vyatkina.shell.Command;
import ru.fizteh.fivt.students.vyatkina.shell.FileManager;

import java.util.concurrent.ExecutionException;

public class DirCommand implements Command {

    private final FileManager fileManager;

    public DirCommand (FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    public void execute (String[] args) throws ExecutionException {
      try {
      String [] files = fileManager.getSortedCurrentDirectoryFiles ();
      for (String file: files) {
          System.out.println (file);
      }
      } catch (RuntimeException e) {
          throw new ExecutionException (e.fillInStackTrace ());
      }

    }

    @Override
    public String getName () {
        return "dir";
    }

    @Override
    public int getArgumentCount () {
        return 0;
    }
}

