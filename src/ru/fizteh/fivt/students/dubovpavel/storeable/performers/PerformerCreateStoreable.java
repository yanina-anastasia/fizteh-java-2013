package ru.fizteh.fivt.students.dubovpavel.storeable.performers;

import ru.fizteh.fivt.students.dubovpavel.executor.Command;
import ru.fizteh.fivt.students.dubovpavel.executor.Dispatcher;
import ru.fizteh.fivt.students.dubovpavel.executor.PerformerException;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.StorageAccessible;
import ru.fizteh.fivt.students.dubovpavel.multifilehashmap.performers.PerformerCreate;
import ru.fizteh.fivt.students.dubovpavel.storeable.TableStoreableBuilder;
import ru.fizteh.fivt.students.dubovpavel.storeable.TypeNamesMatcher;

import java.util.ArrayList;

public class PerformerCreateStoreable<D extends Dispatcher & StorageAccessible> extends PerformerCreate<D> {
    private TableStoreableBuilder builder;

    public PerformerCreateStoreable(TableStoreableBuilder dataBaseBuilder) {
        builder = dataBaseBuilder;
    }

    private void testType(ArrayList<Class<?>> fields, String type) throws PerformerException {
        Class<?> caster = TypeNamesMatcher.classByName.get(type);
        if(caster == null) {
            throw new PerformerException(String.format("Type %s is not supported", type));
        } else {
            fields.add(caster);
        }
    }

    @Override
    public void execute(D dispatcher, Command command) throws PerformerException {
        ArrayList<Class<?>> fields = new ArrayList<>();
        testType(fields, command.getArgument(1).substring(1));
        for(int i = 2; i < command.argumentsCount() - 1; i++) {
            testType(fields, command.getArgument(i));
        }
        String lastChunk = command.getArgument(command.argumentsCount() - 1);
        testType(fields, lastChunk.substring(0, lastChunk.length() - 1));
        builder.setFields(fields);
        dispatcher.getStorage().create(command.getArgument(0));
    }
}
