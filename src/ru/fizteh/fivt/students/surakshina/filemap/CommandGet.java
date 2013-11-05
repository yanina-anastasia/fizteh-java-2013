package ru.fizteh.fivt.students.surakshina.filemap;

public class CommandGet extends DataBaseCommand {

    public CommandGet(TableState stateNew) {
        super(stateNew);
        name = "get";
        numberOfArguments = 1;
    }

    @Override
    public void executeProcess(String[] input) {
        if (state.getTableProvider() != null) {
            String key = input[1];
            if (state.getTable().get(key) != null) {
                System.out.println("found\n" + state.getTable().get(key));
            } else {
                System.out.println("not found");
            }
        } else {
            System.out.println("no table");
        }
    }
}
