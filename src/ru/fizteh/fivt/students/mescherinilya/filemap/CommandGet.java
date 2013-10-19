package ru.fizteh.fivt.students.mescherinilya.filemap;

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
        if (FileMap.storage.containsKey(args[0])) {
            System.out.println("found\n" + FileMap.storage.get(args[0]));
        } else {
            System.out.println("not found");
        }

    }

    @Override
    public String getName() {
        return "get";
    }
}
