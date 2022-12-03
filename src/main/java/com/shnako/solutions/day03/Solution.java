package com.shnako.solutions.day03;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Solution extends SolutionBase {
    @Override
    public String runPart1() throws IOException {
        List<String> backpacks = InputProcessingUtil.readInputLines(getDay());
        return backpacks
                .stream()
                .map(this::getDuplicateItem)
                .map(this::getPriority)
                .mapToInt(Integer::intValue)
                .sum() + "";
    }

    @Override
    public String runPart2() throws IOException {
        List<String> backpacks = InputProcessingUtil.readInputLines(getDay());
        List<List<String>> groups = Lists.partition(backpacks, 3);
        return groups
                .stream()
                .map(this::getBadge)
                .map(this::getPriority)
                .mapToInt(Integer::intValue)
                .sum() + "";
    }

    private char getBadge(List<String> group) {
        Set<Integer> backpack1itemSet = group.get(0).chars().boxed().collect(Collectors.toSet());
        Set<Integer> backpack2itemSet = group.get(1).chars().boxed().collect(Collectors.toSet());
        Set<Integer> backpack3itemSet = group.get(2).chars().boxed().collect(Collectors.toSet());

        backpack1itemSet.retainAll(backpack2itemSet);
        backpack1itemSet.retainAll(backpack3itemSet);

        return (char) Iterables.getOnlyElement(backpack1itemSet).intValue();
    }

    private char getDuplicateItem(String backpack) {
        String compartment1 = backpack.substring(0, (backpack.length() / 2));
        String compartment2 = backpack.substring((backpack.length() / 2));

        for (char c1 : compartment1.toCharArray()) {
            for (char c2 : compartment2.toCharArray()) {
                if (c1 == c2) {
                    return c1;
                }
            }
        }

        return 0;
    }

    private int getPriority(char item) {
        return item > 96 ? item - 96 : item - 64 + 26;
    }
}