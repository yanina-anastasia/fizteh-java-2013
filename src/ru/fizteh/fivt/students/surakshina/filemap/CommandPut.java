package ru.fizteh.fivt.students.surakshina.filemap;

public class CommandPut extends DataBaseCommand {
    public CommandPut(TableState state) {
        super(state);
        name = "put";
        numberOfArguments = 2;
    }

    @Override
    public void executeProcess(String[] input) {
        if (state.getTable() != null) {
            String key = input[1];
            String value = input[2];
            String result = state.getTable().put(key, value);
            if (result != null) {
                System.out.println("overwrite ");
                System.out.println(result);
            } else {
                System.out.println("new");
            }
        } else {
            System.out.println("no table");
        }
    }

}
