package com.shnako.solutions.day15;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

/*
We store each sensor location along with its closest beacon location and Manhattan distance to it in Sensor objects.

For part 1, we iterate over each Sensor to find out what range of the specified row it can scan.
We then merge those ranges to avoid overlaps between sensors.
The result is the sum of range lengths on that row that can be scanned.

For part 2, we go over each possible row lower than the specified maximum value,
and for each one of them we determine the merged ranges.
Once we find a row that has 2 merged ranges, it means that the whole range is not covered
and that the beacon is there, at the position not covered by the 2 ranges.
The result then is the result of the calculation applied to the row and position found above.
 */
@SuppressWarnings("StatementWithEmptyBody")
public class Solution extends SolutionBase {
    @Override
    public String runPart1() throws IOException {
        List<Sensor> sensors = readInput();
        int row = 2000000;

        List<Pair<Integer, Integer>> rangesOnRow = sensors
                .stream()
                .map(sensor -> findMinMaxScannedBySensorOnRow(sensor, row))
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
        while (mergeRanges(rangesOnRow)) ;

        return rangesOnRow
                .stream()
                .map(pair -> pair.getRight() - pair.getLeft())
                .reduce(0, Integer::sum) + "";
    }

    @Override
    public String runPart2() throws IOException {
        List<Sensor> sensors = readInput();
        int max = 4000000;

        for (int row = 0; row <= max; row++) {
            int finalRow = row;
            List<Pair<Integer, Integer>> rangesOnRow = sensors
                    .stream()
                    .map(sensor -> findMinMaxScannedBySensorOnRow(sensor, finalRow))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(ArrayList::new));

            while (mergeRanges(rangesOnRow)) ;

            if (rangesOnRow.size() == 2) {
                int x = rangesOnRow.get(0).getLeft() - rangesOnRow.get(1).getRight() == 2
                        ? rangesOnRow.get(0).getLeft() - 1
                        : rangesOnRow.get(1).getLeft() - 1;

                return BigDecimal.valueOf(x)
                        .multiply(BigDecimal.valueOf(max))
                        .add(BigDecimal.valueOf(row))
                        .toString();
            }
        }
        return "Not found!";
    }

    private int getManhattanDistance(Point a, Point b) {
        return abs(a.x - b.x) + abs(a.y - b.y);
    }

    private Pair<Integer, Integer> findMinMaxScannedBySensorOnRow(Sensor sensor, int row) {
        int verticalDistance = abs(sensor.getLocation().y - row);
        int horizontalDistance = sensor.getBeaconDistance() - verticalDistance;
        if (horizontalDistance < 0) {
            return null;
        }
        return Pair.of(sensor.getLocation().x - horizontalDistance, sensor.getLocation().x + horizontalDistance);
    }

    private boolean mergeRanges(List<Pair<Integer, Integer>> ranges) {
        for (int i = 0; i < ranges.size(); i++) {
            for (int j = i + 1; j < ranges.size(); j++) {
                Pair<Integer, Integer> mergedRange = mergeRangePair(ranges.get(i), ranges.get(j));
                if (mergedRange != null) {
                    ranges.remove(ranges.get(j));
                    ranges.remove(ranges.get(i));
                    ranges.add(mergedRange);
                    return true;
                }
            }
        }
        return false;
    }

    private Pair<Integer, Integer> mergeRangePair(Pair<Integer, Integer> r1, Pair<Integer, Integer> r2) {
        if (r1.getRight() < r2.getLeft() || r2.getRight() < r1.getLeft()) {
            return null;
        }
        return Pair.of(min(r1.getLeft(), r2.getLeft()), max(r1.getRight(), r2.getRight()));
    }

    private List<Sensor> readInput() throws IOException {
        return InputProcessingUtil.readInputLines(getDay())
                .stream()
                .map(Sensor::new)
                .collect(Collectors.toList());
    }

    @Data
    private class Sensor {
        private Point location;
        private Point beacon;
        private int beaconDistance;

        public Sensor(String input) {
            String[] components = input.split("[ ,:=xy]");
            this.location = new Point(Integer.parseInt(components[4]), Integer.parseInt(components[8]));
            this.beacon = new Point(Integer.parseInt(components[16]), Integer.parseInt(components[20]));

            beaconDistance = getManhattanDistance(location, beacon);
        }
    }
}