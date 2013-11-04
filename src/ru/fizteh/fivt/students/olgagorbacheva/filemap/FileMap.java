package ru.fizteh.fivt.students.olgagorbacheva.filemap;


import ru.fizteh.fivt.students.olgagorbacheva.shell.State;
import java.io.FileNotFoundException;
import java.io.IOException;
import ru.fizteh.fivt.students.olgagorbacheva.shell.Shell;
import ru.fizteh.fivt.students.olgagorbacheva.shell.Command;

public class FileMap {

      /**
       * @param args
       */
      private State state;
      private Storage storage;

      public FileMap(String tableName) {
            state = new State(tableName);
            storage = new Storage();
      }

      public void execute(String[] args) {
            ReadWrite rw = new ReadWrite(storage);

            try {
                  rw.readFile(state);
            } catch (FileNotFoundException exp) {
                  System.err.println("Файл не найден");
                  System.exit(1);
            } catch (IOException exp) {
                  System.err.println("Ошибка чтения файла");
                  System.exit(1);
            } catch (FileMapException exp) {
                  System.err.println(exp.getLocalizedMessage());
                  System.exit(1);
            }

            Shell sh = new Shell (state);
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
                  rw.writeFile(state);
            } catch (FileNotFoundException exp) {
                  System.err.println("Файл не найден");
                  System.exit(1);
            } catch (IOException exp) {
                  System.err.println("Ошибка записи в файл");
                  System.exit(1);
            } catch (FileMapException exp) {
                  System.err.println(exp.getLocalizedMessage());
                  System.exit(1);
            }
      }

}