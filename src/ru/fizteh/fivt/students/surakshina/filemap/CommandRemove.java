package ru.fizteh.fivt.students.surakshina.filemap;

public class CommandRemove extends DataBaseCommand {
    public CommandRemove(TableState state) {
        super(state);
        name = "remove";
        numberOfArguments = 1;
    }

    @Override
    public void executeProcess(String[] input) {
        if (state.getTableProvider() != null) {
            String key = input[1];
            if (state.getTable().remove(key) != null) {
                System.out.println("removed");
            } else {
                System.out.println("not found");
            }
        }
        
    }
}
