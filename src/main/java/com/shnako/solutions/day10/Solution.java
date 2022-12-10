package com.shnako.solutions.day10;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/*
Nothing special in this solution - we just execute the program as described.
 */
@SuppressWarnings("FieldCanBeLocal")
public class Solution extends SolutionBase {
    private final List<Integer> PART_1_CYCLES_TO_MEASURE = List.of(20, 60, 100, 140, 180, 220);
    private final int PART_2_WIDTH = 40;

    @Override
    public String runPart1() throws IOException {
        List<Pair<String, Integer>> program = readInput();

        int cycle = 1;
        int x = 1;
        int signalStrength = 0;
        for (Pair<String, Integer> command : program) {
            if (command.getLeft().equals("noop")) {
                signalStrength += getSignalStrengthAtCycle(cycle, x);
                cycle++;
            } else {
                signalStrength += getSignalStrengthAtCycle(cycle, x);
                cycle++;
                signalStrength += getSignalStrengthAtCycle(cycle, x);
                cycle++;
                x += command.getRight();
            }
        }

        return signalStrength + "";
    }

    @Override
    public String runPart2() throws IOException {
        List<Pair<String, Integer>> program = readInput();

        int cycle = 1;
        int x = 1;
        StringBuilder output = new StringBuilder();
        for (Pair<String, Integer> command : program) {
            if (command.getLeft().equals("noop")) {
                output.append(drawPixel(cycle - 1, x));
                cycle++;
            } else {
                output.append(drawPixel(cycle - 1, x));
                cycle++;
                output.append(drawPixel(cycle - 1, x));
                cycle++;
                x += command.getRight();
            }
        }

        return output.toString();
    }

    private int getSignalStrengthAtCycle(int cycle, int x) {
        return PART_1_CYCLES_TO_MEASURE.contains(cycle) ? x * cycle : 0;
    }

    private String drawPixel(int pixel, int x) {
        List<Integer> sprite = List.of(x - 1, x, x + 1);

        String result = pixel != 0 && (pixel) % PART_2_WIDTH == 0 ? "\n" : "";
        result += sprite.contains(pixel % PART_2_WIDTH) ? "#" : ".";
        return result;
    }

    private List<Pair<String, Integer>> readInput() throws IOException {
        return InputProcessingUtil.readInputLines(getDay())
                .stream()
                .map(line -> {
                    String[] components = line.split(" ");
                    if (components.length == 1) {
                        return Pair.of(components[0], 0);
                    }
                    return Pair.of(components[0], Integer.parseInt(components[1]));
                })
                .collect(Collectors.toList());
    }
}