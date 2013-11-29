package ru.fizteh.fivt.students.kamilTalipov.database.core;

import ru.fizteh.fivt.storage.structured.TableProviderFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class MultiFileHashTableFactory implements TableProviderFactory, AutoCloseable {
    private final ArrayList<Provider> providers;

    private volatile boolean isClosed = false;

    public MultiFileHashTableFactory() {
        providers = new ArrayList<>();
    }

    @Override
    public MultiFileHashTableProvider create(String dir) throws IllegalArgumentException, IOException {
        checkState();
        if (dir == null) {
            throw new IllegalArgumentException("Directory path must be not null");
        }

        try {
            synchronized (providers) {
                MultiFileHashTableProvider resultProvider = null;
                for (Provider provider : providers) {
                    if (provider.dir.equals(dir)) {
                        resultProvider = provider.provider;
                        break;
                    }
                }
                if (resultProvider != null) {
                    return resultProvider;
                }

                providers.add(new Provider(dir, new MultiFileHashTableProvider(dir)));
                return providers.get(providers.size() - 1).provider;
            }
        } catch (DatabaseException e) {
            throw new IllegalArgumentException("Database error", e);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File not found", e);
        }
    }

    @Override
    public void close() {
        if (isClosed) {
            return;
        }

        synchronized (providers) {
            isClosed = true;
            for (Provider provider : providers) {
                provider.provider.close();
            }
        }
    }

    private void checkState() throws IllegalStateException {
        if (isClosed) {
            throw new IllegalStateException("Factory is closed");
        }
    }

    private class Provider {
        public final String dir;
        public final MultiFileHashTableProvider provider;

        public Provider(String dir, MultiFileHashTableProvider provider) {
            this.dir = dir;
            this.provider = provider;
        }
    }
}
