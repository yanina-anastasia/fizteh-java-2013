package ru.fizteh.fivt.students.olgagorbacheva.multyfilehashmap;

public class MultyFileHashMapException  extends Exception{

   
      private static final long serialVersionUID = 5666606200419535018L;
      private String message;
      
      public MultyFileHashMapException(String message) {
            this.message = message;
      }
            
      public String getLocalizedMessage() {
            return message;
      }
      
}