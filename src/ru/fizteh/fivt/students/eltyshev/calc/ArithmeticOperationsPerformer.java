package ru.fizteh.fivt.students.eltyshev.calc;

public class ArithmeticOperationsPerformer {
    public static int add(int arg1, int arg2) {
        long result = arg1 + arg2;
        check_result(result);
        return (int) result;
    }

    public static int sub(int arg1, int arg2) {
        long result = arg1 - arg2;
        check_result(result);
        return (int) result;
    }

    public static int mul(int arg1, int arg2) {
        long result = arg1 * arg2;
        check_result(result);
        return (int) result;
    }

    public static int div(int arg1, int arg2) {
        if (arg2 == 0) {
            throw new ArithmeticException("Division by zero!");
        }
        long result = arg1 / arg2;
        check_result(result);
        return (int) result;
    }

    public static int pow(int arg1, int arg2)
    {
        long result = (long)pow(arg1, arg2);
        check_result(result);
        return (int) result;
    }

    private static void check_result(long result) {
        if (result > Integer.MAX_VALUE) {
            throw new ArithmeticException("Integer overflow!");
        }
        if (result < Integer.MIN_VALUE) {
            throw new ArithmeticException("Integer underflow!");
        }
    }
}
