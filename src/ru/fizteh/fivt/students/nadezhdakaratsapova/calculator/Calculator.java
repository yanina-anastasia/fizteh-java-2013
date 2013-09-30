package ru.fizteh.fivt.students.nadezhdakaratsapova.calculator;

import java.util.Stack;
import java.lang.Character;
import java.io.IOException;

public class Calculator {

    public static int priority (final char token) {
        switch (token) {
            case '*':
            case '/':
                return 2;

            case '+':
            case '-':
                return 1;

        }
        return 0;
    }

    public static Integer calculate(final Integer arg1, final Integer arg2, final char operation) throws IOException {
        switch (operation) {
            case '*':
                return arg1 * arg2;
            case '/':
                if (arg2 == 0) throw new IOException("Devision by zero");
                return arg1/arg2;
            case '+':
                return arg1 + arg2;
            case '-':
                return arg1 - arg2;
        }
        return 0;
    }


    public static void main (String args[]) {
        try {
            StringBuilder AlgExpression = new StringBuilder(args[0]);
            for (int i = 1; i < args.length; ++i) {
                AlgExpression.append(' ');
                AlgExpression.append(args[i]);
            }
            System.out.println(AlgExpression);
            String Task = new String(AlgExpression);
            int Radix = 17;
            StringBuilder OutputString = new StringBuilder();
            Stack<Character> DataStack = new Stack<Character>();
            int IndexBegin;
            int IndexEnd;
            int i = 0;
            boolean flagOpen = false;//знака минус после скобки нет (для обработки отрицательных чисел)
            boolean flagClose = false;
            int countOfBrackets = 0;
            int PrevToken = 1;       //1 - арифметичесая операция;
            //2 - число;
            // 3 - скобка;

            while (i < Task.length()) {
                IndexBegin = i;

                while ((i < Task.length()) && ((Character.isDigit(Task.charAt(i))) | ((Task.charAt(i) >= 'A') && (Task.charAt(i) <= 'G') ) ) ) {
                    ++i;
                }
                if (IndexBegin != i) {
                    if (PrevToken == 2){
                        throw new IOException("numbers without operation");
                    } else {
                        IndexEnd = i;
                        if (!flagClose) {
                            OutputString.append(Task.substring(IndexBegin, IndexEnd));
                            OutputString.append(' ');
                            --i;
                        } else {
                            throw new IOException("have to be in brackets");
                        }
                        if (flagOpen) {
                            flagClose = true;
                        }
                        PrevToken = 2;
                    }
                } else {
                    if (( Task.charAt(i) == '(')) {
                        if (PrevToken != 2) {
                            flagClose = false;
                            if (((i + 2) != Task.length()) && (Task.charAt(i + 1) == '-')) {
                                if (Task.charAt(i + 2) == '(') {
                                    DataStack.push('*');
                                    OutputString.append("-1");
                                    OutputString.append(' ');
                                }   else {
                                    OutputString.append('-');
                                    DataStack.push(Task.charAt(i));
                                    ++i;
                                    flagOpen = true;
                                }
                            } else {
                                DataStack.push(Task.charAt(i));
                            }
                        }  else {
                            throw new IOException("operation was missed");
                        }
                        PrevToken = 3;
                    } else {
                        if (Task.charAt(i) == ')') {
                            if (PrevToken != 1) {
                                char Top;
                                if (flagOpen && !flagClose) {
                                    throw new IOException("not correct input");
                                }
                                flagClose = false;
                                flagOpen = false;
                                while ( (!(DataStack.empty())) && ( (Top = DataStack.peek()) != '(') ) {
                                    OutputString.append(Top);
                                    OutputString.append(' ');
                                    DataStack.pop();
                                }
                                if (!DataStack.empty())  {
                                    DataStack.pop();
                                } else {
                                    throw new IOException("not enough brackets1");
                                }
                            } else {
                                throw new IOException("not correct expression");
                            }
                            PrevToken = 3;
                        } else {
                            int pr = priority(Task.charAt(i));
                            if (pr > 0) {
                                if (PrevToken != 1) {
                                    while ((!DataStack.empty()) && (pr <= (priority(DataStack.peek())))) {
                                        OutputString.append(DataStack.pop());
                                        OutputString.append(' ');
                                    }
                                    DataStack.push(Task.charAt(i));
                                } else {
                                    throw new IOException("not correct expression");
                                }
                                PrevToken = 1;
                            } else {
                                if ((Task.charAt(i) != ' ') && (Task.charAt(i) != '\t')) {
                                    throw new IOException("Undefined symbol");
                                }
                            }
                        }
                    }

                }
                ++i;
            }

            while (!DataStack.empty()) {
                char Top = DataStack.peek();
                if ((Top == '(') | (Top == ')')) {
                    throw new IOException("not enough brackets2");
                } else {
                    OutputString.append(DataStack.pop());
                    OutputString.append(' ');
                }

            }

            Stack<Integer> Result = new Stack<Integer>();
            i = 0;

            while (i < (OutputString.length() - 1)) {
                if (OutputString.charAt(i + 1) == ' ') {
                    if (Character.isDigit(OutputString.charAt(i))) {
                        Result.push(Integer.parseInt(OutputString.substring(i, i + 1), Radix));
                    } else {
                        if (!Result.empty()) {
                            Integer arg2 = Result.pop();
                            Integer arg1;
                            if (!Result.empty()) {
                                arg1 = Result.pop();
                            } else {
                                throw new IOException("mistake in expression1");
                            }
                            Result.push(calculate(arg1, arg2, OutputString.charAt(i)));
                        } else {
                            throw new IOException("mistake in expression2");
                        }
                    }
                    ++i;
                } else {
                    IndexBegin = i;
                    while (OutputString.charAt(i) != ' ') {
                        ++i;
                    }
                    IndexEnd = i;
                    Result.push(Integer.parseInt(OutputString.substring(IndexBegin, IndexEnd), Radix));
                }
                ++i;
            }
            int res = Result.pop();
            if (Result.empty()) {
                System.out.println(Integer.toString(res, Radix));
            } else {
                throw new IOException("mistake in expression");
            }
        }
        catch(IOException e) {
            System.err.println("Exception was caught: " + e.getMessage());
            int i = 2;
            System.exit(i);
        }
    }
}

