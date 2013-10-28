package ru.fizteh.fivt.students.piakovenko.filemap;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.piakovenko.shell.MyException;
import ru.fizteh.fivt.students.piakovenko.shell.Shell;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Pavel
 * Date: 19.10.13
 * Time: 20:17
 * To change this template use File | Settings | File Templates.
 */
public class DataBasesCommander implements TableProvider {
    private File dataBaseDirectory = null;
    private DataBase currentDataBase = null;
    private Map<String, DataBase> filesMap = new HashMap<String, DataBase>();
    private Shell shell = null;

    private static File getMode (File directory) {
        for (File f: directory.listFiles()) {
            if (f.getName().equals("db.dat") && f.isFile())
                return f;
        }
        return null;
    }

    private void fulfillFiles() {
        for (File f: dataBaseDirectory.listFiles()){
            filesMap.put(f.getName(), new DataBase(shell, f));
        }
    }

    private void changeCommandsStatus(DataBase database) throws MyException {
        shell.changeCommandStatus("put", database);
        shell.changeCommandStatus("get", database);
        shell.changeCommandStatus("remove", database);
        shell.changeCommandStatus("exit", database);
        shell.changeCommandStatus("size", database);
        shell.changeCommandStatus("commit", database);
        shell.changeCommandStatus("rollback", database);
    }

    public DataBasesCommander () {
        shell = new Shell();
        dataBaseDirectory = new File(System.getProperty("fizteh.db.dir"));
        fulfillFiles();
        File modeFile = null;
        if ((modeFile = getMode(dataBaseDirectory)) != null ) {
            currentDataBase = new DataBase(shell, modeFile);
            currentDataBase.initialize();
            shell.changeInvitation("Database $ ");
        } else {
            fulfillFiles();
            initialize();
            shell.changeInvitation("MultiFile Database $ ");
        }
    }

    public DataBasesCommander(Shell s, File storage) {
        shell = s;
        dataBaseDirectory = storage;
        fulfillFiles();
        File modeFile = null;
        if ((modeFile = getMode(storage)) != null ) {
            currentDataBase = new DataBase(shell, modeFile);
            currentDataBase.initialize();
            shell.changeInvitation("Database $ ");
        } else {
            fulfillFiles();
            initialize();
            shell.changeInvitation("MultiFile Database $ ");
        }
    }

    public void use (String dataBase) throws MyException, IOException {
        if (filesMap.containsKey(dataBase)) {
            if (currentDataBase != null && currentDataBase.numberOfChanges() != 0) {
                System.out.println(currentDataBase.numberOfChanges() + " unsaved changes");
                return;
            }
            if (filesMap.get(dataBase).equals(currentDataBase)) {
                System.out.println("using " + dataBase);
                return;
            }  else if (currentDataBase != null) {
                currentDataBase.saveDataBase();
            }
            currentDataBase = filesMap.get(dataBase);
            changeCommandsStatus(currentDataBase);
            currentDataBase.load();
            System.out.println("using " + dataBase);
        } else {
            System.out.println(dataBase + " not exists");
        }
    }

    public void removeTable (String dataBase) throws IllegalArgumentException {
        if (dataBase == null) {
            throw new IllegalArgumentException("Null pointer to dataBase name");
        }
        if (filesMap.containsKey(dataBase)) {
            if (filesMap.get(dataBase).equals(currentDataBase)) {
                currentDataBase = null;
                try {
                    changeCommandsStatus(currentDataBase);
                } catch (MyException e) {
                    System.err.println("Error! " + e.what());
                    System.exit(1);
                }
            }
            try {
                ru.fizteh.fivt.students.piakovenko.shell.Remove.removeRecursively(filesMap.get(dataBase).returnFiledirectory());
            } catch (IOException e) {
                System.err.println("Error! " + e.getMessage());
                System.exit(1);
            } catch (MyException e) {
                System.err.println("Error! " + e.what());
                System.exit(1);
            }
            filesMap.remove(dataBase);
            System.out.println("dropped");
        } else {
            System.out.println(dataBase + " not exists");
            throw new IllegalArgumentException(dataBase +" not exists");
        }
    }

    public Table createTable (String dataBase) throws IllegalArgumentException{
        if (dataBase == null) {
            throw new IllegalArgumentException("Null pointer to name!");
        }
        if (filesMap.containsKey(dataBase)) {
            System.out.println(dataBase + " exists");
        } else {
            File newFileMap = new File(dataBaseDirectory, dataBase);
            if (!newFileMap.mkdirs()){
                System.err.println("Unable to create this directory - " + dataBase);
                System.exit(1);
            }
            System.out.println("created");
            filesMap.put(dataBase, new DataBase(shell, newFileMap ));
            return filesMap.get(dataBase);
        }
        return null;
    }

    public Table getTable(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("Null pointer to name of Table");
        }
        if (filesMap.containsKey(name)) {
            return filesMap.get(name);
        }
        return null;
    }

    public void initialize() {
        shell.addCommand(new Exit(currentDataBase));
        shell.addCommand(new Drop(this));
        shell.addCommand(new Create(this));
        shell.addCommand(new Use(this));
        shell.addCommand(new Get(currentDataBase));
        shell.addCommand(new Put(currentDataBase));
        shell.addCommand(new Remove(currentDataBase));
        shell.addCommand(new Size(currentDataBase));
        shell.addCommand(new Commit(currentDataBase));
        shell.addCommand(new Rollback(currentDataBase));
    }
}
