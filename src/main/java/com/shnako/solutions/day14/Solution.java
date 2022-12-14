package com.shnako.solutions.day14;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.max;
import static java.lang.Math.min;

/*
This solution is based on the observation that sand behaves like rock once settled into position,
so we don't need to differentiate between them (sand is ground up rock after all).
We hold a map of coordinates where there is rock or sand, with the column index being the key
and the value being a sorted list of occupied points in that column, with the highest first and lowest last.

For part 1, we determine the lowest point and start dropping each grain of sand, settling it as per the algorithm.
Once the first grain of sand goes as low as the lowest existing occupied position, it has fallen into the abyss.
The result is the number of grains of sand that have settled so far as all the following will fall into the abyss too.

For part 2, we first draw the floor and adjust the lowest point accordingly.
The floor goes left and right the same distance as it is from the top as in the worst case scenario with no rocks,
that's how far grains can go before getting stuck at the top.
We drop grains of sand according to the algorithm until we drop a grain that can't go anywhere.
The result is the number of grains of sand that have settled so far as all the following have nowhere to go.
 */
@SuppressWarnings("OptionalGetWithoutIsPresent")
public class Solution extends SolutionBase {
    private final Pair<Integer, Integer> SAND_SOURCE = Pair.of(500, 0);

    @Override
    public String runPart1() throws IOException {
        Map<Integer, List<Integer>> map = readInput();

        int maxDepth = map.values().stream()
                .map(l -> l.get(l.size() - 1))
                .max(Integer::compareTo)
                .get();


        for (int unit = 0; ; unit++) {
            boolean completed = pourSand(map, maxDepth);
            if (completed) {
                return unit + "";
            }
        }
    }

    @Override
    public String runPart2() throws IOException {
        Map<Integer, List<Integer>> map = readInput();

        int maxDepth = map.values().stream()
                .map(l -> l.get(l.size() - 1))
                .max(Integer::compareTo)
                .orElse(-1);
        int floorLeft = map.keySet()
                .stream()
                .min(Integer::compareTo)
                .get() - maxDepth;
        int floorRight = map.keySet()
                .stream()
                .max(Integer::compareTo)
                .get() + maxDepth;
        drawLine(map, Pair.of(floorLeft, maxDepth + 2), Pair.of(floorRight, maxDepth + 2));
        maxDepth += 2;

        for (int unit = 1; ; unit++) {
            boolean completed = pourSand(map, maxDepth);
            if (completed) {
                return unit + "";
            }
        }
    }

    private boolean pourSand(Map<Integer, List<Integer>> map, int maxDepth) {
        int x = SAND_SOURCE.getLeft();
        int y = SAND_SOURCE.getRight();

        while (true) {
            // Fallen into abyss.
            if (y == maxDepth) {
                return true;
            }

            // Fall straight down.
            if (!map.containsKey(x)) {
                return true;
            }
            if (!map.get(x).contains(y + 1)) {
                y++;
                continue;
            }

            // Fall down and to the left.
            if (!map.containsKey(x - 1)) {
                return true;
            }
            if (!map.get(x - 1).contains(y + 1)) {
                x--;
                y++;
                continue;
            }

            // Fall down and to the right.
            if (!map.containsKey(x + 1)) {
                return true;
            }
            if (!map.get(x + 1).contains(y + 1)) {
                x++;
                y++;
                continue;
            }

            // It's full all the way up to the sand source.
            if (x == SAND_SOURCE.getLeft() && y == SAND_SOURCE.getRight()) {
                return true;
            }

            insertSorted(map.get(x), y);
            return false;
        }
    }

    private Map<Integer, List<Integer>> readInput() throws IOException {
        List<String> paths = InputProcessingUtil.readInputLines(getDay());

        var result = new HashMap<Integer, List<Integer>>();
        for (String path : paths) {
            List<Pair<Integer, Integer>> lines = Arrays.stream(path.split(" -> "))
                    .map(line -> {
                        String[] components = line.split(",");
                        return Pair.of(Integer.parseInt(components[0]), Integer.parseInt(components[1]));
                    })
                    .toList();
            for (int i = 1; i < lines.size(); i++) {
                drawLine(result, lines.get(i - 1), lines.get(i));
            }
        }
        return result;
    }

    private void drawLine(Map<Integer, List<Integer>> map, Pair<Integer, Integer> p1, Pair<Integer, Integer> p2) {
        if (p1.getLeft().equals(p2.getLeft())) { // Vertical
            if (!map.containsKey(p1.getLeft())) {
                map.put(p1.getLeft(), new ArrayList<>());
            }
            for (int i = min(p1.getRight(), p2.getRight()); i <= max(p1.getRight(), p2.getRight()); i++) {
                insertSorted(map.get(p1.getLeft()), i);
            }
        } else { // Horizontal
            for (int j = min(p1.getLeft(), p2.getLeft()); j <= max(p1.getLeft(), p2.getLeft()); j++) {
                if (!map.containsKey(j)) {
                    map.put(j, new ArrayList<>());
                }
                insertSorted(map.get(j), p1.getRight());
            }
        }
    }

    private void insertSorted(List<Integer> list, int value) {
        for (int i = 0; i < list.size(); i++) {
            if (value < list.get(i)) {
                list.add(i, value);
                return;
            }
            if (value == list.get(i)) {
                return;
            }
        }
        list.add(value);
    }
}