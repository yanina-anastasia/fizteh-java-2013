/**
 * Task.java
 * Caclulator
 *
 * Created by Vladimir Mishatkin on 9/20/13
 */

package ru.fizteh.fivt.students.mishatkin.calculator;

import java.io.PrintWriter;

class Task {
    private InputSource in;
    private PrintWriter out;

    Task(InputSource in, PrintWriter out) {
        this.in = in;
        this.out = out;
    }

    private void printInNumericSystem(int decimalResult, int base) {
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

    public int solve() {
        ReversePolishNoteEncoder encoder = new ReversePolishNoteEncoder();
        boolean isValidInput = true;
        int calculationResultDecimal = 0;
        while (isValidInput && in.hasNextLine()) {
            try {
                calculationResultDecimal = encoder.calculate(in.nextLine());
            } catch (Exception e) {
                System.err.println(e.getMessage());
                isValidInput = false;
            }
        }
        if (isValidInput) {
            printInNumericSystem(calculationResultDecimal, Calculator.MyBase);
        } else {
            return 1;
        }
        return 0;
    }

}