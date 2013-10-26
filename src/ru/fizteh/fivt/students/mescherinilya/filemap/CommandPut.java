package ru.fizteh.fivt.students.mescherinilya.filemap;

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
        if (FileMap.storage.containsKey(args[0])) {
            System.out.println("overwrite\n" + FileMap.storage.get(args[0]));
        } else {
            System.out.println("new");
        }
        FileMap.storage.put(args[0], args[1]);

    }

    @Override
    public String getName() {
        return "put";
    }
}
