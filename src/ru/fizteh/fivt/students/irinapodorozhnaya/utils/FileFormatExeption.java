package ru.fizteh.fivt.students.irinapodorozhnaya.utils;

public class FileFormatExeption extends RuntimeException {
    public FileFormatExeption() {
    }

    public FileFormatExeption(String message) {
        super(message);
    }

    public FileFormatExeption(String message, Throwable cause) {
        super(message, cause);
    }

    public FileFormatExeption(Throwable cause) {
        super(cause);
    }
}
