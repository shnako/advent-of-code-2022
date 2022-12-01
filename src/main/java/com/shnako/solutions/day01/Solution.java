package com.shnako.solutions.day01;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Solution extends SolutionBase {
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

    private List<Integer> readTotalElfCalories() throws Exception {
        List<String> inputLines = InputProcessingUtil.readInputLines(getDay());
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
}