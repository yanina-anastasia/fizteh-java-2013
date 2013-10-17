package ru.fizteh.fivt.students.nadezhdakaratsapova.filemap;


import java.io.IOException;

public class PutCommand implements Command {

    public String getName() {
        return "put";
    }

    public void execute(String[] args, DataTable dataTable) throws IOException {
        String value = dataTable.getValue(args[1]);
        dataTable.add(args[1], args[2]);
        if (value == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(value);
        }
    }

    public int getArgsCount() {
        return 2;
    }

}
