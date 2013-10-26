package ru.fizteh.fivt.students.mescherinilya.multifilehashmap;

public class CommandPut implements Command {

    public CommandPut() {
        super();
    }

    @Override
    public int getArgsCount() {
        return 2;
    }

    @Override
    public void execute(String[] args) {
        if (DatabaseWorker.currentTable == null) {
            System.out.println("no table");
            return;
        }
        if (DatabaseWorker.storage.containsKey(args[0])) {
            System.out.println("overwrite\n" + DatabaseWorker.storage.get(args[0]));
        } else {
            System.out.println("new");
        }
        DatabaseWorker.storage.put(args[0], args[1]);

    }

    @Override
    public String getName() {
        return "put";
    }
}
