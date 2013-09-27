public class Main {
    public static void main(String[] args) {
        try {
            // Writing numbers as 1 000 000 is allowed for user convenience.
            Calculator ObjCalculator = new Calculator(StringOperations.concatenate(args).replaceAll("\\s", "").toUpperCase());
            //Note that result is presented in radix of 10.
            System.out.println(ObjCalculator.calculate().toString());
        } catch(InappropriateSymbolException e) {
            System.out.println(e.getMessage());
        } catch(InvalidLexemMetException e) {
            System.out.println(e.getMessage());
        } catch (DivisionByZeroException e) {
            System.out.println(e.getMessage());
        }
    }
}
