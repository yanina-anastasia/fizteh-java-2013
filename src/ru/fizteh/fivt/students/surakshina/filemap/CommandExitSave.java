package ru.fizteh.fivt.students.surakshina.filemap;

public class CommandExitSave extends DataBaseCommand {
        public CommandExitSave(TableState state) {
            super(state);
            name = "exit";
            numberOfArguments = 0;
        }

        @Override
        public void executeProcess(String[] input) {
            if (state.getTable() != null) {
                int count = state.getTable().unsavedChanges();
                if (count != 0) {
                    return;
                } else {
                    System.exit(0);
                }
            } else {
                System.exit(0);
            }
        }

}
