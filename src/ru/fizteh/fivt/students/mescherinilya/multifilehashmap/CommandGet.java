package ru.fizteh.fivt.students.mescherinilya.multifilehashmap;

public class CommandGet implements Command {

    public CommandGet() {
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
            System.out.println("found\n" + DatabaseWorker.storage.get(args[0]));
        } else {
            System.out.println("not found");
        }

    }

    @Override
    public String getName() {
        return "get";
    }
}
