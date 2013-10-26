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
        if (MultiFileHashMap.storage.containsKey(args[0])) {
            System.out.println("overwrite\n" + MultiFileHashMap.storage.get(args[0]));
        } else {
            System.out.println("new");
        }
        MultiFileHashMap.storage.put(args[0], args[1]);

    }

    @Override
    public String getName() {
        return "put";
    }
}
