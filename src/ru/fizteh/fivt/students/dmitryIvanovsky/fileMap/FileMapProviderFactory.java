package ru.fizteh.fivt.students.dmitryIvanovsky.fileMap;

import ru.fizteh.fivt.storage.strings.TableProviderFactory;
import ru.fizteh.fivt.students.dmitryIvanovsky.shell.CommandLauncher;

import java.io.File;
import java.nio.file.Path;

public class FileMapProviderFactory implements TableProviderFactory {
    private CommandLauncher.Code checkBdDir(Path pathTables) {
        File currentFile = pathTables.toFile();
        File[] listFiles = currentFile.listFiles();
        if (listFiles == null) {
            return CommandLauncher.Code.ERROR;
        }
        if (listFiles.length == 0) {
            return CommandLauncher.Code.OK;
        }
        for (File nameMap : listFiles) {
            if (!nameMap.isDirectory()) {
                //errPrint(nameMap.getAbsolutePath() + " не папка");
                return CommandLauncher.Code.ERROR;
            } else {
                //setDirTable.add(nameMap.getName());
            }
        }
        return CommandLauncher.Code.OK;
    }

    @Override
    public FileMapProvider create(String dir) {

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
