package com.shnako.solutions.day01;

import com.shnako.solutions.SolutionBase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Solution implements SolutionBase {
    @Override
    public String runPart1() throws Exception {
        List<Integer> elfCalories = readTotalElfCalories();
        return Collections.max(elfCalories) + "";
    }

    @Override
    public String runPart2() throws Exception {
        List<Integer> elfCalories = readTotalElfCalories();
        return elfCalories
                .stream()
                .sorted(Comparator.reverseOrder())
                .limit(3)
                .mapToInt(Integer::intValue)
                .sum() + "";
    }

    private List<Integer> readTotalElfCalories() throws IOException {
        List<String> inputLines = readInputLines();
        List<Integer> elfCalories = new ArrayList<>();
        int currentElfCalories = 0;
        for (String line : inputLines) {
            if (line.isBlank()) {
                elfCalories.add(currentElfCalories);
                currentElfCalories = 0;
                continue;
            }
            currentElfCalories += Integer.parseInt(line);
        }
        return elfCalories;
    }

    private List<String> readInputLines() throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get("src/main/java/com/shnako/solutions/day01/input.txt"))) {
            return stream.collect(Collectors.toList());
        }
    }
}