package ru.fizteh.fivt.students.paulinMatavina.filemap;

import ru.fizteh.fivt.students.paulinMatavina.utils.CommandRunner;

public class FileMap {
    public static void main(String[] args) {
        try {
            String property = System.getProperty("fizteh.db.dir");
            MultiDbState state = new MultiDbState(property, "db.dat");  
            CommandRunner.run(args, state);
        } catch (IllegalArgumentException e) {
            System.err.println("multifilemap: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.exit(Integer.parseInt(e.getMessage()));
        }
    }
}
