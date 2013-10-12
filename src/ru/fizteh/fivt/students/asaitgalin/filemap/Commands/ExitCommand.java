package ru.fizteh.fivt.students.asaitgalin.filemap.Commands;

import ru.fizteh.fivt.students.asaitgalin.filemap.SingleFileTable;
import ru.fizteh.fivt.students.asaitgalin.filemap.TableEntryWriter;
import ru.fizteh.fivt.students.asaitgalin.shell.Command;

import java.io.IOException;

public class ExitCommand implements Command {
    private SingleFileTable storage;
    private String dbName;

    public ExitCommand(SingleFileTable storage, String dbName) {
        this.storage = storage;
        this.dbName = dbName;
    }

    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public void execute(String[] args) throws IOException {
        System.out.println("exit");
        try {
            TableEntryWriter writer = new TableEntryWriter(dbName);
            storage.saveEntries(writer);
        } catch (IOException ioe) {
            System.err.println("Failed to save database. Internal error");
        }
        System.exit(0);
    }

    @Override
    public int getArgsCount() {
        return 0;
    }
}
