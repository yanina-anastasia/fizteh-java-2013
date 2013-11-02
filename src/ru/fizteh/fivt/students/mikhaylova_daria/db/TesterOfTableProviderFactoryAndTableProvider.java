package ru.fizteh.fivt.students.mikhaylova_daria.db;


import org.junit.*;
import ru.fizteh.fivt.storage.strings.*;
import ru.fizteh.fivt.students.mikhaylova_daria.shell.MyFileSystem;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;

public class TesterOfTableProviderFactoryAndTableProvider {

    private static TableProviderFactory factory;
    private static TableProvider manager;
    private static File existingFile;
    private static File workingDirFile;

    public static void removeFile(String name) {
        try {
            MyFileSystem.removing(name);
        } catch (IOException e) {
            System.err.println("Ошибка при удалении временного файла");
            System.exit(1);
        }
    }

    @BeforeClass
    public  static void beforeClass() throws Exception {
        factory = new TableManagerFactory();
        File tempDb = File.createTempFile("darya", "mikhailova");
        String workingDir = tempDb.getName();
        if (!tempDb.delete()) {
            System.err.println("Ошибка при удалении временного файла");
            System.exit(1);
        }
        workingDirFile = new File(workingDir);
        if (!workingDirFile.mkdir()) {
            System.err.println("Ошибка при создании временного файла");
            System.exit(1);
        }
        try {
            manager = factory.create(workingDir);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Bad directory", e);
        }
        File temp = File.createTempFile("darya", "mikhailova");
        String name = temp.getName();
        if (!temp.delete()) {
            System.err.println("Ошибка при удалении временного файла");
            System.exit(1);
        }
        existingFile = new File(workingDirFile, name);
        if (!existingFile.mkdir()) {
            System.err.println("Ошибка при создании временного файла");
            System.exit(1);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTableManagerByNullStringShouldFail() {
        TableProvider obj = factory.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createdTableByNullStringShouldFail() {
        manager.createTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTableSpaceNameShouldFail() {
        manager.createTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTableBadCharInNameShouldFail1() {
        manager.createTable("a/b");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTableBadCharInNameShouldFail2() {
        manager.createTable("a\\b");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTableBadCharInNameShouldFail3() {
        manager.createTable("..");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTableBadCharInNameShouldFail4() {
        manager.createTable(".");
    }

    @Test
    public void createExistingTableShouldReturnNull() {
        String name = existingFile.getName();
        assertNull(manager.createTable(name));
    }



    @Test(expected = IllegalArgumentException.class)
    public void getSpaceTableShouldFail() {
        manager.getTable(" ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNullNameTableShouldFail() {
        manager.getTable(null);
    }

    @Test
    public void getExistingTableShouldRef() {
        assertNotNull("getTable не обнаружил существующую таблицу", manager.getTable(existingFile.getName()));
    }

    @Test
    public void doubleGetTableEquals() {
        assertEquals("Дважды вызванный с тем же аргументом getTable возвращает разные объекты",
                manager.getTable(existingFile.getName()), manager.getTable(existingFile.getName()));
    }


    @Test
    public void getExistingTableImplTable() {
        boolean flag = false;
        Class[] inter = manager.getTable(existingFile.getName()).getClass().getInterfaces();
        for (Class i:inter) {
            if (i.equals(ru.fizteh.fivt.storage.strings.Table.class)) {
                flag = true;
            }
        }
        assertTrue("Полученный объект не поддерживает интерфейс Table", flag);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeTableByNullStringShouldFail() {
        manager.removeTable(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeTableNlShouldFail() {
        manager.removeTable("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeTableBadCharInNameShouldFail1() {
        manager.removeTable("a/b");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeTableBadCharInNameShouldFail2() {
        manager.removeTable("a\\b");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeTableBadCharInNameShouldFail3() {
        manager.removeTable("..");
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeTableBadCharInNameShouldFail4() {
        manager.removeTable(".");
    }


    @Test(expected = IllegalStateException.class)
    public void removeNonexistentTableShouldFail() {
        manager.removeTable("nonexistent");
    }

    @AfterClass
    public static void afterAll() {
        String name = workingDirFile.toPath().toString();
        removeFile(name);
    }
}

