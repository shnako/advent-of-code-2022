package com.shnako.solutions.day05;

import com.google.common.collect.Iterables;
import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/*
The hardest part today is parsing the input. Once that's done, we just apply the moves and get the result.

We're using Stack objects to represent the stacks, so for part 1
we just apply the moves to move crates from the top of the source stack to the top of the destination stack.

For part 2, in order to minimize the changes to the existing solution, for each group of moves
we move each crate from the top of the source stack to the top of a temporary stack,
and then from the top of the temporary stack to the top of the destination stack.
This way the crates preserve their order as required for part 2.
 */

public class Solution extends SolutionBase {
    @Override
    public String runPart1() throws IOException {
        return runPart(1);
    }

    @Override
    public String runPart2() throws IOException {
        return runPart(2);
    }

    private String runPart(int part) throws IOException {
        List<String> input = InputProcessingUtil.readInputLines(getDay());
        List<Stack<String>> stacks = getStacks(input.subList(0, input.indexOf("")));
        List<Triple<Integer, Integer, Integer>> moves = getMoves(input.subList(input.indexOf("") + 1, input.size()));

        if (part == 1) {
            movePart1(stacks, moves);
        } else {
            movePart2(stacks, moves);
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < stacks.size(); i++) {
            stringBuilder.append(stacks.get(i).peek());
        }
        return stringBuilder.toString();
    }

    private void movePart1(List<Stack<String>> stacks, List<Triple<Integer, Integer, Integer>> moves) {
        for (Triple<Integer, Integer, Integer> move : moves) {
            for (int i = 0; i < move.getLeft(); i++) {
                stacks.get(move.getRight()).push(stacks.get(move.getMiddle()).pop());
            }
        }
    }

    private void movePart2(List<Stack<String>> stacks, List<Triple<Integer, Integer, Integer>> moves) {
        Stack<String> tempStack = new Stack<>();
        for (Triple<Integer, Integer, Integer> move : moves) {
            for (int i = 0; i < move.getLeft(); i++) {
                tempStack.push(stacks.get(move.getMiddle()).pop());
            }

            for (int i = 0; i < move.getLeft(); i++) {
                stacks.get(move.getRight()).push(tempStack.pop());
            }
        }
    }

    private List<Stack<String>> getStacks(List<String> inputLines) {
        String[] stackIds = Iterables.getLast(inputLines).split(" ");
        int stackCount = Integer.parseInt(stackIds[stackIds.length - 1]);
        List<Stack<String>> stacks = new ArrayList<>();
        for (int i = 0; i <= stackCount; i++) {
            stacks.add(new Stack<>());
        }

        for (int i = inputLines.size() - 2; i >= 0; i--) {
            String[] crates = inputLines.get(i).split(" ");
            int crateIndex = 0;
            int stackIndex = 1;
            while (crateIndex < crates.length) {
                if (StringUtils.isBlank(crates[crateIndex])) {
                    crateIndex += 4;
                } else {
                    String crate = crates[crateIndex].replace("[", "").replace("]", "");
                    stacks.get(stackIndex).push(crate);
                    crateIndex += 1;
                }
                stackIndex += 1;
            }
        }

        return stacks;
    }

    private List<Triple<Integer, Integer, Integer>> getMoves(List<String> inputLines) {
        return inputLines
                .stream()
                .map(line -> line.split("[a-z ]+"))
                .map(comps -> new ImmutableTriple<>(
                        Integer.valueOf(comps[1]),
                        Integer.valueOf(comps[2]),
                        Integer.valueOf(comps[3])
                ))
                .collect(Collectors.toList());
    }
}