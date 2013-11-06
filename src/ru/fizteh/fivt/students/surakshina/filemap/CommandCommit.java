package ru.fizteh.fivt.students.surakshina.filemap;

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
        } else {
            System.out.println("no table");
        }

    }
}
