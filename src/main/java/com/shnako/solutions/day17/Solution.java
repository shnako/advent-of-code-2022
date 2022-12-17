package com.shnako.solutions.day17;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;
import org.apache.commons.lang3.tuple.Triple;

import java.awt.*;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings({"FieldCanBeLocal", "SuspiciousNameCombination", "OptionalGetWithoutIsPresent"})
public class Solution extends SolutionBase {
    private final int WIDTH = 7;
    private final int LEFT = 2;
    private final int ABOVE = 4;

    private char[] movements;
    private int movementIndex;

    @Override
    public String runPart1() throws IOException {
        movements = readInput();
        return simulate(2022) + "";
    }

    @Override
    public String runPart2() throws IOException {
        movements = readInput();

        Triple<Integer, Integer, Integer> pattern = findPattern();
        int fallsBeforeCycles = pattern.getLeft(); // 65 / 139
        int cycleLength = pattern.getMiddle(); // 35 / 1695
        int cycleHeight = pattern.getRight(); //53 / 2671

        var falls = new BigInteger("1000000000000");

        int fallsAfterCycles = falls
                .subtract(BigInteger.valueOf(fallsBeforeCycles))
                .remainder(BigInteger.valueOf(cycleLength))
                .intValue();

        int heightOfBeforeAndOneCycleAndAfter = simulate(fallsBeforeCycles + cycleLength + fallsAfterCycles);

        var patternRepeats = falls
                .subtract(BigInteger.valueOf(fallsBeforeCycles))
                .subtract(BigInteger.valueOf(fallsAfterCycles))
                .divide(BigInteger.valueOf(cycleLength));

        return patternRepeats
                .subtract(BigInteger.ONE)
                .multiply(BigInteger.valueOf(cycleHeight))
                .add(BigInteger.valueOf(heightOfBeforeAndOneCycleAndAfter))
                .toString();
    }

    private Triple<Integer, Integer, Integer> findPattern() {
        int topPatternElements = 30;
        var topPatterns = new HashMap<Set<Point>, List<Integer>>();
        List<Point> chamber = initializeChamberFloor();
        movementIndex = 0;
        for (int fall = topPatternElements, rock = 0; fall < 5000; fall++, rock = (rock + 1) % rocks.size()) {
            dropRock(chamber, rocks.get(rock));
            int towerHeight = getTowerHeight(chamber);
            Set<Point> topPattern = chamber
                    .stream()
                    .filter(p -> p.x > towerHeight - topPatternElements)
                    .map(p -> new Point(p.x - towerHeight + topPatternElements, p.y))
                    .collect(Collectors.toSet());
            if (!topPatterns.containsKey(topPattern)) {
                topPatterns.put(topPattern, new ArrayList<>());
            }
            topPatterns.get(topPattern).add(fall);
        }

        List<Integer> fallsWithRepeatingPattern = topPatterns.values().stream().findFirst().get();

        int cycleLength = fallsWithRepeatingPattern.get(2) - fallsWithRepeatingPattern.get(1);
        int cycleHeight = simulate(fallsWithRepeatingPattern.get(2)) - simulate(fallsWithRepeatingPattern.get(1));

        int fallsBeforeCycleBegins = topPatterns.values()
                .stream()
                .filter(l -> l.size() == fallsWithRepeatingPattern.size())
                .map(l -> l.get(0))
                .min(Integer::compareTo)
                .get();

        return Triple.of(fallsBeforeCycleBegins, cycleLength, cycleHeight);
    }

    private int simulate(int falls) {
        List<Point> chamber = initializeChamberFloor();
        movementIndex = 0;
        for (int i = 0, rock = 0; i < falls; i++, rock = (rock + 1) % rocks.size()) {
            dropRock(chamber, rocks.get(rock));
        }

        return getTowerHeight(chamber);
    }

    private void dropRock(List<Point> chamber, List<Point> rock) {
        int towerTop = getTowerHeight(chamber);
        var rockLocation = new Point(towerTop + ABOVE, LEFT); // Bottom left point in rock shape.
        while (true) {
            var newLocation = new Point(rockLocation.x, rockLocation.y + (movements[movementIndex] == '<' ? -1 : 1));
            if (isValidRockPosition(chamber, rock, newLocation)) {
                rockLocation = newLocation;
            }
            movementIndex = (movementIndex + 1) % movements.length;

            newLocation = new Point(rockLocation.x - 1, rockLocation.y);
            if (isValidRockPosition(chamber, rock, newLocation)) {
                rockLocation = newLocation;
            } else {
                break;
            }
        }
        for (Point rockPoint : rock) {
            chamber.add(new Point(rockLocation.x + rockPoint.x, rockLocation.y + rockPoint.y));
        }
    }

    private boolean isValidRockPosition(List<Point> chamber, List<Point> rock, Point newLocation) {
        for (Point rockPoint : rock) {
            if (newLocation.y + rockPoint.y < 0 || newLocation.y + rockPoint.y > WIDTH - 1) {
                return false;
            }
            if (chamber.contains(new Point(newLocation.x + rockPoint.x, newLocation.y + rockPoint.y))) {
                return false;
            }
        }
        return true;
    }

    private List<Point> initializeChamberFloor() {
        return IntStream.range(0, WIDTH)
                .mapToObj(y -> new Point(0, y))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private int getTowerHeight(List<Point> points) {
        return points
                .stream()
                .map(point -> point.x)
                .max(Comparator.naturalOrder())
                .get();
    }

    private char[] readInput() throws IOException {
        return InputProcessingUtil.readInputLines(getDay())
                .get(0)
                .toCharArray();
    }

    List<List<Point>> rocks = List.of(
            List.of(
                    new Point(0, 0),
                    new Point(0, 1),
                    new Point(0, 2),
                    new Point(0, 3)
            ),
            List.of(
                    new Point(0, 1),
                    new Point(1, 0),
                    new Point(1, 1),
                    new Point(1, 2),
                    new Point(2, 1)
            ),
            List.of(
                    new Point(0, 0),
                    new Point(0, 1),
                    new Point(0, 2),
                    new Point(1, 2),
                    new Point(2, 2)
            ),
            List.of(
                    new Point(0, 0),
                    new Point(1, 0),
                    new Point(2, 0),
                    new Point(3, 0)
            ),
            List.of(
                    new Point(0, 0),
                    new Point(0, 1),
                    new Point(1, 0),
                    new Point(1, 1)
            )
    );
}