package ru.fizteh.fivt.students.surakshina.filemap;

import java.io.IOException;

public class CommandCommit extends DataBaseCommand {
    public CommandCommit(TableState state) {
        super(state);
        name = "commit";
        numberOfArguments = 0;
    }
    
    
    @Override
    public void executeProcess(String[] input) {
        if (state.getTable() != null) {
            int count = state.getTable().commit();
            System.out.println(count);
            if (count != 0) {
                try {
                    state.getTableProvider().saveChanges(state.getCurrentDirectory());
                } catch (IOException e) {
                    state.printError(e.getMessage());
                }
            } else {
               return;
            }
        } else {
            System.out.println("no table");
        }
        
    }
}
