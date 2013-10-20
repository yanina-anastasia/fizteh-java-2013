package ru.fizteh.fivt.students.paulinMatavina.filemap;

import ru.fizteh.fivt.students.paulinMatavina.utils.*;

public class FileMap {
    public static void main(String[] args) {
        DbState state = new DbState();
        
        state.add(new DbGet());
        state.add(new DbPut());
        state.add(new DbExit());
        state.add(new DbRemove());
        
        CommandRunner.run(args, state);
    }
}
