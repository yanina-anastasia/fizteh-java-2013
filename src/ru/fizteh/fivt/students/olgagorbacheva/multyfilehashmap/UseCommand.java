package ru.fizteh.fivt.students.olgagorbacheva.multyfilehashmap;

import java.io.File;
import java.io.IOException;

import ru.fizteh.fivt.students.olgagorbacheva.filemap.FileMapException;
import ru.fizteh.fivt.students.olgagorbacheva.filemap.ReadWrite;
import ru.fizteh.fivt.students.olgagorbacheva.shell.ShellException;
import ru.fizteh.fivt.students.olgagorbacheva.shell.State;
import ru.fizteh.fivt.students.olgagorbacheva.shell.Command;
import ru.fizteh.fivt.students.olgagorbacheva.shell.ChangeDirectoryCommand;

public class UseCommand implements Command {

      private String name = "use";
      private final int argNumber = 1;

      private ReadWrite rw;

      public UseCommand(ReadWrite rw) {
            this.rw = rw;
      }

      public void execute(String[] args, State state) {
            File table = new File(new File(System
                        .getProperty("fizteh.db.dir"))
                        .getAbsolutePath(), args[1]);
            if (!table.exists()) {
                  System.out.println("tablename not exists");
            } else {
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
                  ChangeDirectoryCommand cd = new ChangeDirectoryCommand();
                  try {
                        String[] toRoot = new String[2];
                        toRoot[0] = "cd";
                        toRoot[1] = new File(System
                                    .getProperty("fizteh.db.dir"))
                                    .getAbsolutePath();
                        cd.execute(toRoot, state);
                        cd.execute(args, state);
                  } catch (ShellException exp) {
                        System.out.println("ошибка смены таблицы");
                  } finally {
                        System.out.println("using tablename");
                  }
                  try {
                        rw.readFile(state);
                  } catch (IOException
                              | FileMapException exp) {
                        System.err.println(exp.getLocalizedMessage());
                        System.exit(1);

                  }
            }
      }

      public String getName() {
            return name;
      }

      public int getArgNumber() {
            return argNumber;
      }

}