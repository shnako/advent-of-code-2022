package com.shnako.solutions.day11;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;
import lombok.Data;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/*
We use the Monkey class to store all the information for each monkey throughout the simulation.

For part 1 we can simply simulate the rounds as described to get the result.

For part 2 however, because we no longer reduce the worry by dividing by 3,
the numbers quickly grow to completely unmanageable sizes.
Using BigDecimal still works but grinds to a halt after a few hundred rounds.

The solution here is based on the observations that:
- We have a very low number of monkeys.
- The test is always a division by a low prime divisor that doesn't repeat between monkeys.
- Each monkey will only throw to one of the same 2 monkeys.
- We don't actually want the worry levels but the number of inspections done by each monkey.

This means that instead of the huge worry level, we really only care about
the remainder of dividing the worry level at each point by the Least Common Multiple of all test divisors.
So for example if we have divisors 23 and 19, their LCM is 437 (23 * 19).
Trying to find out if 437190 is divisible by 19 is the same as trying to find out if 190 (437190 % 437) is.

Using this, we can keep the numbers small by only using the modulo of the worry level rather than the huge number,
while also making sure that in each round the items are passed around the same way, thus getting the correct result.

Because the requirements for both parts are essentially the same,
both parts have been refactored to use the same code with different parameters.
 */
public class Solution extends SolutionBase {
    @Override
    public String runPart1() throws IOException {
        List<Monkey> monkeys = readInput();
        long monkeyBusiness = getMonkeyBusiness(monkeys, 20, true);
        return monkeyBusiness + "";
    }

    @Override
    public String runPart2() throws IOException {
        List<Monkey> monkeys = readInput();
        long monkeyBusiness = getMonkeyBusiness(monkeys, 10000, false);
        return monkeyBusiness + "";
    }

    private long getMonkeyBusiness(List<Monkey> monkeys, int rounds, boolean reduceWorryLevel) {
        int lcm = monkeys
                .stream()
                .map(Monkey::getDivisor)
                .reduce(1, (a, b) -> a * b);

        for (int i = 0; i < rounds; i++) {
            for (Monkey monkey : monkeys) {
                while (!monkey.getItems().isEmpty()) {
                    long worryLevel = monkey.getItems().remove();
                    long operand = monkey.getOperand() > 0 ? monkey.getOperand() : worryLevel;
                    worryLevel = switch (monkey.getOperation()) {
                        case "*" -> (worryLevel * operand) % lcm;
                        case "+" -> (worryLevel + operand) % lcm;
                        default -> throw new InvalidParameterException("Invalid operation: " + monkey.getOperation());
                    };
                    if (reduceWorryLevel) {
                        worryLevel /= 3;
                    }
                    if (worryLevel % monkey.getDivisor() == 0) {
                        monkeys.get(monkey.trueMonkeyId).getItems().add(worryLevel);
                    } else {
                        monkeys.get(monkey.falseMonkeyId).getItems().add(worryLevel);
                    }
                    monkey.setItemsInspected(monkey.getItemsInspected() + 1);
                }
            }
        }

        return monkeys
                .stream()
                .map(Monkey::getItemsInspected)
                .sorted(Comparator.reverseOrder())
                .limit(2)
                .reduce(1L, (a, b) -> a * b);
    }

    private List<Monkey> readInput() throws IOException {
        List<String> lines = InputProcessingUtil.readInputLines(getDay());

        var monkeys = new ArrayList<Monkey>();
        for (int i = 0; i < lines.size(); i += 7) {
            monkeys.add(readMonkey(lines.subList(i, i + 6)));
        }
        return monkeys;
    }

    private Monkey readMonkey(List<String> monkeyLines) {
        var monkey = new Monkey();

        String[] itemComponents = monkeyLines.get(1).substring(18).split(", ");
        monkey.setItems(new LinkedList<>(Arrays.stream(itemComponents)
                .map(Long::parseLong)
                .toList()));

        String[] operatingComponents = monkeyLines.get(2).substring(23).split(" ");
        monkey.setOperation(operatingComponents[0]);
        monkey.setOperand(operatingComponents[1].equals("old") ? 0 : Integer.parseInt(operatingComponents[1]));

        monkey.setDivisor(Integer.parseInt(monkeyLines.get(3).substring(21)));

        monkey.setTrueMonkeyId(Integer.parseInt(monkeyLines.get(4).substring(29)));
        monkey.setFalseMonkeyId(Integer.parseInt(monkeyLines.get(5).substring(30)));

        return monkey;
    }

    @Data
    private static class Monkey {
        Queue<Long> items;
        String operation;
        int operand;
        int divisor;
        int trueMonkeyId;
        int falseMonkeyId;
        long itemsInspected;
    }
}