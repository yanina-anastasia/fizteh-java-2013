package ru.fizteh.fivt.students.dubovpavel.storeable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.dubovpavel.strings.ObjectTransformer;

import java.util.ArrayList;

public class StoreableImplTransformer implements ObjectTransformer<Storeable> {
    private ArrayList<Class<?>> fields;
    public StoreableImplTransformer(ArrayList<Class<?>> types) {
        fields = types;
    }

    public String serialize(Storeable obj) {
        return null;
    }

    public Storeable deserialize(String obj) {
        return null;
    }

    public Storeable copy(Storeable obj) { // obj must be checked already
        StoreableImpl newObj = new StoreableImpl((ArrayList<Class<?>>)fields.clone());
        for(int i = 0; i < fields.size(); i++) {
            newObj.setColumnAt(i, obj.getColumnAt(i));
        }
        return newObj;
    }
}
