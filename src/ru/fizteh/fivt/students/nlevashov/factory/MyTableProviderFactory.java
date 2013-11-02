package ru.fizteh.fivt.students.nlevashov.factory;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.nio.file.Paths;

public class MyTableProviderFactory implements TableProviderFactory {

    //Path rootPath;

    /**
     * Конструктор. На данном этапе эволюционирования страдает фигней.
     * Предполагается, наверное, что должен составлять список баз данных.
     *
     * @param root Адрес базы данных.
     * @throws IllegalArgumentException Если адрес null или имеет недопустимое значение.
     */
/*
    public MyTableProviderFactory(Path root) {
        if (root == null) {
            throw new IllegalArgumentException("TableProviderFactory.constructor: root is null");
        }
        rootPath = root;
    }
  */
    public MyTableProviderFactory() {

    }

    /**
     * Возвращает объект для работы с базой данных.
     *
     * @param dir Директория с файлами базы данных.
     * @return Объект для работы с базой данных.
     * @throws IllegalArgumentException Если значение директории null или имеет недопустимое значение.
     */

    /*
    @Override
    public TableProvider create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException("TableProviderFactory.create: dir is null");
        }
        return (new MyTableProvider(rootPath.resolve(dir).normalize()));
    } */

    @Override
    public TableProvider create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException("TableProviderFactory.create: dir is null");
        }
        return (new MyTableProvider(Paths.get(dir).normalize()));
    }
}
