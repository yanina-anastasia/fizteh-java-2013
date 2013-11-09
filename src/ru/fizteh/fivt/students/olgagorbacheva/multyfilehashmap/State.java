package ru.fizteh.fivt.students.olgagorbacheva.multyfilehashmap;

import java.io.File;



public class State {

      DataTable dataBase;
      File dataBaseFile;
      
      public State(File file) {
            dataBaseFile = file;
            dataBase = new DataTable(file.getName(), file);
      }

      public File getDataBaseFile() {
            return dataBaseFile;
      }
}
