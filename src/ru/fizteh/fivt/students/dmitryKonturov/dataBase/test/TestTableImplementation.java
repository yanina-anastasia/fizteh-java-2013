package ru.fizteh.fivt.students.dmitryKonturov.dataBase.test;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.students.dmitryKonturov.dataBase.databaseImplementation.TableImplementation;
import ru.fizteh.fivt.students.dmitryKonturov.dataBase.databaseImplementation.TableProviderFactoryImplementation;
import ru.fizteh.fivt.students.dmitryKonturov.dataBase.databaseImplementation.TableProviderImplementation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestTableImplementation {
    private TableProviderImplementation provider;
    private File correctTableName;
    private TableImplementation correctTable;
    private List<Class<?>> correctTypeList;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void initializeTemporaryFolder() throws IOException {
        TableProviderFactoryImplementation factory = new TableProviderFactoryImplementation();
        assertNotNull("Factory should be not null", factory);

        File providerWorkspace = tempFolder.newFolder("workspace");

        correctTableName = new File(providerWorkspace, "correctTable");
        assertTrue(correctTableName.mkdir());

        File correctTableSignatureFile = new File(correctTableName, "signature.tsv");
        assertTrue(correctTableSignatureFile.createNewFile());
        try (FileWriter sign = new FileWriter(correctTableSignatureFile)) {
            sign.write("int String double boolean byte long float");
        }

        provider = (TableProviderImplementation) factory.create(providerWorkspace.toString());
        assertNotNull("Provider should be not null", provider);

        correctTypeList = new ArrayList<>();
        correctTypeList.add(Integer.class);
        correctTypeList.add(String.class);
    }

}
