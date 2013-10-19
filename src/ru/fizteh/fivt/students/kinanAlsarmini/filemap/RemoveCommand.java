package ru.fizteh.fivt.students.kinanAlsarmini.filemap;

class RemoveCommand extends ExternalCommand {
    public RemoveCommand() {
        super("remove", 1);
    }

    public void execute(String[] args, Table table) {
        if (table.exists(args[0])) {
            table.remove(args[0]);
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
    }
};
