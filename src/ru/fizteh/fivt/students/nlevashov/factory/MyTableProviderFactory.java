package ru.fizteh.fivt.students.nlevashov.factory;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.nio.file.Paths;

public class MyTableProviderFactory implements TableProviderFactory {

    /**
     * Возвращает объект для работы с базой данных.
     *
     * @param dir Директория с файлами базы данных.
     * @return Объект для работы с базой данных.
     * @throws IllegalArgumentException Если значение директории null или имеет недопустимое значение.
     */
    @Override
    public TableProvider create(String dir) {
        if ((dir == null) || dir.trim().isEmpty()) {
            throw new IllegalArgumentException("TableProviderFactory.create: dir is null");
        }
        return (new MyTableProvider(Paths.get(dir).normalize()));
    }
}
