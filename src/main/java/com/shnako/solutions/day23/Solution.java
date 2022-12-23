package com.shnako.solutions.day23;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
We keep the current positions of all elves as a list of Point.
At each round, we determine the requested new positions for all elves according to the puzzle rules
and hold these in a HashMultimap where the key is the position and the values are the indices of the elves requesting it.
If there is exactly 1 elf requesting a position in this Multimap, the elf can move into that position.

For part 1, we iterate for 10 rounds and then determine a tight bounding box around all elves.
The result is the size of the bounding box minus the number of elves.

For part 2, we iterate until none of the elves can move anymore.
The result is the number of the first round where the elves no longer move.
 */
@SuppressWarnings("OptionalGetWithoutIsPresent")
public class Solution extends SolutionBase {
    private static final int[][] AROUND = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};
    private static final int[][] MOVES = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // N, S, W, E
    private static final int[][][] CHECKS = {
            {{-1, -1}, {-1, 0}, {-1, 1}}, // NW, N, NE
            {{1, -1}, {1, 0}, {1, 1}}, // SW, S, SE
            {{-1, -1}, {0, -1}, {1, -1}}, //NW, W, SW
            {{-1, 1}, {0, 1}, {1, 1}} //NE, E, SE
    };

    @Override
    public String runPart1() throws IOException {
        List<Point> elfLocations = readInput();
        int direction = 0;
        print(elfLocations, 0);
        for (int i = 1; i <= 10; i++) {
            move(elfLocations, direction);
            print(elfLocations, i);
            direction = (direction + 1) % MOVES.length;
        }

        Pair<Point, Point> boundingBox = getBoundingBox(elfLocations);
        return ((boundingBox.getRight().x - boundingBox.getLeft().x + 1)
                * (boundingBox.getRight().y - boundingBox.getLeft().y + 1))
                - elfLocations.size() + "";
    }

    @Override
    public String runPart2() throws IOException {
        List<Point> elfLocations = readInput();
        int direction = 0;
        // print(elfLocations, 0);
        for (int i = 1; ; i++) {
            boolean hasMoved = move(elfLocations, direction);
            // print(elfLocations, i);
            direction = (direction + 1) % MOVES.length;
            if (!hasMoved) {
                return i + "";
            }
        }
    }

    private boolean move(List<Point> elfLocations, int firstDirection) {
        Set<Point> elfLocationSet = new HashSet<>(elfLocations);
        Multimap<Point, Integer> nextElfLocations = HashMultimap.create();
        for (int iElf = 0; iElf < elfLocations.size(); iElf++) {
            if (canMove(elfLocations.get(iElf), elfLocationSet)) {
                for (int iDir = 0; iDir < MOVES.length; iDir++) {
                    int direction = (firstDirection + iDir) % MOVES.length;
                    if (!hasNeighbourInDirection(elfLocations.get(iElf), direction, elfLocationSet)) {
                        Point nextLocation = new Point(elfLocations.get(iElf).x + MOVES[direction][0], elfLocations.get(iElf).y + MOVES[direction][1]);
                        nextElfLocations.put(nextLocation, iElf);
                        break;
                    }
                }
            }
        }

        boolean hasMoved = false;
        for (Point p : nextElfLocations.keySet()) {
            if (nextElfLocations.get(p).size() == 1) {
                Point elfLocation = elfLocations.get(nextElfLocations.get(p).stream().findFirst().get());
                elfLocation.setLocation(p);
                hasMoved = true;
            }
        }
        return hasMoved;
    }

    private boolean hasNeighbourInDirection(Point point, int dir, Set<Point> elfLocationSet) {
        for (int[] coordinates : CHECKS[dir]) {
            Point neighbour = new Point(point.x + coordinates[0], point.y + coordinates[1]);
            if (elfLocationSet.contains(neighbour)) {
                return true;
            }
        }
        return false;
    }

    private boolean canMove(Point point, Set<Point> elfLocationSet) {
        for (int[] coordinates : AROUND) {
            Point neighbour = new Point(point.x + coordinates[0], point.y + coordinates[1]);
            if (elfLocationSet.contains(neighbour)) {
                return true;
            }
        }
        return false;
    }

    private Pair<Point, Point> getBoundingBox(List<Point> elfLocations) {
        int minX = 0, minY = 0, maxX = 0, maxY = 0;
        for (Point p : elfLocations) {
            if (p.x < minX) {
                minX = p.x;
            }
            if (p.x > maxX) {
                maxX = p.x;
            }
            if (p.y < minY) {
                minY = p.y;
            }
            if (p.y > maxY) {
                maxY = p.y;
            }
        }
        return Pair.of(new Point(minX, minY), new Point(maxX, maxY));
    }

    private List<Point> readInput() throws IOException {
        List<Point> elfCoordinates = new ArrayList<>();
        List<String> inputLines = InputProcessingUtil.readInputLines(getDay());
        for (int i = 0; i < inputLines.size(); i++) {
            for (int j = 0; j < inputLines.get(i).length(); j++) {
                if (inputLines.get(i).charAt(j) == '#') {
                    elfCoordinates.add(new Point(i, j));
                }
            }
        }
        return elfCoordinates;
    }

    private void print(List<Point> elfLocations, int round) {
        System.out.println("== End of Round " + round + " ==");
        Set<Point> elfLocationSet = new HashSet<>(elfLocations);
        Pair<Point, Point> box = getBoundingBox(elfLocations);
        for (int i = box.getLeft().x; i <= box.getRight().x; i++) {
            for (int j = box.getLeft().y; j <= box.getRight().y; j++) {
                if (elfLocationSet.contains(new Point(i, j))) {
                    System.out.print("#");
                } else {
                    System.out.print(".");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}