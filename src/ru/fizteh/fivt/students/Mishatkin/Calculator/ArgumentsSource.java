/**
 * ArgumentsSource.java
 * Calculator
 *
 * Created by Vladimir Mishatkin on 9/17/13
 */

package ru.fizteh.fivt.students.Mishatkin.Calculator;

public class ArgumentsSource implements InputSource {

    private int nextArgumentIndex = 0;
    private String[] args;

    public ArgumentsSource(String[] _args) {
        args = _args;
    }

    @Override
    public boolean hasNextLine() {
        return (nextArgumentIndex < args.length);
    }

    @Override
    public String nextLine() {
        return args[nextArgumentIndex++];
    }
}
