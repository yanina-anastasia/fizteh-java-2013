/**
 * Calculator.java
 * Calculator
 *
 * Created by Vladimir Mishatkin on 9/14/13
 */
package ru.fizteh.fivt.students.Mishatkin.Vladimir;

import java.io.*;
import java.lang.*;
import java.util.*;

import static java.lang.String.valueOf;

public class Calculator
{
    public static int MyBase = 17;
    private static boolean shouldUseArgsInsteadOfSTDIN = true;

    public static void main(String[] args)
    {
        InputStream inputStream = System.in;
        OutputStream outputStream = System.out;
        PrintWriter out = new PrintWriter(outputStream);
        InputSource in = shouldUseArgsInsteadOfSTDIN ? new ArgumentsSource(args) :
                         new StandartInputSource(new Scanner(inputStream));
        Task solver = new Task(in, out);
        solver.solve();
        out.close();
    }
}

class Task
{
    private InputSource in;
    private PrintWriter out;

    Task(InputSource _in, PrintWriter _out)
    {
        in = _in;
        out =_out;
    }

    private void printInNumericSystem(int decimalResult, int base)
    {
        boolean negative = (decimalResult < 0);
        decimalResult = Math.abs(decimalResult);
        String toPrint = "";
        while (decimalResult > 0) {
            int radixValue = decimalResult % base;
            char charValue = (char)((radixValue >= 10) ? ('A' + radixValue - 10) : '0' + radixValue);
            toPrint = charValue + toPrint;
            decimalResult /= base;
        }
        if (toPrint.length() == 0) {
            toPrint = "0";
        }
        if (negative) {
            toPrint = "-" + toPrint;
        }
        out.println(toPrint);
    }

    public void solve()
    {
        ReversePolishNoteEncoder encoder = new ReversePolishNoteEncoder();
        boolean isValidInput = true;
        int calculationResultDecimal = 0;
        while (isValidInput && in.hasNextLine()) {
            try {
                calculationResultDecimal = encoder.calculate(in.nextLine());
            } catch (Exception e) {
                isValidInput = false;
            }
        }
        if (isValidInput) {
            printInNumericSystem(calculationResultDecimal, Calculator.MyBase);
        }
    }

}