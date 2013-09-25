package ru.fizteh.fivt.students.eltyshev.calc;

public class Program
{
    public static void main(String[] Args)
    {
        StringBuilder sb = new StringBuilder();
        for(String arg: Args)
        {
            sb.append(arg);
        }
        try
        {
            double result = ExpressionSolver.Execute(sb.toString());
            System.out.print(result);
        }
        catch(IllegalArgumentException e)
        {
            System.out.println("String input error!");
        }
        catch(InvalidCharacterException e)
        {
            System.out.println("Invalid character: " + e.getExpression());
        }
        catch(BadBracketBalanceException e)
        {
            System.out.println("Check your brackets!");
        }

    }
}
