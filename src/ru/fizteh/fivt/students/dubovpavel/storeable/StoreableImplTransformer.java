package ru.fizteh.fivt.students.dubovpavel.storeable;

import org.json.JSONArray;
import org.json.JSONException;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.dubovpavel.strings.ObjectTransformer;

import java.text.ParseException;
import java.util.ArrayList;

public class StoreableImplTransformer implements ObjectTransformer<Storeable> {
    private ArrayList<Class<?>> fields;
    public StoreableImplTransformer(ArrayList<Class<?>> types) {
        fields = types;
    }

    public String serialize(Storeable obj) throws ColumnFormatException {
        JSONArray json = new JSONArray();
        for(int i = 0; i < fields.size(); i++) {
            Object value = obj.getColumnAt(i);
            try {
                json.put(fields.get(i).getMethod("valueOf", new Class[] {String.class}).invoke(null, value.toString()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return json.toString();
    }

    public Storeable deserialize(String obj) throws SerialException, ParseException {
        try {
            JSONArray json = new JSONArray(obj);
            StoreableImpl storeable = new StoreableImpl(fields);
            if(json.length() != fields.size()) {
                throw new ParseException("JSON length and fields size mismatch", -1);
            }
            for(int i = 0; i < fields.size(); i++) {
                storeable.setColumnAt(i, json.get(i));
            }
            return storeable;
        } catch (JSONException e) {
            throw new ParseException(e.getMessage(), -1);
        } catch (ColumnFormatException e) {
            throw new SerialException(e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            throw new SerialException(e.getMessage());
        }
    }

    public Storeable copy(Storeable obj) { // obj must be checked already
        StoreableImpl newObj = new StoreableImpl((ArrayList<Class<?>>)fields.clone());
        for(int i = 0; i < fields.size(); i++) {
            newObj.setColumnAt(i, obj.getColumnAt(i));
        }
        return newObj;
    }
}
