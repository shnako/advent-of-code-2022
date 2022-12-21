package com.shnako.solutions.day21;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;
import lombok.Data;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;
import java.util.stream.Collectors;

/*
The solution uses the Monkey object to represent each monkey's state.
These are stored in a map that maps from monkey id to Monkey object.

For part 1 we use the calculate method to look for calculations that can be computed.
We repeat this up until we do a run that doesn't calculate the number for any more monkeys.
The result is the number calculated for the root monkey.

For part 2 we make 2 assumptions:
- The monkey operations are a tree, with the root monkey being the root and humn being one of the leaves.
- All division operations are without remainder, otherwise there would be multiple valid answers.
We use the calculate method to do all the calculations that can be computed.
This leaves us with a tree starting from root to humn, where at each node only one of the monkeys yells a number.
We traverse this tree from root to humn, determining the correct number for each node, and stop when reaching humn.
The result is the number calculated for the humn monkey.
 */
public class Solution extends SolutionBase {
    private static final String ID_HUMAN = "humn";
    private static final String ID_ROOT = "root";

    @Override
    public String runPart1() throws IOException {
        Map<String, Monkey> monkeys = readInput();

        calculate(monkeys);

        return monkeys.get(ID_ROOT).getNumber().toString();
    }

    @Override
    public String runPart2() throws IOException {
        Map<String, Monkey> monkeys = readInput();
        monkeys.get(ID_HUMAN).setNumber(null);

        calculate(monkeys);

        monkeys.get(ID_ROOT).setOperation("=");
        determineInput(monkeys.get(ID_ROOT), monkeys);

        return monkeys.get(ID_HUMAN).getNumber().toString();
    }

    private void calculate(Map<String, Monkey> monkeys) {
        boolean calculated = true;
        while (calculated) {
            calculated = false;
            for (Monkey monkey : monkeys.values()) {
                if (monkey.getNumber() == null
                        && monkey.getLeftMonkeyId() != null && monkeys.get(monkey.getLeftMonkeyId()).getNumber() != null
                        && monkey.getLeftMonkeyId() != null && monkeys.get(monkey.getRightMonkeyId()).getNumber() != null) {
                    BigInteger leftOperand = monkeys.get(monkey.getLeftMonkeyId()).number;
                    BigInteger rightOperand = monkeys.get(monkey.getRightMonkeyId()).number;
                    BigInteger result = switch (monkey.getOperation()) {
                        case "+" -> leftOperand.add(rightOperand);
                        case "-" -> leftOperand.subtract(rightOperand);
                        case "*" -> leftOperand.multiply(rightOperand);
                        case "/" -> leftOperand.divide(rightOperand);
                        default -> throw new RuntimeException("Unknown operation " + monkey.getOperation());
                    };
                    monkey.setNumber(result);
                    calculated = true;
                }
            }
        }
    }

    private void determineInput(Monkey currentMonkey, Map<String, Monkey> monkeys) {
        while (!currentMonkey.getId().equals("humn")) {
            Monkey leftMonkey = monkeys.get(currentMonkey.getLeftMonkeyId());
            Monkey rightMonkey = monkeys.get(currentMonkey.getRightMonkeyId());
            BigInteger result = currentMonkey.getNumber();

            if (leftMonkey.getNumber() != null) {
                BigInteger leftOperand = leftMonkey.getNumber();

                BigInteger rightOperand = switch (currentMonkey.getOperation()) {
                    case "+" -> result.subtract(leftOperand);
                    case "-" -> leftOperand.subtract(result);
                    case "*" -> result.divide(leftOperand);
                    case "/" -> leftOperand.divide(result);
                    case "=" -> leftOperand;
                    default -> throw new RuntimeException("Unknown operation " + currentMonkey.getOperation());
                };

                rightMonkey.setNumber(rightOperand);
                currentMonkey = rightMonkey;
            } else {
                BigInteger rightOperand = rightMonkey.getNumber();

                BigInteger leftOperand = switch (currentMonkey.getOperation()) {
                    case "+" -> result.subtract(rightOperand);
                    case "-" -> rightOperand.add(result);
                    case "*" -> result.divide(rightOperand);
                    case "/" -> rightOperand.multiply(result);
                    case "=" -> rightOperand;
                    default -> throw new RuntimeException("Unknown operation " + currentMonkey.getOperation());
                };

                leftMonkey.setNumber(leftOperand);
                currentMonkey = leftMonkey;
            }
        }
    }

    private Map<String, Monkey> readInput() throws IOException {
        return InputProcessingUtil.readInputLines(getDay())
                .stream()
                .map(Monkey::new)
                .collect(Collectors.toMap(Monkey::getId, m -> m));
    }

    @Data
    private static class Monkey {
        private final String id;
        private String leftMonkeyId;
        private String rightMonkeyId;
        private String operation;
        private BigInteger number;

        private Monkey(String line) {
            String[] components = line.split("[: ]+");
            id = components[0];
            if (components.length == 2) {
                number = new BigInteger(components[1]);
            } else {
                leftMonkeyId = components[1];
                operation = components[2];
                rightMonkeyId = components[3];
            }
        }
    }
}