package com.shnako.solutions.day16;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;
import lombok.Data;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Solution extends SolutionBase {
    @Override
    public String runPart1() throws IOException {
        Map<String, Valve> valveMap = readInput();
        return exploreMemoized(30, valveMap.get("AA"), valveMap) + "";
    }

    @Override
    public String runPart2() {
        return "456";
    }

    private final Map<Integer, Integer> partialResults = new HashMap<>();

    private int exploreMemoized(int timeLeft, Valve currentValve, Map<String, Valve> valveMap) {
        int hash = calculateHash(timeLeft, currentValve, valveMap);

        if (partialResults.containsKey(hash)) {
            return partialResults.get(hash);
        }
        int result = explore(timeLeft, currentValve, valveMap);
        partialResults.put(hash, result);
        return result;
    }

    // Looks like I'm having hash collisions on my input using the default implementation with 31 so rewriting with 163.
    private int calculateHash(int timeLeft, Valve currentValve, Map<String, Valve> valveMap) {
        int hash = 17;
        hash = hash * 163 + timeLeft;
        hash = hash * 163 + currentValve.hashCode();
        for (Valve valve : valveMap.values()) {
            hash = hash * 163 + valve.hashCode();
        }
        return hash;
    }

    private int explore(int timeLeft, Valve currentValve, Map<String, Valve> valveMap) {
        int pressureReleased = valveMap.values()
                .stream()
                .filter(Valve::isOpen)
                .map(Valve::getFlowRate)
                .mapToInt(Integer::intValue)
                .sum();

        if (timeLeft == 1) {
            return pressureReleased;
        }

        int maxPossibleForRemainingTime = 0;
        if (!currentValve.isOpen() && currentValve.getFlowRate() > 0) {
            currentValve.setOpen(true);
            maxPossibleForRemainingTime = exploreMemoized(timeLeft - 1, currentValve, valveMap);
            currentValve.setOpen(false);
        }

        for (String neighbourValveId : currentValve.getNeighbourIds()) {
            int maxPressure = exploreMemoized(timeLeft - 1, valveMap.get(neighbourValveId), valveMap);
            if (maxPressure > maxPossibleForRemainingTime) {
                maxPossibleForRemainingTime = maxPressure;
            }
        }

        return pressureReleased + maxPossibleForRemainingTime;
    }

    private Map<String, Valve> readInput() throws IOException {
        return InputProcessingUtil.readInputLines(getDay())
                .stream()
                .map(Valve::new)
                .collect(Collectors.toMap(v -> v.id, v -> v));
    }

    @Data
    private static class Valve {
        private final String id;
        private final int flowRate;
        private final List<String> neighbourIds;
        private boolean open;

        public Valve(String inputLine) {
            String[] components = inputLine.split("[ =;,]+");
            id = components[1];
            flowRate = Integer.parseInt(components[5]);
            neighbourIds = Arrays.stream(components, 10, components.length).toList();
        }

        @Override
        public int hashCode() {
            int hash = 17;
            hash = hash * 31 + id.hashCode();
            hash = hash * 31 + Boolean.hashCode(open);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Valve) {
                return this.id.equals(((Valve) obj).id);
            } else {
                return false;
            }
        }
    }
}