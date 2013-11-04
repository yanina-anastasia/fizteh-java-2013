package ru.fizteh.fivt.students.olgagorbacheva.multyfilehashmap;

import java.io.File;

import ru.fizteh.fivt.students.olgagorbacheva.shell.ShellException;
import ru.fizteh.fivt.students.olgagorbacheva.shell.State;
import ru.fizteh.fivt.students.olgagorbacheva.shell.Command;
import ru.fizteh.fivt.students.olgagorbacheva.shell.RemoveCommand;

public class DropCommand implements Command {

      private String name = "drop";
      private final int argNumber = 1;

      public DropCommand( ) {
            
      }

      public void execute(String[] args, State state){
            File table = new File(new File(System.getProperty("fizteh.db.dir")), args[1]);
            if (!table.exists()) {
                  System.out.println("tablename not exists");
            } else {
                  RemoveCommand rm = new RemoveCommand();
                  try {
                        rm.execute(args, state);
                  } catch (ShellException exp) {
                        System.out.println("ошибка удаления таблицы");
                  } finally {
                        System.out.println("droped");
                  }
            }
      }

      public String getName() {
            return name;
      }

      public int getArgNumber() {
            return argNumber;
      }

}

