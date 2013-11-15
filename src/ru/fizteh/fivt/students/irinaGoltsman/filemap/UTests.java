package ru.fizteh.fivt.students.irinaGoltsman.filemap;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.irinaGoltsman.multifilehashmap.DBTableProviderFactory;
import ru.fizteh.fivt.students.irinaGoltsman.shell.Code;
import ru.fizteh.fivt.students.irinaGoltsman.shell.MapOfCommands;
import ru.fizteh.fivt.students.irinaGoltsman.shell.Shell;
import ru.fizteh.fivt.students.irinaGoltsman.shell.ShellCommands;

import java.io.IOException;

public class UTests {
    TableProviderFactory factory;
    TableProvider tableProvider;
    MapOfCommands cm = new MapOfCommands();
    DataBase dataBase;
    @Rule
    public TemporaryFolder rootDBDirectory = new TemporaryFolder();

    @Before
    public void init() throws IOException {
        factory = new DBTableProviderFactory();
        tableProvider = factory.create(rootDBDirectory.toString());
        dataBase = new DataBase(tableProvider);
        cm.addCommand(new ShellCommands.Exit());
        cm.addCommand(new DBCommands.Put());
        cm.addCommand(new DBCommands.Get());
        cm.addCommand(new DBCommands.Remove());
        cm.addCommand(new DBCommands.Commit());
        cm.addCommand(new DBCommands.CreateTable());
        cm.addCommand(new DBCommands.Drop());
        cm.addCommand(new DBCommands.Use());
        cm.addCommand(new DBCommands.Size());
        cm.addCommand(new DBCommands.RollBack());
    }

    @Test
    public void simpleWork() {
        String[] args = new String[1];
        args[0] = "use newTable";
        Code codeOfShell = Shell.shell(args);
    }
}
