package ru.fizteh.fivt.students.kinanAlsarmini.filemap;

class PutCommand extends ExternalCommand {
    public PutCommand() {
        super("put", 2);
    }

    public void execute(String[] args, Table table) {
        if (table.exists(args[0])) {
            System.out.println("overwrite");
            System.out.println(table.get(args[0]));
        } else {
            System.out.println("new");
        }

        table.put(args[0], args[1]);
    }
}
