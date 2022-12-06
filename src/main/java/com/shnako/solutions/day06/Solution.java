package com.shnako.solutions.day06;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public class Solution extends SolutionBase {
    @Override
    public String runPart1() throws IOException {
        return findFirstSequenceOfLength(4);
    }

    @Override
    public String runPart2() throws IOException {
        return findFirstSequenceOfLength(14);
    }

    public String findFirstSequenceOfLength(int length) throws IOException {
        String input = InputProcessingUtil.readInputLines(getDay()).get(0);

        for (int i = length; i <= input.length(); i++) {
            Set<Character> set = input.substring(i - length, i)
                    .chars().mapToObj(x -> (char) x).collect(Collectors.toSet());
            if (set.size() == length) {
                return i + "";
            }
        }

        return "Sequence not found.";
    }
}