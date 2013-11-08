package ru.fizteh.fivt.students.visamsonov.storage;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import java.io.IOException;

public interface TableProviderFactoryInterface extends TableProviderFactory {

	TableProviderInterface create (String dir) throws IOException;
}