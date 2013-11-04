package ru.fizteh.fivt.students.olgagorbacheva.filemap;
import ru.fizteh.fivt.students.olgagorbacheva.shell.State;

import ru.fizteh.fivt.students.olgagorbacheva.shell.Command;

public class GetCommand implements Command {

      private String name = "get";
      private final int argNumber = 1;
      private Storage storage;

      public GetCommand(Storage storage) {
            this.storage = storage;
      }

      public void execute(String[] args, State state) {
            String value = storage.get(args[1]);
            if (value != null){
                  System.out.println(value);
                  System.out.println("found");
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