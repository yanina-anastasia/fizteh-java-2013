package ru.fizteh.fivt.students.nlevashov.factory;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.nio.file.Paths;
import java.io.IOException;

/**
 * Представляет интерфейс для создание экземпляров {@link ru.fizteh.fivt.storage.structured.TableProvider}.
 *
 * Предполагается, что реализация интерфейса фабрики будет иметь публичный конструктор без параметров.
 */
public class MyTableProviderFactory implements TableProviderFactory {

    /**
     * Возвращает объект для работы с базой данных.
     *
     * @param path Директория с файлами базы данных.
     * @return Объект для работы с базой данных, который будет работать в указанной директории.
     *
     * @throws IllegalArgumentException Если значение директории null или имеет недопустимое значение.
     * @throws java.io.IOException В случае ошибок ввода/вывода.
     */
    @Override
    public TableProvider create(String path) throws IOException {
        if ((path == null) || path.trim().isEmpty()) {
            throw new IllegalArgumentException("TableProviderFactory.create: dir is null");
        }
        return (new MyTableProvider(Paths.get(System.getProperty("user.dir")).resolve(path).normalize()));
    }
}
