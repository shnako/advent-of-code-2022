package com.shnako.solutions.day18;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;


/*
For part 1 we look at each droplet and see how many neighbours it has.
The number of exposed sides is 6 minus the number of neighbours.
The result is the sum of exposed sides for each droplet.

For part 2 we need to find the external volume of the droplets.
Because the 3D space is quite small, it is feasible to check each outside point within a tight bounding box containing all droplets.
We start at a point with minimum coordinates within the bounding box and use BFS to check all the points that can be reached.
If a neighbour is an empty space that we've not visited, we add it to the queue of points to look from next.
If a neighbour is a droplet, we add 1 to the total surface area.
The result is the total surface area calculated by this algorithm.
 */
public class Solution extends SolutionBase {
    private static final List<Triple<Integer, Integer, Integer>> NEIGHBOURS = List.of(
            Triple.of(-1, 0, 0),
            Triple.of(1, 0, 0),
            Triple.of(0, -1, 0),
            Triple.of(0, 1, 0),
            Triple.of(0, 0, -1),
            Triple.of(0, 0, 1)
    );

    @Override
    public String runPart1() throws IOException {
        List<Triple<Integer, Integer, Integer>> droplets = readInput();
        return droplets.stream()
                .map(d -> 6 - getNeighbours(d)
                        .stream()
                        .filter(droplets::contains)
                        .count())
                .mapToInt(Long::intValue)
                .sum() + "";
    }

    @Override
    public String runPart2() throws IOException {
        List<Triple<Integer, Integer, Integer>> droplets = readInput();
        Pair<Triple<Integer, Integer, Integer>, Triple<Integer, Integer, Integer>> boundingBox = getBoundingBox(droplets);

        Queue<Triple<Integer, Integer, Integer>> searchQueue = new LinkedList<>();
        List<Triple<Integer, Integer, Integer>> visitedDroplets = new ArrayList<>();
        searchQueue.add(boundingBox.getLeft());
        int surfaceArea = 0;
        while (!searchQueue.isEmpty()) {
            Triple<Integer, Integer, Integer> droplet = searchQueue.remove();
            if (visitedDroplets.contains(droplet)) {
                continue;
            }
            List<Triple<Integer, Integer, Integer>> neighbours = getNeighbours(droplet)
                    .stream()
                    .filter(d -> isWithinBounds(boundingBox, d))
                    .filter(d -> !visitedDroplets.contains(d))
                    .toList();

            searchQueue.addAll(neighbours
                    .stream()
                    .filter(d -> !droplets.contains(d))
                    .toList());

            surfaceArea += neighbours
                    .stream()
                    .filter(droplets::contains)
                    .count();

            visitedDroplets.add(droplet);
        }

        return surfaceArea + "";
    }

    private List<Triple<Integer, Integer, Integer>> getNeighbours(Triple<Integer, Integer, Integer> droplet) {
        return NEIGHBOURS
                .stream()
                .map(n -> Triple.of(droplet.getLeft() + n.getLeft(), droplet.getMiddle() + n.getMiddle(), droplet.getRight() + n.getRight()))
                .toList();
    }

    private boolean isWithinBounds(Pair<Triple<Integer, Integer, Integer>, Triple<Integer, Integer, Integer>> boundingBox, Triple<Integer, Integer, Integer> point) {
        return boundingBox.getLeft().getLeft() <= point.getLeft() && point.getLeft() <= boundingBox.getRight().getRight()
                && boundingBox.getLeft().getMiddle() <= point.getMiddle() && point.getMiddle() <= boundingBox.getRight().getMiddle()
                && boundingBox.getLeft().getRight() <= point.getRight() && point.getRight() <= boundingBox.getRight().getRight();
    }

    private Pair<Triple<Integer, Integer, Integer>, Triple<Integer, Integer, Integer>> getBoundingBox(List<Triple<Integer, Integer, Integer>> droplets) {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;

        for (Triple<Integer, Integer, Integer> d : droplets) {
            minX = d.getLeft() < minX ? d.getLeft() : minX;
            minY = d.getMiddle() < minY ? d.getMiddle() : minY;
            minZ = d.getRight() < minZ ? d.getRight() : minZ;
            maxX = d.getLeft() > maxX ? d.getLeft() : maxX;
            maxY = d.getMiddle() > maxY ? d.getMiddle() : maxY;
            maxZ = d.getRight() > maxZ ? d.getRight() : maxZ;
        }

        return Pair.of(Triple.of(minX - 1, minY - 1, minZ - 1), Triple.of(maxX + 1, maxY + 1, maxZ + 1));
    }


    private List<Triple<Integer, Integer, Integer>> readInput() throws IOException {
        return InputProcessingUtil.readInputLines(getDay())
                .stream()
                .sorted()
                .map(line -> {
                    String[] components = line.split(",");
                    return Triple.of(Integer.parseInt(components[0]), Integer.parseInt(components[1]), Integer.parseInt(components[2]));
                })
                .collect(Collectors.toList());
    }
}