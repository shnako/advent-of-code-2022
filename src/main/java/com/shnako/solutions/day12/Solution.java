package com.shnako.solutions.day12;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/*
We use Dijkstra's algorithm to find the minimum number of steps to the destination.

For part 1, we search from point 'S' to point 'E'.
For part 2, we also use all points 'a' as starting points in the algorithm.
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

    public String runPart(int part) throws IOException {
        char[][] heightMap = readInput();
        boolean[][] visitMap = new boolean[heightMap.length][heightMap[0].length];
        int[][] steps = new int[heightMap.length][heightMap[0].length];
        Queue<Pair<Integer, Integer>> nextNodes = new LinkedList<>();

        Pair<Integer, Integer> destination = null;
        for (int i = 0; i < heightMap.length; i++) {
            for (int j = 0; j < heightMap[0].length; j++) {
                steps[i][j] = Integer.MAX_VALUE;
                if (heightMap[i][j] == 'S' || (part == 2 && heightMap[i][j] == 'a')) {
                    nextNodes.add(Pair.of(i, j));
                    steps[i][j] = 0;
                    heightMap[i][j] = 'a';
                }
                if (heightMap[i][j] == 'E') {
                    destination = Pair.of(i, j);
                    heightMap[i][j] = 'z';
                }
            }
        }

        while (!nextNodes.isEmpty()) {
            Pair<Integer, Integer> currentNode = nextNodes.remove();

            if (currentNode.equals(destination)) {
                return steps[destination.getLeft()][destination.getRight()] + "";
            }

            List<Pair<Integer, Integer>> visitableNeighbours = getNeighbours(currentNode, heightMap)
                    .stream()
                    .filter(n -> !visitMap[n.getLeft()][n.getRight()])
                    .filter(n -> getStepsBetweenNodes(heightMap, currentNode, n) <= 1)
                    .toList();

            for (Pair<Integer, Integer> neighbour : visitableNeighbours) {
                if (steps[neighbour.getLeft()][neighbour.getRight()] > steps[currentNode.getLeft()][currentNode.getRight()] + 1) {
                    nextNodes.add(neighbour);
                    steps[neighbour.getLeft()][neighbour.getRight()] = steps[currentNode.getLeft()][currentNode.getRight()] + 1;
                }
            }

            visitMap[currentNode.getLeft()][currentNode.getRight()] = true;
        }

        return "No path found";
    }

    private List<Pair<Integer, Integer>> getNeighbours(Pair<Integer, Integer> currentNode, char[][] heightMap) {
        List<Pair<Integer, Integer>> neighbours = new ArrayList<>();
        if (currentNode.getLeft() - 1 >= 0) {
            neighbours.add(Pair.of(currentNode.getLeft() - 1, currentNode.getRight()));
        }
        if (currentNode.getLeft() + 1 < heightMap.length) {
            neighbours.add(Pair.of(currentNode.getLeft() + 1, currentNode.getRight()));
        }
        if (currentNode.getRight() - 1 >= 0) {
            neighbours.add(Pair.of(currentNode.getLeft(), currentNode.getRight() - 1));
        }
        if (currentNode.getRight() + 1 < heightMap[0].length) {
            neighbours.add(Pair.of(currentNode.getLeft(), currentNode.getRight() + 1));
        }
        return neighbours;
    }

    private int getStepsBetweenNodes(char[][] heightMap, Pair<Integer, Integer> currentNode, Pair<Integer, Integer> neighbourNode) {
        return heightMap[neighbourNode.getLeft()][neighbourNode.getRight()] - heightMap[currentNode.getLeft()][currentNode.getRight()];
    }

    private char[][] readInput() throws IOException {
        return InputProcessingUtil.readInputLines(getDay())
                .stream()
                .map(String::toCharArray)
                .toArray(char[][]::new);
    }
}