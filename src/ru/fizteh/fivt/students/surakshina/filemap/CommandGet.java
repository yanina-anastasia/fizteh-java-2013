package ru.fizteh.fivt.students.surakshina.filemap;

public class CommandGet extends DataBaseCommand {

    public CommandGet(TableState stateNew) {
        super(stateNew);
        name = "get";
        numberOfArguments = 1;
    }

    @Override
    public void executeProcess(String[] input) {
        if (state.getTable() != null) {
            String key = input[1];
            String value = state.getTable().get(key);
            if (value != null) {
                System.out.println("found\n" + value);
            } else {
                System.out.println("not found");
            }
        } else {
            System.out.println("no table");
        }
    }
}
