package ru.fizteh.fivt.students.surakshina.filemap;

public class CommandRemove extends DataBaseCommand {
    public CommandRemove(TableState state) {
        super(state);
        name = "remove";
        numberOfArguments = 1;
    }

    @Override
    public void executeProcess(String[] input) {
        if (state.getTable() != null) {
            String key = input[1];
            String result = state.getTable().remove(key);
            if (result != null) {
                System.out.println("removed");
            } else {
                System.out.println("not found");
            }
        } else {
            System.out.println("no table");
        }

    }
}
