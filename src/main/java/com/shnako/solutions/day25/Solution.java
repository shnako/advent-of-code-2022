package com.shnako.solutions.day25;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

/*
The SNAFU numbers are basically base 5 numbers offset by 2.

For part 1, the solution converts the SNAFU numbers to base 10, sums them and then converts the result back to SNAFU.
We convert back to SNAFU like we would convert to base 5, paying special attention to when the remainder rolls over.
The result is that SNAFU number.

For part 2, we simply need to have solved all the previous problems to get the star so nothing to code.
 */
public class Solution extends SolutionBase {
    private static final BigInteger FIVE = BigInteger.valueOf(5);

    @Override
    public String runPart1() throws IOException {
        List<String> snafuNumbers = readInput();

        BigInteger sum = snafuNumbers
                .stream()
                .map(this::snafuToBase10)
                .reduce(ZERO, BigInteger::add);

        System.out.println("The SNAFU sum in decimal is: " + sum);
        return base10ToSnafu(sum);
    }

    @Override
    public String runPart2() {
        return "Merry Christmas!";
    }

    public BigInteger snafuToBase10(String snafuNumber) {
        BigInteger b10 = ZERO;
        for (char snafuDigit : snafuNumber.toCharArray()) {
            b10 = b10.multiply(FIVE).add(snafuDigitToBase10(snafuDigit));
        }
        return b10;
    }

    public String base10ToSnafu(BigInteger b10) {
        StringBuilder sb = new StringBuilder();
        while (b10.compareTo(ZERO) > 0) {
            int remainder = b10.mod(FIVE).intValue();
            b10 = b10.divide(FIVE);
            if (remainder == 3) {
                b10 = b10.add(ONE);
                remainder = -2;
            }
            if (remainder == 4) {
                b10 = b10.add(ONE);
                remainder = -1;
            }
            sb.insert(0, base10ToSnafuDigit(remainder));
        }

        return sb.toString();
    }

    private BigInteger snafuDigitToBase10(char snafuDigit) {
        return switch (snafuDigit) {
            case '=' -> BigInteger.valueOf(-2);
            case '-' -> BigInteger.valueOf(-1);
            case '0' -> BigInteger.valueOf(0);
            case '1' -> BigInteger.valueOf(1);
            case '2' -> BigInteger.valueOf(2);
            default -> throw new RuntimeException("Illegal snafu character: " + snafuDigit);
        };
    }

    private char base10ToSnafuDigit(int base10) {
        return switch (base10) {
            case -2 -> '=';
            case -1 -> '-';
            case 0 -> '0';
            case 1 -> '1';
            case 2 -> '2';
            default -> throw new RuntimeException("Unexpected base 10 value: " + base10);
        };
    }

    private List<String> readInput() throws IOException {
        return InputProcessingUtil.readInputLines(getDay());
    }
}