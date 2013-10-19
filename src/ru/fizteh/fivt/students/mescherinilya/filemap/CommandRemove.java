package ru.fizteh.fivt.students.mescherinilya.filemap;

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
        if (FileMap.storage.containsKey(args[0])) {
            FileMap.storage.remove(args[0]);
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
