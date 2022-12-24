package com.shnako.solutions.day24;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.Point;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
We hold the map of the valley in a Multimap, mapping each Point in the valley to what is there at the specified time.
We start at the entrance and at each minute, we check to see if the positions up, down, left, right and current
will be free from blizzards in the next minute, and if so we move there, keeping a set of all positions we could be at.
Once we see the exit in the list of current positions, we know that we have reached it.

For part 1, the result is the first minute where we've reached the exit.

For part 2, we do 3 trips, swapping the entrance and exit on the second trip and keeping the valley map between trips.
The result is the sum of the minutes it took to do each trip.
 */
@SuppressWarnings({"OptionalGetWithoutIsPresent", "DuplicatedCode"})
public class Solution extends SolutionBase {
    private static final int[][] MOVES = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {0, 0}};

    @Override
    public String runPart1() throws IOException {
        Multimap<Point, Character> valleyMap = readInput();
        int valleyHeight = valleyMap.keySet().stream().max(Comparator.comparingInt(p -> p.x)).get().x + 1;
        int valleyWidth = valleyMap.keySet().stream().max(Comparator.comparingInt(p -> p.y)).get().y + 1;

        var entrance = new Point(0, 1);
        var exit = new Point(valleyHeight - 1, valleyWidth - 2);

        int result = crossValley(valleyMap, valleyHeight, valleyWidth, entrance, exit).getLeft();

        return result + "";
    }

    @Override
    public String runPart2() throws IOException {
        Multimap<Point, Character> valleyMap = readInput();
        int valleyHeight = valleyMap.keySet().stream().max(Comparator.comparingInt(p -> p.x)).get().x + 1;
        int valleyWidth = valleyMap.keySet().stream().max(Comparator.comparingInt(p -> p.y)).get().y + 1;

        var entrance = new Point(0, 1);
        var exit = new Point(valleyHeight - 1, valleyWidth - 2);

        Pair<Integer, Multimap<Point, Character>> trip1 = crossValley(valleyMap, valleyHeight, valleyWidth, entrance, exit);
        Pair<Integer, Multimap<Point, Character>> trip2 = crossValley(trip1.getRight(), valleyHeight, valleyWidth, exit, entrance);
        Pair<Integer, Multimap<Point, Character>> trip3 = crossValley(trip2.getRight(), valleyHeight, valleyWidth, entrance, exit);

        return trip1.getLeft() + trip2.getLeft() + trip3.getLeft() + "";
    }

    private Pair<Integer, Multimap<Point, Character>> crossValley(Multimap<Point, Character> currentValleyMap, int valleyHeight, int valleyWidth, Point entrance, Point exit) {
        Set<Point> currentLocations = new HashSet<>();
        currentLocations.add(entrance);

        int minute;
        for (minute = 1; !currentLocations.contains(exit); minute++) {
            Multimap<Point, Character> nextValleyMap = getNextValleyMap(currentValleyMap, valleyHeight, valleyWidth);
            Set<Point> nextLocations = new HashSet<>();

            for (Point currentLocation : currentLocations) {
                for (int[] move : MOVES) {
                    Point nextPoint = new Point(currentLocation.x + move[0], currentLocation.y + move[1]);
                    if (nextValleyMap.get(nextPoint).size() == 1 && nextValleyMap.get(nextPoint).stream().findFirst().get() == '.') {
                        nextLocations.add(nextPoint);
                    }
                }
            }

            currentValleyMap = nextValleyMap;
            currentLocations = nextLocations;

            print(currentValleyMap, currentLocations, valleyHeight, valleyWidth, minute);
        }

        return Pair.of(minute - 1, currentValleyMap);
    }

    private Multimap<Point, Character> getNextValleyMap(Multimap<Point, Character> currentValleyMap, int valleyHeight, int valleyWidth) {
        Multimap<Point, Character> nextValleyMap = HashMultimap.create();

        for (Map.Entry<Point, Character> e : currentValleyMap.entries()) {
            switch (e.getValue()) {
                case '#' -> nextValleyMap.put(e.getKey(), e.getValue());
                case '>' -> nextValleyMap.put(new Point(e.getKey().x, e.getKey().y == valleyWidth - 2 ? 1 : e.getKey().y + 1), e.getValue());
                case '<' -> nextValleyMap.put(new Point(e.getKey().x, e.getKey().y == 1 ? valleyWidth - 2 : e.getKey().y - 1), e.getValue());
                case 'v' -> nextValleyMap.put(new Point(e.getKey().x == valleyHeight - 2 ? 1 : e.getKey().x + 1, e.getKey().y), e.getValue());
                case '^' -> nextValleyMap.put(new Point(e.getKey().x == 1 ? valleyHeight - 2 : e.getKey().x - 1, e.getKey().y), e.getValue());
                default -> {}
            }
        }

        for (int x = 0; x < valleyHeight; x++) {
            for (int y = 0; y < valleyWidth; y++) {
                Point p = new Point(x, y);
                if (!nextValleyMap.keySet().contains(p)) {
                    nextValleyMap.put(p, '.');
                }
            }
        }

        return nextValleyMap;
    }

    private Multimap<Point, Character> readInput() throws IOException {
        List<String> inputLines = InputProcessingUtil.readInputLines(getDay());
        Multimap<Point, Character> valleyMap = HashMultimap.create();
        for (int x = 0; x < inputLines.size(); x++) {
            for (int y = 0; y < inputLines.get(x).length(); y++) {
                valleyMap.put(new Point(x, y), inputLines.get(x).charAt(y));
            }
        }
        return valleyMap;
    }

    private void print(Multimap<Point, Character> valleyMap, Set<Point> currentLocations, int height, int width, int minute) {
        System.out.println("Minute " + minute + ":");
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                Point p = new Point(x, y);
                if (currentLocations.contains(p)) {
                    System.out.print("E");
                } else {
                    Collection<Character> atPoint = valleyMap.get(p);
                    if (atPoint.size() == 1) {
                        System.out.print(atPoint.stream().findFirst().get());
                    } else {
                        System.out.print(atPoint.size());
                    }
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}