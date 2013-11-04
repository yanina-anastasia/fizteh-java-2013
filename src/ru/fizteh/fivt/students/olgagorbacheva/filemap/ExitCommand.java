package ru.fizteh.fivt.students.olgagorbacheva.filemap;
import ru.fizteh.fivt.students.olgagorbacheva.shell.Command;
import ru.fizteh.fivt.students.olgagorbacheva.shell.State;

public class ExitCommand implements Command  {
      private String name = "exit";
      private final int argNumber = 0;
      
      public ExitCommand() {
            
      }
      
      public void execute(String args[], State state) {
            System.out.println("exit");
      }
      
      public String getName() {
            return name;
      }
      
      public int getArgNumber() {
            return argNumber;
      }

}
