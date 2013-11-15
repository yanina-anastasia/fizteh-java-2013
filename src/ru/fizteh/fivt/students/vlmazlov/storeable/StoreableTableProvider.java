package ru.fizteh.fivt.students.vlmazlov.storeable;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.text.ParseException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import javax.activation.UnsupportedDataTypeException;
import javax.xml.stream.XMLStreamException;
import ru.fizteh.fivt.students.vlmazlov.shell.QuietCloser;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.students.vlmazlov.filemap.GenericTable;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.ProviderReader;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.ProviderWriter;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.GenericTableProvider;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.ValidityCheckFailedException;
import ru.fizteh.fivt.students.vlmazlov.multifilemap.ValidityChecker;

public class StoreableTableProvider extends GenericTableProvider<Storeable, StoreableTable> implements TableProvider {

	public StoreableTableProvider(String name, boolean autoCommit) throws ValidityCheckFailedException {
		super(name, autoCommit);
	}

    @Override
    protected StoreableTable instantiateTable(String name, Object args[]) {
        return new StoreableTable(name, autoCommit, (List)args[0]);
    }

    public StoreableTable createTable(String name, List<Class<?>> columnTypes) throws IOException {
        if ((columnTypes == null) || (columnTypes.isEmpty())) {
            throw new IllegalArgumentException("wrong type (column types not specified)");
        }

        try {
            for (Class<?> type : columnTypes) {
                ValidityChecker.checkColumnType(type);
            }
        } catch (ValidityCheckFailedException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }

        StoreableTable table = super.createTable(name, new Object[]{columnTypes});

        File tableDir = new File(getRoot(), name);
        File signatureFile = new File(tableDir, "signature.tsv");

        signatureFile.createNewFile();

        PrintWriter writer = new PrintWriter(signatureFile);

        try {
            for (Class<?> clazz : columnTypes) {
                writer.print(TypeName.getNameByClass(clazz) + " ");
            }
        } finally {
            QuietCloser.closeQuietly(writer);  
        }

        return table;
    }

   @Override
	public Storeable deserialize(StoreableTable table, String value) throws ParseException {
		return this.deserialize((Table)table, value);
	}

    @Override
    public String serialize(StoreableTable table, Storeable value) throws ColumnFormatException {
    	return this.serialize((Table)table, value);
    }

    @Override
    public Storeable deserialize(Table table, String value) throws ParseException {
        List<Object> values = new ArrayList<Object>();

        try {
            XMLStoreableReader reader = new XMLStoreableReader(value);
            for (int i = 0; i < table.getColumnsCount();++i) {
                values.add(reader.readColumn(table.getColumnType(i)));
            }


        } catch (XMLStreamException ex) {
            throw new ParseException(ex.getMessage(), 0);
        }

        return createFor(table, values);
    }

    @Override
    public String serialize(Table table, Storeable value) throws ColumnFormatException {
        try {
            ValidityChecker.checkValueFormat(table, value);
        } catch (ValidityCheckFailedException ex) {
            throw new ColumnFormatException(ex.getMessage());
        }

        try {
            return XMLStoreableWriter.serialize(value);
        } catch (XMLStreamException ex) {
            throw new RuntimeException("Unable to write XML: " + ex.getMessage());
        }
    }

    @Override
    public Storeable createFor(Table table) {
    	List<Class<?>> valueTypes = new ArrayList<Class<?>>(table.getColumnsCount());
    	
    	for (int i = 0;i < table.getColumnsCount();++i) {
    		valueTypes.add(table.getColumnType(i));
    	}

    	return new TableRow(valueTypes);
    }

    @Override 
    public Storeable createFor(Table table, List<?> values) throws ColumnFormatException, IndexOutOfBoundsException {
    	if (values.size() > table.getColumnsCount()) {
    		throw new IndexOutOfBoundsException("Too many columns passed");
    	}

    	if (values.size() < table.getColumnsCount()) {
    		throw new IndexOutOfBoundsException("Too few columns passed");
    	} 

    	Storeable result = createFor(table);

    	for (int i = 0;i < values.size();++i) {
    		result.setColumnAt(i, values.get(i));
    	}

        return result;
    }

    private List<Class<?>> getTableSignature(File tableDir) 
    throws ValidityCheckFailedException, IOException {
        ValidityChecker.checkMultiStoreableTableRoot(tableDir);

        File signatureFile = new File(tableDir, "signature.tsv");
        List<Class<?>> signature = new ArrayList<Class<?>>();
        Scanner scanner = new Scanner(signatureFile);

        try {
            while (scanner.hasNext()) {
                String type = scanner.next();
                Class<?> columnType = TypeName.getClassByName(type.trim());

                if (columnType == null) {
                    throw new UnsupportedDataTypeException("Unsupported column type: " + type);
                }

                signature.add(columnType);
            }

            ValidityChecker.checkStoreableTableSignature(signature);

            //В итоге заменил на другой, см. ProviderReadre.readMultiTable
            ///Костыль!!!!!!!!!!!!!!
            /*if (signatureFile.delete()) {
                System.out.println("Waste");
            } */   
            ///Костыль!!!!!!!!!!!!!!
            return signature;
        } finally {
            scanner.close();
        }  

    }

    @Override
    public void read() throws IOException, ValidityCheckFailedException {
        for (File file : ProviderReader.getTableDirList(this)) {
            StoreableTable table = createTable(file.getName(), getTableSignature(file));
            ProviderReader.readMultiTable(file, table, this);
            //read data has to be preserved
            table.commit();
        }
    }

    @Override
    public void write() throws IOException, ValidityCheckFailedException {
      for (Map.Entry<StoreableTable, File> entry : ProviderWriter.getTableDirMap(this).entrySet()) {

        ValidityChecker.checkMultiStoreableTableRoot(entry.getValue());

        ProviderWriter.writeMultiTable(entry.getKey(), entry.getValue(), this);
      }
    }
}