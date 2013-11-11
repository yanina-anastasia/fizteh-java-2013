package ru.fizteh.fivt.students.msandrikova.storeable;

import java.util.ArrayList;
import java.util.List;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

public class TableRow implements Storeable {
	private List<Class<?>> columnTypes = new ArrayList<Class<?>>();
	private List<Object> row = new ArrayList<Object>();
	
	public TableRow(List<Class<?>> columnTypes) {
		this.columnTypes = columnTypes;
	}

	@Override
	public void setColumnAt(int columnIndex, Object value) throws ColumnFormatException, IndexOutOfBoundsException {
		if(columnIndex < 0 || columnIndex >= columnTypes.size()) {
			throw new IndexOutOfBoundsException("Column index can not be less then 0 and more then types amount.");
		}
		if(value != null && !value.getClass().equals(this.columnTypes.get(columnIndex))) {
			throw new ColumnFormatException("Value's class must be equal to column type");
		}
		row.set(columnIndex, value);
	}

	@Override
	public Object getColumnAt(int columnIndex) throws IndexOutOfBoundsException {
		if(columnIndex < 0 || columnIndex >= columnTypes.size()) {
			throw new IndexOutOfBoundsException("Column index can not be less then 0 and more then types amount.");
		}
		return row.get(columnIndex);
	}
	
	private Object getClassAt(int columnIndex, Class<?> classType) throws ColumnFormatException, IndexOutOfBoundsException  {
		if(columnIndex < 0 || columnIndex >= columnTypes.size()) {
			throw new IndexOutOfBoundsException("Column index can not be less then 0 and more then types amount.");
		}
		if(classType.equals(this.columnTypes.get(columnIndex))) {
			throw new ColumnFormatException("Requested class must be equal to column type");
		}
		return row.get(columnIndex);
	}

	@Override
	public Integer getIntAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
		return (Integer) this.getClassAt(columnIndex, Integer.class);
	}

	@Override
	public Long getLongAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
		return (Long) this.getClassAt(columnIndex, Long.class);
	}

	@Override
	public Byte getByteAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
		return (Byte) this.getClassAt(columnIndex, Byte.class);
	}

	@Override
	public Float getFloatAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
		return (Float) this.getClassAt(columnIndex, Float.class);
	}

	@Override
	public Double getDoubleAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
		return (Double) this.getClassAt(columnIndex, Double.class);
	}

	@Override
	public Boolean getBooleanAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
		return (Boolean) this.getClassAt(columnIndex, Boolean.class);
	}

	@Override
	public String getStringAt(int columnIndex) throws ColumnFormatException, IndexOutOfBoundsException {
		return (String) this.getClassAt(columnIndex, String.class);
	}

}
