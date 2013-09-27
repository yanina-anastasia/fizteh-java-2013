import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

class Constants {
    public static final int PRECISION = 8;
    public static final int RADIX = 19;
    public static final String BUG = "Program contains a bug.";
}

class Pair<L, R> {
    private L l;
    private R r;
    Pair(L l, R r) {
        this.l = l;
        this.r = r;
    }
    public L getLeft() {
        return this.l;
    }
    public R getRight() {
        return this.r;
    }
}

class StringOperations {
    public static String concatenate(String[] data) {
        String result = "";
        for(int i = 0; i < data.length; i++) {
            result += data[i];
        }
        return result;
    }
    public static String removeWhitespaces(String data) {
        return data.replaceAll("\\s", "");
    }
}

class Fraction {
    private BigInteger numerator, denominator;
    Fraction(BigInteger number) {
        this.numerator = number;
        this.denominator = new BigInteger("1");
    }

    Fraction(BigInteger numerator, BigInteger denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public Fraction add(Fraction other) {
        BigInteger rNumerator, rDenominator;
        rNumerator = this.numerator.multiply(other.denominator).add(other.numerator.multiply(this.denominator));
        rDenominator = this.denominator.multiply(other.denominator);
        BigInteger gcd = rNumerator.gcd(rDenominator);
        return new Fraction(rNumerator.divide(gcd), rDenominator.divide(gcd));
    }

    public Fraction subtract(Fraction other) {
        BigInteger rNumerator, rDenominator;
        rNumerator = this.numerator.multiply(other.denominator).subtract(other.numerator.multiply(this.denominator));
        rDenominator = this.denominator.multiply(other.denominator);
        BigInteger gcd = rNumerator.gcd(rDenominator);
        return new Fraction(rNumerator.divide(gcd), rDenominator.divide(gcd));
    }

    public Fraction multiply(Fraction other) {
        BigInteger rNumerator, rDenominator;
        rNumerator = this.numerator.multiply(other.numerator);
        rDenominator = this.denominator.multiply(other.denominator);
        BigInteger gcd = rNumerator.gcd(rDenominator);
        return new Fraction(rNumerator.divide(gcd), rDenominator.divide(gcd));
    }

    public Fraction divide(Fraction other) {
        BigInteger rNumerator, rDenominator;
        rNumerator = this.numerator.multiply(other.denominator);
        rDenominator = this.denominator.multiply(other.numerator);
        BigInteger gcd = rNumerator.gcd(rDenominator);
        return new Fraction(rNumerator.divide(gcd), rDenominator.divide(gcd));
    }

    public boolean isZero() {
        return this.numerator.equals(BigInteger.ZERO);
    }

    public String toString() {
        try {
            return (new BigDecimal(this.numerator)).divide(new BigDecimal(this.denominator), Constants.PRECISION, RoundingMode.HALF_EVEN).toString();
        } catch (ArithmeticException e) {
            throw new RuntimeException(Constants.BUG);
        }
    }
}