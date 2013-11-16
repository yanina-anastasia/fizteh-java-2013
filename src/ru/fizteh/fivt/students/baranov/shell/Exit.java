package ru.fizteh.fivt.students.baranov.shell;

public class Exit implements BasicCommand {
        public void executeCommand(String[] arguments, Shell usedShell) throws ShellInterruptionException {    
                throw new ShellInterruptionException();
        }        
        public int getNumberOfArguments() {
                return 0;
        }        
        public String getCommandName() {
                return "exit";
        }
}