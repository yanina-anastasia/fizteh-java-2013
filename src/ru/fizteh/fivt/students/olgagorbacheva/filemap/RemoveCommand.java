package ru.fizteh.fivt.students.olgagorbacheva.filemap;

import ru.fizteh.fivt.students.olgagorbacheva.shell.Command;
import ru.fizteh.fivt.students.olgagorbacheva.shell.State;
import ru.fizteh.fivt.students.olgagorbacheva.shell.ShellException;

public class RemoveCommand implements Command {

      private String name = "remove";
      private final int argNumber = 1;
      private Storage storage;

      public RemoveCommand(Storage storage) {
            this.storage = storage;
      }

      public void execute(String[] args, State state) throws ShellException {
            if (storage.remove(args[1])) {
                  System.out.println("removed");
            } else {
                  System.out.println("not found");
            }

      }

      public String getName() {
            return name;
      }

      public int getArgNumber() {
            return argNumber;
      }

}