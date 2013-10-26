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
        if (MultiFileHashMap.storage.containsKey(args[0])) {
            MultiFileHashMap.storage.remove(args[0]);
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
