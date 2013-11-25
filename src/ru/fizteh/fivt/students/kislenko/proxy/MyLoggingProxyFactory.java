package ru.fizteh.fivt.students.kislenko.proxy;

import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import java.io.Writer;

public class MyLoggingProxyFactory implements LoggingProxyFactory {
    /**
     * Создаёт класс-обёртку вокруг объекта <code>implementation</code>, которая при вызове
     * методов интерфейса <code>interfaceClass</code> выполняет логирование аргументов и результата
     * вызова методов.
     * <p/>
     * Класс-обёртка не имеет права выбрасывать свои исключения, но обязана выбрасывать те же самые
     * исключения, что выбрасывает оригинальный класс.
     * <p/>
     * Класс-обёртка должен быть потокобезопасным.
     *
     * @param writer         Объект, в который ведётся запись лога.
     * @param implementation Объект, реализующий интерфейс <code>interfaceClass</code>.
     * @param interfaceClass Класс интерфейса, методы которого должны выполнять запись в лог.
     * @return Объект, реализующий интерфейс <code>interfaceClass</code>, при вызове методов которого
     *         выполняется запись в лог.
     * @throws IllegalArgumentException Если любой из переданных аргументов null или имеет некорректное значение.
     */
    @Override
    public Object wrap(Writer writer, Object implementation, Class<?> interfaceClass) {
        return null;
    }
}
