package ru.fizteh.fivt.students.mescherinilya.multifilehashmap;

public class CommandRemove implements Command {

    public CommandRemove() {
        super();
    }

    @Override
    public int getArgsCount() {
        return 1;
    }

    @Override
    public void execute(String[] args) {
        if (DatabaseWorker.currentTable == null) {
            System.out.println("no table");
            return;
        }

        if (DatabaseWorker.storage.containsKey(args[0])) {
            DatabaseWorker.storage.remove(args[0]);
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }

    }

    @Override
    public String getName() {
        return "remove";
    }
}
