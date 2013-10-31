package ru.fizteh.fivt.students.ryabovaMaria.fileMap;

import java.lang.reflect.Method;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.ryabovaMaria.shell.AbstractCommands;

public class FileMapCommands extends AbstractCommands {
    private TableProviderFactory myTableProviderFactory;
    private TableProvider myTableProvider;
    private Table myTable = null;
    private boolean usingTable = false;
    
    public FileMapCommands(String curDir) {
        myTableProviderFactory = new MyTableProviderFactory();
        myTableProvider = myTableProviderFactory.create(curDir);
    }
    
    public void create() throws Exception {
        String tableName = lexems[1];
        try {
            if (myTableProvider.createTable(tableName) == null) {
                System.out.println(tableName + " exists");
            } else {
                System.out.println("created");
            }
        } catch (IllegalArgumentException e) {
            throw new Exception("create: " + e);
        }
    }
    
    public void drop() throws Exception {
        String tableName = lexems[1];
        try {
            myTableProvider.removeTable(tableName);
            System.out.println("dropped");
        } catch (IllegalStateException e) {
            System.out.println(tableName + " not exists");
        } catch (IllegalArgumentException e) {
            throw new Exception("drop: " + e);
        }
    }
        
    public void use() throws Exception {
        String tableName = lexems[1];
        if (myTable != null) {
            Class c = myTable.getClass();
            Method makeCommand = c.getMethod("countChanges", boolean.class);
            int counted = (int) makeCommand.invoke(myTable, false);
            if (counted != 0) {
                System.out.println(counted + " unsave changes");
                return;
            }
        }
        try {
            myTable = myTableProvider.getTable(tableName);
            if (myTable == null) {
                System.out.println(tableName + " not exists");
            } else {
                usingTable = true;
                System.out.println("using " + tableName);
            }
        } catch (IllegalArgumentException e) {
            throw new Exception("Incorrect table");
        }
    }

    private void parse() {
        String[] tempLexems = new String[0];
        if (lexems.length > 1) {
            tempLexems = lexems[1].split("[ \t\n\r]+", 2);
        }
        lexems = tempLexems;
    }
    
    private void checkTheNumbOfArgs(int n, String commandName) throws Exception {
        if (lexems.length < n) {
            throw new Exception(commandName + ": there is no enough arguments.");
        }
        if (lexems.length > n) {
            throw new Exception(commandName + ": there is so many arguments.");
        }
    }
       
    public void put() throws Exception {
        if (!usingTable) {
            System.out.println("no table");
            return;
        }
        parse();
        checkTheNumbOfArgs(2, "put");
        try {
            String previousValue = myTable.put(lexems[0], lexems[1]);
            if (previousValue == null) {
                System.out.println("new");
            } else {
                System.out.println("overwrite");
                System.out.println(previousValue);
            }
        } catch (IllegalArgumentException e) {
            throw new Exception("put: Incorrect arguments");
        }
    }
    
    public void get() throws Exception {
        if (!usingTable) {
            System.out.println("no table");
            return;
        }
        parse();
        try {
            checkTheNumbOfArgs(1, "get");
            String value = myTable.get(lexems[0]);
            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("found");
                System.out.println(value);
            }
        } catch (IllegalArgumentException e) {
            throw new Exception("get: Incorrect argument");
        }
    }
    
    public void remove() throws Exception {
        if (!usingTable) {
            System.out.println("no table");
            return;
        }
        parse();
        checkTheNumbOfArgs(1, "remove");
        try {
            String value = myTable.remove(lexems[0]);
            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("removed");
            }
        } catch (IllegalArgumentException e) {
            throw new Exception("remove: Incorrect argument");
        }
    }
    
    public void size() throws Exception {
        if (!usingTable) {
            System.out.println("no table");
            return;
        }
        parse();
        checkTheNumbOfArgs(0, "size");
        System.out.println(myTable.size());
    }
    
    public void commit() throws Exception {
        if (!usingTable) {
            System.out.println("no table");
            return;
        }
        parse();
        checkTheNumbOfArgs(0, "commit");
        System.out.println(myTable.commit());
    }
    
    public void rollback() throws Exception {
        if (!usingTable) {
            System.out.println("no table");
            return;
        }
        parse();
        checkTheNumbOfArgs(0, "rollback");
        System.out.println(myTable.rollback());
    }
    
    public void exit() {
        System.exit(0);
    }
}
