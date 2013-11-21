package ru.fizteh.fivt.students.mescherinilya.multifilehashmap;

import java.io.File;
import java.io.IOException;
import java.util.TreeMap;

public class CommandUse implements Command {
    public CommandUse() {
        super();
    }

    @Override
    public String getName() {
        return "use";
    }

    @Override
    public int getArgsCount() {
        return 1;
    }

    @Override
    public void execute(String[] args) throws Exception {

        //сначала закоммитим все произведенные изменения
        if (DatabaseWorker.currentTable != null) {
            DatabaseWorker.writeDatabase();
        }

        //затем попробуем задать новую директорию для работы
        File newCurrentTable = new File(MultiFileHashMap.rootDir + File.separator + args[0]);

        if (!newCurrentTable.exists() || !newCurrentTable.isDirectory()) {
            System.out.println(args[0] + " not exists");
            return;
        }

        DatabaseWorker.currentTable = newCurrentTable;
        DatabaseWorker.storage = new TreeMap<String, String>();

        DatabaseWorker.readDatabase();

        System.out.println("using " + args[0]);
    }
}
