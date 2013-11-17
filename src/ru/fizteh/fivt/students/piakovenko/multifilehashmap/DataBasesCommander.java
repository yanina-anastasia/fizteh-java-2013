package ru.fizteh.fivt.students.piakovenko.multifilehashmap;

import ru.fizteh.fivt.students.piakovenko.filemap.*;
import ru.fizteh.fivt.students.piakovenko.shell.CurrentStatus;
import ru.fizteh.fivt.students.piakovenko.shell.MakeDirectory;
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
public class DataBasesCommander {
    private File dataBaseDirectory = null;
    private DataBase currentDataBase = null;
    private Map<String, DataBase> filesMap = new HashMap<String, DataBase>();
    private Shell shell = null;

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
    }


    public DataBasesCommander(Shell s, File storage) {
        shell = s;
        dataBaseDirectory = storage;
        fulfillFiles();
    }

    public void use (String dataBase) throws MyException, IOException {
        if (filesMap.containsKey(dataBase)) {
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

    public void drop (String dataBase) throws IOException, MyException{
        if (filesMap.containsKey(dataBase)) {
            if (filesMap.get(dataBase).equals(currentDataBase)) {
                currentDataBase = null;
                changeCommandsStatus(currentDataBase);
            }
            ru.fizteh.fivt.students.piakovenko.shell.Remove.removeRecursively(filesMap.get(dataBase).returnFiledirectory());
            filesMap.remove(dataBase);
            System.out.println("dropped");
        } else {
            System.out.println(dataBase + " not exists");
        }
    }

    public void create (String dataBase) throws IOException, MyException {
        if (filesMap.containsKey(dataBase)) {
            System.out.println(dataBase + " exists");
        } else {
            File newFileMap = new File(dataBaseDirectory, dataBase);
            if (!newFileMap.mkdirs()){
                throw new MyException(new Exception("Unable to create this directory - " + newFileMap.getCanonicalPath()));
            }
            System.out.println("created");
            filesMap.put(dataBase, new DataBase(shell, newFileMap ));
        }
    }

    public void initialize() {
        shell.addCommand(new Exit(currentDataBase));
        shell.addCommand(new Drop(this));
        shell.addCommand(new Create(this));
        shell.addCommand(new Use(this));
        shell.addCommand(new Get(currentDataBase));
        shell.addCommand(new Put(currentDataBase));
        shell.addCommand(new Remove(currentDataBase));
    }
}
