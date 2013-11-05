package ru.fizteh.fivt.students.surakshina.filemap;

public class CommandPut extends DataBaseCommand {
    public CommandPut(TableState state) {
        super(state);
        name = "put";
        numberOfArguments = 2;
    }

    @Override
    public void executeProcess(String[] input) {
        if (state.getTableProvider() != null) {
            String key = input[1];
            String value = input[2];
            if (state.getTable().put(key, value) != null) {
                System.out.println("overwrite\n" + state.getTable().get(key));
            } else {
                System.out.println("new");
            }
        }
    }

}
