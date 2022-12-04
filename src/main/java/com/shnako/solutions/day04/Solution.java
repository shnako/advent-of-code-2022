package com.shnako.solutions.day04;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class Solution extends SolutionBase {
    @Override
    public String runPart1() throws IOException {
        List<Boolean> pairs = InputProcessingUtil.readInputLines(getDay())
                .stream()
                .map(Pair::new)
                .map(Pair::overlaps)
                .toList();
        return Collections.frequency(pairs, true) + "";
    }

    @Override
    public String runPart2() throws IOException {
        List<Boolean> pairs = InputProcessingUtil.readInputLines(getDay())
                .stream()
                .map(Pair::new)
                .map(Pair::overlapsAtAll)
                .toList();
        return Collections.frequency(pairs, true) + "";
    }

    private static class Pair {
        private final int a1;
        private final int b1;
        private final int a2;
        private final int b2;

        private Pair(String inputLine) {
            String[] components = inputLine.split(",");
            String[] pair1 = components[0].split("-");
            String[] pair2 = components[1].split("-");

            a1 = Integer.parseInt(pair1[0]);
            b1 = Integer.parseInt(pair1[1]);
            a2 = Integer.parseInt(pair2[0]);
            b2 = Integer.parseInt(pair2[1]);
        }

        private boolean overlaps() {
            return (a1 >= a2 && b1 <= b2) || (a2 >= a1 && b2 <= b1);
        }

        private boolean overlapsAtAll() {
            return (a1 <= a2 && b1 >= a2) || (a2 <= a1 && b2 >= a1);
        }
    }
}

