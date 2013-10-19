package ru.fizteh.fivt.students.kinanAlsarmini.filemap;

class GetCommand extends ExternalCommand {
    public GetCommand() {
        super("get", 1);
    }

    public void execute(String[] args, Table table) {
        if (table.exists(args[0])) {
            System.out.println("found");
            System.out.println(table.get(args[0]));
        } else {
            System.out.println("not found");
        }
    }
}
