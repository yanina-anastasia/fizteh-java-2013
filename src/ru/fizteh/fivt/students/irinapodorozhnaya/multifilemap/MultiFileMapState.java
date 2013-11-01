package ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import ru.fizteh.fivt.students.irinapodorozhnaya.db.DbState;
import ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.commands.CommandCommit;
import ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.commands.CommandCreate;
import ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.commands.CommandDrop;
import ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.commands.CommandRollBack;
import ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.commands.CommandSize;
import ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.commands.CommandUse;
import ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.extend.ExtendProvider;
import ru.fizteh.fivt.students.irinapodorozhnaya.multifilemap.extend.ExtendTable;

public class MultiFileMapState extends DbState {
    
    private ExtendTable workingTable;
    private final ExtendProvider provider;
    
    MultiFileMapState(InputStream in, PrintStream out) throws IOException {
        super(in, out);
        String path = System.getProperty("fizteh.db.dir");
        if (path == null) {
            throw new IOException("can't get property");
        }
        provider = new MyTableProviderFactory().create(path);
        add(new CommandUse(this));
        add(new CommandCreate(this));
        add(new CommandDrop(this));
        add(new CommandCommit(this));
        add(new CommandSize(this));
        add(new CommandRollBack(this));
    }

    @Override
    protected void open() throws IOException {        
    }
    
    @Override
    public String getValue(String key) throws IOException {
        if (workingTable == null) {
            throw new IOException("no table");
        }
        return workingTable.get(key);
    }

    @Override
    public String removeValue(String key) throws IOException {
        if (workingTable == null) {
            throw new IOException("no table");
        }
        return workingTable.remove(key);
    }

    @Override
    public String put(String key, String value) throws IOException {
        if (workingTable == null) {
            throw new IOException("no table");
        }
        return workingTable.put(key, value);
    }
    
    @Override
    public int commitDif() throws IOException {
        if (workingTable != null) {
            return workingTable.commit();
        }
        return 0;
    }

    public int getCurrentTableSize() {
        return workingTable.size();
    }

    public int rollBack() {
        return workingTable.rollback();
    }
    
    public void drop(String name) {
        provider.removeTable(name);
    }

    public ExtendTable create(String name) throws IOException {
        ExtendTable table = provider.createTable(name);
        if (table == null) {
        throw new IOException(name + " exists");
        }
        return table;
    }
    
    public void use(String name) throws IOException {
        ExtendTable table = provider.getTable(name);
        if (table == null) {
        throw new IOException(name + " not exists");
        }
        if (this.workingTable != null) {
            int n = this.workingTable.getChangedValuesNumber();
            if (n != 0) {
                throw new IOException(n + " unsaved changed");
            }
            }
        this.workingTable = table;
    }
}
