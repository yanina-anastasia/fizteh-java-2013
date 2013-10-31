package ru.fizteh.fivt.students.belousova.multifilehashmap;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class MultiFileHashMap {
    public static void main(String[] args) {
        MultiFileShell shell = new MultiFileShell();
        String dir = System.getProperty("fizteh.db.dir");
        ChangesCountingTableProviderFactory tableProviderFactory = new MultiFileTableProviderFactory();
        ChangesCountingTableProvider tableProvider = tableProviderFactory.create(dir);
        MultiFileShellState multiFileShellState = new MultiFileShellState(tableProvider, null);
        shell.run(args, multiFileShellState);
    }
}
