/**
 * StandardInputSource.java
 * Calculator
 *
 * Created by Vladimir Mishatkin on 9/17/13
 */

package ru.fizteh.fivt.students.Mishatkin.Vladimir;

import java.util.Scanner;

public class StandartInputSource implements InputSource {
    private Scanner in;

    StandartInputSource(Scanner _in) {
        in = _in;
    }

    @Override
    public boolean hasNextLine() {
        return in.hasNextLine();
    }

    @Override
    public String nextLine() {
        return in.nextLine();
    }
}
