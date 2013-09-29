public class Main {
    public static void main(String[] args) {
        try {
            // Writing numbers as 1 000 000 is allowed for user convenience.
            StringBuilder concatenator = new StringBuilder();
            for(String arg: args) {
                concatenator.append(arg);
            }
            Calculator ObjCalculator = new Calculator(concatenator.toString().replaceAll("\\s", "").toUpperCase());
            //Note that result is presented in radix of 10.
            System.out.println(ObjCalculator.calculate());
        } catch(Calculator.InappropriateSymbolException e) {
            System.err.println(e.getMessage());
        } catch(Calculator.InvalidLexemMetException e) {
            System.err.println(e.getMessage());
        } catch(ArithmeticException e) {
            System.err.println(e.getMessage());
        } catch(NumberFormatException e) {
            System.err.println(e.getMessage());
        }
    }
}
