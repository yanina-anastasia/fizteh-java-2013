package ru.fizteh.fivt.students.olgagorbacheva.multyfilehashmap;

import java.io.File;

import ru.fizteh.fivt.students.olgagorbacheva.shell.Command;
import ru.fizteh.fivt.students.olgagorbacheva.shell.State;

public class CreateCommand implements Command {

      private String name = "create";
      private final int argNumber = 1;

      public CreateCommand( ) {
            
      }

      public void execute(String[] args, State state) {
            File table = new File(new File(System.getProperty("fizteh.db.dir")), args[1]);
            if (table.exists()) {
                  System.out.println("tablename exists");
            } else {
                  table.mkdir();
                  System.out.println("created");
            }
      }

      public String getName() {
            return name;
      }

      public int getArgNumber() {
            return argNumber;
      }

}
