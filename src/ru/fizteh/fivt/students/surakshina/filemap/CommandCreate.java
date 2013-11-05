package ru.fizteh.fivt.students.surakshina.filemap;

public class CommandCreate extends DataBaseCommand {
    public CommandCreate(TableState state) {
        super(state);
        name = "create";
        numberOfArguments = 1;
    }

    @Override
    public void executeProcess(String[] input) {
        String name = input[1];
        try {
            if (state.getTableProvider().createTable(name) != null) {
                System.out.println(name + " exists");
            } else {
                System.out.println("created");
            }
        } catch (IllegalArgumentException e) {
            state.printError(e.getMessage());
        }

    }

}
