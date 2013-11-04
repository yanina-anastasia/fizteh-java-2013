package ru.fizteh.fivt.students.olgagorbacheva.filemap;

public class MainClass {

      /**
       * @param args
       */
      public static void main(String[] args) {
            
            FileMap fm = new FileMap("db");
            fm.execute(args);

      }

}