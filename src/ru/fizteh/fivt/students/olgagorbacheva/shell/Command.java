package ru.fizteh.fivt.students.olgagorbacheva.shell;

public interface Command {
      
      public void execute(String args[], State state) throws ShellException;            
      public String getName();           
      public int getArgNumber();                   

}