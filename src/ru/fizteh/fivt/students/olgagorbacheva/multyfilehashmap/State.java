package ru.fizteh.fivt.students.olgagorbacheva.multyfilehashmap;

import java.io.File;

import ru.fizteh.fivt.students.nadezhdakaratsapova.filemap.DataTable;


public class State {

      DataTable dataBase;
      File dataBaseFile;
      
      public State(File file) {
            dataBaseFile = file;
            dataBase = new DataTable(file.getName());
      }

      public File getDataBaseFile() {
            return dataBaseFile;
      }
}
