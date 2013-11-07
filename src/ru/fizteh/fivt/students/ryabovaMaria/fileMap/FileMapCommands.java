package ru.fizteh.fivt.students.ryabovaMaria.fileMap;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.ryabovaMaria.shell.AbstractCommands;

public class FileMapCommands extends AbstractCommands {
    private TableProviderFactory myTableProviderFactory;
    private TableProvider myTableProvider;
    private Table myTable = null;
    private boolean usingTable = false;
    
    public FileMapCommands(String curDir) throws IOException {
        String propertyString = System.getProperty(curDir);
        if (propertyString == null) {
            System.err.println("Bad property");
            System.exit(1);
        }
        File currentDirectory = new File(propertyString);
        myTableProviderFactory = new MyTableProviderFactory();
        myTableProvider = myTableProviderFactory.create(currentDirectory.toString());
    }
    
    public void create() throws Exception {
        if (lexems.length < 2) {
            System.out.println("wrong type (incorrect nubmer of args)");
            return;
        }
        String[] temp = lexems[1].split("[ ]+", 2);
        if (temp.length < 2) {
            System.out.println("wrong type (incorrect number of args)");
            return;
        }
        String tableName = temp[0];
        String tempString = temp[1].trim();
        temp = tempString.split("[()]");
        if ((temp.length <= 0) || !temp[0].isEmpty() || (temp.length > 2)) {
            System.out.println("wrong type (illegal arguments)");
            return;
        }
        tempString = temp[1];
        temp = tempString.split("[ ]+");
        ArrayList<Class<?>> types = new ArrayList();
        for (int i = 0; i < temp.length; ++i) {
            switch (temp[i].trim()) {
                case "int" :
                    types.add(Integer.class);
                    break;
                case "long" :
                    types.add(Long.class);
                    break;
                case "byte" :
                    types.add(Byte.class);
                    break;
                case "float" :
                    types.add(Float.class);
                    break;
                case "double" :
                    types.add(Double.class);
                    break;
                case "boolean" :
                    types.add(Boolean.class);
                    break;
                case "String" :
                    types.add(String.class);
                    break;
                default :
                    System.out.println("wrong type (illegal column type)");
                    return;
            }
        }
        try {
            if (myTableProvider.createTable(tableName, types) == null) {
                System.out.println(tableName + " exists");
            } else {
                System.out.println("created");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("wrong type (" + e.getMessage() + ")");
        }
    }
    
    public void drop() throws Exception {
        String tableName = lexems[1];
        if (myTable != null) {
            if (myTable.getName().equals(tableName)) {
                System.out.println("wrong type (this table is used)");
                return;
            }
        }
        try {
            myTableProvider.removeTable(tableName);
            System.out.println("dropped");
        } catch (IllegalStateException e) {
            System.out.println(tableName + " not exists");
        } catch (IllegalArgumentException e) {
            System.out.println("wrong type (" + e.getMessage() + ")");
        }
    }
        
    public void use() throws Exception {
        String tableName = lexems[1];
        if (myTable != null) {
            Class c = myTable.getClass();
            Method makeCommand = c.getMethod("countChanges", boolean.class);
            int counted = (int) makeCommand.invoke(myTable, false);
            if (counted != 0) {
                System.out.println(counted + " unsaved changes");
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
        } catch (Exception e) {
            System.out.println("wrong type (incorrect table)");
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
        try {
            checkTheNumbOfArgs(2, "put");
        } catch (Exception e) {
            System.out.println("wrong type (incorrect number of args)");
            return;
        }
        try {
            Storeable curValue = myTableProvider.deserialize(myTable, lexems[1]);
            Storeable previousValue = myTable.put(lexems[0], curValue);
            String stringPreviousValue = myTableProvider.serialize(myTable, previousValue);
            if (previousValue == null) {
                System.out.println("new");
            } else {
                System.out.println("overwrite");
                System.out.println(stringPreviousValue);
            }
        } catch (Exception e) {
            System.out.println(" wrong type (incorrect args: " + e.getMessage() + ")");
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
        } catch (Exception e) {
            System.out.println("wrong type (incorrect number of args)");
            return;
        }
        try {
            Storeable value = myTable.get(lexems[0]);
            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("found");
                System.out.println(myTableProvider.serialize(myTable, value));
            }
        } catch (IllegalArgumentException e) {
            System.out.println("wrong type (incorrect argument)");
        }
    }
    
    public void remove() throws Exception {
        if (!usingTable) {
            System.out.println("no table");
            return;
        }
        parse();
        try {
            checkTheNumbOfArgs(1, "remove");
        } catch (Exception e) {
            System.out.println("wrong type (incorrect number of args)");
            return;
        }
        try {
            Storeable value = myTable.remove(lexems[0]);
            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("removed");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("wrong type (Incorrect argument)");
        }
    }
    
    public void size() throws Exception {
        if (!usingTable) {
            System.out.println("no table");
            return;
        }
        parse();
        try {
            checkTheNumbOfArgs(0, "size");
        } catch (Exception e) {
            System.out.println("wrong type (incorrect number of args)");
            return;
        }
        System.out.println(myTable.size());
    }
    
    public void commit() throws Exception {
        if (!usingTable) {
            System.out.println("no table");
            return;
        }
        parse();
        try {
            checkTheNumbOfArgs(0, "commit");
        } catch (Exception e) {
            System.out.println("wrong type (incorrect number of args)");
            return;
        }
        try {
            System.out.println(myTable.commit());
        } catch (Exception e) {
            System.out.println("wrong type (" + e.getMessage() + ")");
        }
    }
    
    public void rollback() throws Exception {
        if (!usingTable) {
            System.out.println("no table");
            return;
        }
        parse();
        try {
            checkTheNumbOfArgs(0, "rollback");
        } catch (Exception e) {
            System.out.println("wrong type (incorrect number of args)");
            return;
        }
        try {
            System.out.println(myTable.rollback());
        } catch (Exception e) {
            System.out.println("wrong type (" + e.getMessage() + ")");
        }
    }
    
    public void exit() throws Exception {
        if (myTable != null) {
            Class c = myTable.getClass();
            Method makeCommand = c.getMethod("countChanges", boolean.class);
            int counted = (int) makeCommand.invoke(myTable, false);
            if (counted != 0) {
                System.out.println(counted + " unsaved changes");
                return;
            }
        }
        System.exit(0);
    }
}
