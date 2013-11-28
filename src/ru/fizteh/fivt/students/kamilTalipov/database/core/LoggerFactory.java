package ru.fizteh.fivt.students.kamilTalipov.database.core;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import java.io.Writer;

public class LoggerFactory implements LoggingProxyFactory {
    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
