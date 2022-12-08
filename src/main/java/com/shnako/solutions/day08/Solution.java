package com.shnako.solutions.day08;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/*
We simply look from each tree in all 4 directions to find the answers.
The code looks quite duplicated, but it's very easy to read this way so will not attempt to make it shorter.
 */

@SuppressWarnings("DuplicatedCode")
public class Solution extends SolutionBase {
    @Override
    public String runPart1() throws IOException {
        List<List<Integer>> treeHeights = readInput();
        List<List<Boolean>> treeVisibilities = determineVisibilities(treeHeights);
        return treeVisibilities
                .stream()
                .flatMapToLong(l -> LongStream.of(l.stream().filter(x -> x).count()))
                .sum() + "";
    }

    @Override
    public String runPart2() throws IOException {
        List<List<Integer>> treeHeights = readInput();
        List<List<Integer>> treeScores = determineScenicScores(treeHeights);
        return Collections.max(treeScores
                .stream()
                .flatMap(List::stream)
                .toList()) + "";
    }

    private List<List<Boolean>> determineVisibilities(List<List<Integer>> treeHeights) {
        List<List<Boolean>> treeVisibilities = new ArrayList<>();
        for (int i = 0; i < treeHeights.size(); i++) {
            treeVisibilities.add(new ArrayList<>());
            for (int j = 0; j < treeHeights.get(i).size(); j++) {
                treeVisibilities.get(i).add(isTreeVisible(treeHeights, i, j));
            }
        }
        return treeVisibilities;
    }

    private Boolean isTreeVisible(List<List<Integer>> treeHeights, int x, int y) {
        if (x == 0 || x == treeHeights.size() - 1 || y == 0 || y == treeHeights.get(x).size() - 1) {
            return true;
        }

        int thisTreeHeight = treeHeights.get(x).get(y);

        // Up
        for (int i = x - 1; i >= 0; i--) {
            if (treeHeights.get(i).get(y) >= thisTreeHeight) {
                break;
            }
            if (i == 0) {
                return true;
            }
        }

        // Down
        for (int i = x + 1; i <= treeHeights.size(); i++) {
            if (treeHeights.get(i).get(y) >= thisTreeHeight) {
                break;
            }
            if (i == treeHeights.size() - 1) {
                return true;
            }
        }

        // Left
        for (int i = y - 1; i >= 0; i--) {
            if (treeHeights.get(x).get(i) >= thisTreeHeight) {
                break;
            }
            if (i == 0) {
                return true;
            }
        }

        // Right
        for (int i = y + 1; i <= treeHeights.get(x).size(); i++) {
            if (treeHeights.get(x).get(i) >= thisTreeHeight) {
                break;
            }
            if (i == treeHeights.get(x).size() - 1) {
                return true;
            }
        }
        return false;
    }

    private List<List<Integer>> determineScenicScores(List<List<Integer>> treeHeights) {
        List<List<Integer>> treeScores = new ArrayList<>();
        for (int i = 0; i < treeHeights.size(); i++) {
            treeScores.add(new ArrayList<>());
            for (int j = 0; j < treeHeights.get(i).size(); j++) {
                treeScores.get(i).add(getScenicScores(treeHeights, i, j));
            }
        }
        return treeScores;
    }

    private int getScenicScores(List<List<Integer>> treeHeights, int x, int y) {
        int thisTreeHeight = treeHeights.get(x).get(y);

        int up = 0;
        for (int i = x - 1; i >= 0; i--) {
            up++;
            if (treeHeights.get(i).get(y) >= thisTreeHeight) {
                break;
            }
        }

        int down = 0;
        for (int i = x + 1; i < treeHeights.size(); i++) {
            down++;
            if (treeHeights.get(i).get(y) >= thisTreeHeight) {
                break;
            }
        }

        int left = 0;
        for (int i = y - 1; i >= 0; i--) {
            left++;
            if (treeHeights.get(x).get(i) >= thisTreeHeight) {
                break;
            }
        }

        int right = 0;
        for (int i = y + 1; i < treeHeights.get(x).size(); i++) {
            right++;
            if (treeHeights.get(x).get(i) >= thisTreeHeight) {
                break;
            }
        }

        return up * down * left * right;
    }

    private List<List<Integer>> readInput() throws IOException {
        List<String> lines = InputProcessingUtil.readInputLines(getDay());

        return lines
                .stream()
                .map(line -> line
                        .chars()
                        .boxed()
                        .map(ch -> ch - 48)
                        .collect(Collectors.toList())
                )
                .collect(Collectors.toList());
    }
}