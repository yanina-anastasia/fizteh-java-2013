package ru.fizteh.fivt.students.inaumov.multifilemap;

import ru.fizteh.fivt.students.inaumov.common.CommonShell;
import ru.fizteh.fivt.students.inaumov.filemap.commands.*;

public class MultiFileMapMain {
    public static void main(String[] args) {
        CommonShell<MultiFileMapShellState> shell = new CommonShell<MultiFileMapShellState>();

        shell.addCommand(new PutCommand());
        shell.addCommand(new GetCommand());
        shell.addCommand(new RemoveCommand());
        shell.addCommand(new SizeCommand());
        shell.addCommand(new CommitCommand());
        shell.addCommand(new RollbackCommand());
        shell.addCommand(new CreateCommand());
        shell.addCommand(new DropCommand());
        shell.addCommand(new UseCommand());

        String dataBaseDir = System.getProperty("fizteh.db.dir");

        MultiFileMapShellState shellState = new MultiFileMapShellState();
        DatabaseFactory factory = new DatabaseFactory();
        shellState.tableProvider = factory.create(dataBaseDir);
        shell.setFileMapState(shellState);

        if (args.length == 0) {
            shell.interactiveMode();
        } else {
            shell.batchMode(args);
        }
    }
}
