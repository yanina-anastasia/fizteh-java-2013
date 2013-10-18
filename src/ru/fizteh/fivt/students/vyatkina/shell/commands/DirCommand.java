package ru.fizteh.fivt.students.vyatkina.shell.commands;

import ru.fizteh.fivt.students.vyatkina.shell.Command;
import ru.fizteh.fivt.students.vyatkina.shell.FileManager;

public class DirCommand implements Command {

    private final FileManager fileManager;

    public DirCommand (FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    public void execute (String[] args) {
      String [] files = fileManager.getSortedCurrentDirectoryFiles ();
      for (String file: files) {
          System.out.println (file);
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

