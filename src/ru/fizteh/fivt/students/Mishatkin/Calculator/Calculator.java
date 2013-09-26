/**
 * calculator.java
 * calculator
 *
 * Created by Vladimir Mishatkin on 9/14/13
 */

package ru.fizteh.fivt.students.mishatkin.calculator;

import java.io.*;
import java.lang.*;
import java.util.*;

import static java.lang.String.valueOf;

public class Calculator {
    public static int MyBase = 19;
    private static boolean shouldUseArgsInsteadOfSTDIN = true;

    public static void main(String[] args) {
        InputStream inputStream = System.in;
        OutputStream outputStream = System.out;
        PrintWriter out = new PrintWriter(outputStream);
        InputSource in = shouldUseArgsInsteadOfSTDIN ? new ArgumentsSource(args) :
                         new StandartInputSource(new Scanner(inputStream));
        Task solver = new Task(in, out);
        try {
            solver.solve();

            } catch (Exception e) {
                System.err.println(e.getMessage());
        }
        out.close();
    }
}