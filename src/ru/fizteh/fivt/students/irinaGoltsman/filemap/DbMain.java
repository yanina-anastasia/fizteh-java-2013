package ru.fizteh.fivt.students.irinaGoltsman.filemap;

import ru.fizteh.fivt.students.irinaGoltsman.shell.*;

import java.util.*;

public class DbMain {
    public static void main(String[] args) {
        //String path = "C:\\Users\\Ira\\IdeaProjects\\fizteh-java-2013\\src\\ru\\fizteh\\db\\dir";
        DataBase myDataBase = new DataBase();
        try {
            //myDataBase.load(path);
            myDataBase.load();
        } catch (Exception e) {
            System.err.println("Error while loading db");
            System.err.println(e);
            Code closeCode = myDataBase.emergencyExit();
            if (closeCode != Code.OK) {
                System.err.println("Error while emergency exit");
            }
            System.exit(1);
        }
        MapOfCommands cm = new MapOfCommands();
        cm.addCommand(new ShellCommands.Exit());
        cm.addCommand(new DBCommands.Put());
        cm.addCommand(new DBCommands.Get());
        cm.addCommand(new DBCommands.Remove());
        Code codeOfShell = Shell.shell(args);
        if (codeOfShell == Code.ERROR || codeOfShell == Code.SYSTEM_ERROR) {
            myDataBase.emergencyExit();
        } else {
            Code closeCode = myDataBase.close();
            if (closeCode != Code.OK) {
                System.err.println("Error while closing");
                System.exit(1);
            }
        }
    }
}
