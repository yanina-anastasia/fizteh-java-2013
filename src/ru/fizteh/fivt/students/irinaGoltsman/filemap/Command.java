package ru.fizteh.fivt.students.irinaGoltsman.filemap;

public interface Command {
    //Получить название команды
    public String getName();

    //Получить количество аргументов команды.
    public int getCountOfArguments();

    //Проверить аргументы.
    public boolean check(String[] parts);

    //Выполнить команду.
    public Code perform(String[] parts);

}
