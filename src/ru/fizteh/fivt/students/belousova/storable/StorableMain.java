package ru.fizteh.fivt.students.belousova.storable;

import java.io.IOException;

public class StorableMain {
    public static void main(String[] args) {
        StorableShell shell = new StorableShell();
        String dir = System.getProperty("fizteh.db.dir");
        StorableTableProviderFactory tableProviderFactory = new StorableTableProviderFactory();
        try {
            StorableShellState storableShellState = new StorableShellState(null, tableProviderFactory.create(dir));
            shell.run(args, storableShellState);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
