package com.shnako.solutions.day09;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Math.abs;

/*
The solutions for both parts are identical, with part 1 simulating 2 knots and part 2 simulating 10 knots.

We simulate each step of each movement individually, for each knot.
Every trailing knot follows its leading knot according to the mechanics described in the puzzle.
After each step, we store the position of the tail knot in a set to remove duplicates.
The result is the size of that set.
 */

public class Solution extends SolutionBase {
    @Override
    public String runPart1() throws IOException {
        List<Pair<Character, Integer>> movements = readInput();
        return simulateMovements(movements, 2) + "";
    }

    @Override
    public String runPart2() throws IOException {
        List<Pair<Character, Integer>> movements = readInput();
        return simulateMovements(movements, 10) + "";
    }

    private int simulateMovements(List<Pair<Character, Integer>> movements, int knotsToSimulate) {
        List<MutablePair<Integer, Integer>> knots = new ArrayList<>();
        for (int i = 0; i < knotsToSimulate; i++) {
            knots.add(MutablePair.of(0, 0));
        }

        var head = knots.get(0);
        var tail = knots.get(knots.size() - 1);
        Set<Pair<Integer, Integer>> tailPositions = new HashSet<>();
        for (var movement : movements) {
            for (int step = 0; step < movement.getRight(); step++) {
                switch (movement.getLeft()) {
                    case 'U' -> head.left = head.left - 1;
                    case 'D' -> head.left = head.left + 1;
                    case 'L' -> head.right = head.right - 1;
                    case 'R' -> head.right = head.right + 1;
                }

                for (int nextKnot = 1; nextKnot < knots.size(); nextKnot++) {
                    Pair<Integer, Integer> newNextKnotPosition = getTrailingKnotPosition(knots.get(nextKnot - 1), knots.get(nextKnot));
                    knots.get(nextKnot).left = newNextKnotPosition.getLeft();
                    knots.get(nextKnot).right = newNextKnotPosition.getRight();
                }
                tailPositions.add(ImmutablePair.of(tail.left, tail.right));
            }
        }

        return tailPositions.size();
    }

    private MutablePair<Integer, Integer> getTrailingKnotPosition(MutablePair<Integer, Integer> leadingKnot, MutablePair<Integer, Integer> trailingKnot) {
        var verticalDistance = leadingKnot.left - trailingKnot.left;
        var horizontalDistance = leadingKnot.right - trailingKnot.right;
        if (abs(verticalDistance) <= 1 && abs(horizontalDistance) <= 1) {
            return trailingKnot;
        }

        int left = leadingKnot.left;
        if (abs(verticalDistance) > 1) {
            left -= verticalDistance / 2;
        }

        int right = leadingKnot.right;
        if (abs(horizontalDistance) > 1) {
            right -= horizontalDistance / 2;
        }

        return MutablePair.of(left, right);
    }

    private List<Pair<Character, Integer>> readInput() throws IOException {
        return InputProcessingUtil.readInputLines(getDay())
                .stream()
                .map(line -> {
                    String[] components = line.split(" ");
                    return new ImmutablePair<>(components[0].charAt(0), Integer.parseInt(components[1]));
                })
                .collect(Collectors.toList());
    }
}