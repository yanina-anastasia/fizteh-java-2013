package ru.fizteh.fivt.students.baldindima.junit;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
public class DataBaseTable implements TableProvider {
   
    private String tableDirectory;
	private Map<String, DataBase> tables;
    

   

   /* public void saveTable() throws IOException {
        if (dataBase != null) {
            dataBase.saveDataBase();
        }

    }*/

    

   /* public String get(String keyString) {
        return dataBase.get(keyString);
    }

    public String put(String keyString, String valueString) {
        return dataBase.put(keyString, valueString);
    }

    public String remove(String keyString) {
        return dataBase.remove(keyString);
    }
    public int commit() throws IOException{
    	return dataBase.commit();
    }
    public int rollback() throws IOException{
    	return dataBase.rollback();
    }
    public int size(){
    	return dataBase.size();
    }
    public boolean exists(){
    	return (dataBase != null);
    }*/

    
	
	
	public DataBaseTable(String nTableDirectory) {
        tableDirectory = nTableDirectory;
        tables = new HashMap();
    }
	public DataBase getTableFromMap(String name){
		if (!tables.containsKey(name)) {
            try {
				tables.put(name, new DataBase(name));
			} catch (IOException e) {
				throw new RuntimeException("cannot get table");
			}
        }
        return tables.get(name);
	}
	public void deleteTableFromMap(String name) {
	       if (tables.containsKey(name)) {
	           tables.remove(name);
	       }
	    }
	private void checkName(String name) {
        if ((name == null) || name.trim().length() == 0) {
            throw new IllegalArgumentException("Cannot create table! Wrong name!");
        }

        if (name.matches("[" + '"' + "'\\/:/*/?/</>/|/.\\\\]+") || name.contains(File.separator)
                || name.contains(".")) {
            throw new RuntimeException("Wrong symbols");
        }
    }
	
	public Table createTable(String tableName) {
        checkName(tableName);
        String fullPath = tableDirectory + File.separator + tableName;

        File file = new File(fullPath);

        if (file.exists()) {
            return null;
        }

        if (!file.mkdir()) {
            throw new RuntimeException("Cannot create table " + tableName);
        }

        return getTableFromMap(fullPath);
    }
	
	public Table getTable(String tableName) {
        checkName(tableName);
        String fullPath = tableDirectory + File.separator + tableName;

        File file = new File(fullPath);
        if ((!file.exists()) || (file.isFile())) {
            return null;
        }
        return getTableFromMap(fullPath);
    }
	public void removeTable(String tableName) {
        checkName(tableName);
        String fullPath = tableDirectory + File.separator + tableName;

        File file = new File(fullPath);
        if (!file.exists()) {
            throw new IllegalStateException("Table not exist");
        }

        DataBase base = getTableFromMap(fullPath);
        try {
			base.drop();
		} catch (IOException e) {
			throw new RuntimeException("Cannot delete a table " + tableName);
		}
        if (!file.delete()) {
            throw new RuntimeException("Cannot delete a table " + tableName);
        }
        deleteTableFromMap(fullPath);
    }
	


}
