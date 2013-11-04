package ru.fizteh.fivt.students.olgagorbacheva.multyfilehashmap;

import java.io.File;
import java.io.IOException;

import ru.fizteh.fivt.students.olgagorbacheva.filemap.ExitCommand;
import ru.fizteh.fivt.students.olgagorbacheva.filemap.FileMapException;
import ru.fizteh.fivt.students.olgagorbacheva.filemap.GetCommand;
import ru.fizteh.fivt.students.olgagorbacheva.filemap.PutCommand;
import ru.fizteh.fivt.students.olgagorbacheva.filemap.ReadWrite;
import ru.fizteh.fivt.students.olgagorbacheva.filemap.RemoveCommand;
import ru.fizteh.fivt.students.olgagorbacheva.filemap.Storage;
import ru.fizteh.fivt.students.olgagorbacheva.shell.State;
import ru.fizteh.fivt.students.olgagorbacheva.shell.Shell;
import ru.fizteh.fivt.students.olgagorbacheva.shell.Command;

public class MultyFileHashMap {
      
      private State state;
      private ReadWrite rw;
      private Storage storage;
      
      public MultyFileHashMap() {
            state = new State();
            storage = new Storage();
            rw = new ReadWrite(storage);
      }
      
      public void exectute(String[] args) {
            Shell sh = new Shell (state);
            Command create = new CreateCommand();
            sh.addCommand(create);
            Command drop = new DropCommand();
            sh.addCommand(drop);
            Command use = new UseCommand(rw);
            sh.addCommand(use);
            Command get = new GetCommand (storage);
            sh.addCommand(get);
            Command put = new PutCommand(storage);
            sh.addCommand(put);
            Command remove = new RemoveCommand(storage);
            sh.addCommand(remove);
            Command exit = new ExitCommand();
            sh.addCommand(exit);
            sh.execute(args);
            try {
                  if (!state.getState().equals(
                              new File(System
                                          .getProperty("fizteh.db.dir"))
                                          .getAbsolutePath())) {
                        rw.writeFile(state);
                  }
            } catch (IOException | FileMapException exp) {
                  System.err.println(exp.getLocalizedMessage());
                  System.exit(1);
            }

      }
}
