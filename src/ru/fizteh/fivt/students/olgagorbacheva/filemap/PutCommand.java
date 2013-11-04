package ru.fizteh.fivt.students.olgagorbacheva.filemap;

import ru.fizteh.fivt.students.olgagorbacheva.shell.Command;
import ru.fizteh.fivt.students.olgagorbacheva.shell.State;

public class PutCommand implements Command {

      private String name = "put";
      private final int argNumber = 2;

      private Storage storage;

      public PutCommand(Storage storage) {
            this.storage = storage;
      }

      public void execute(String[] args, State state) {
            if (storage.put(args[1], args[2])) {
                  System.out.println("new");
            } else {
                  System.out.println("overwrite");
                  System.out.println(storage.get(args[1]));
                  storage.set(args[1], args[2]);
            }

      }

      public String getName() {
            return name;
      }

      public int getArgNumber() {
            return argNumber;
      }

}