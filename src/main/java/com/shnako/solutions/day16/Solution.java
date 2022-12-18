package com.shnako.solutions.day16;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;
import lombok.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class Solution extends SolutionBase {
    private List<Valve> valves;
    private List<Integer> functionalValveIds;

    private int[][] distances;

    @Override
    public String runPart1() throws IOException {
        valves = readInput();
        functionalValveIds = valves
                .stream()
                .filter(v -> v.getFlowRate() > 0)
                .map(Valve::getId)
                .collect(Collectors.toList());
        distances = floydWarshall(valves);
        int pressure = explore(0, 30, new ArrayList<>());
        return pressure + "";
    }

    private int explore(int myValveId, int timeRemaining, List<Integer> openValveIds) {
        int thisPressure = openValveIds
                .stream()
                .map(v -> valves.get(v).getFlowRate())
                .mapToInt(Integer::intValue)
                .sum();

        int maxSubPressure = thisPressure * timeRemaining;
        for (int vId : functionalValveIds) {
            int movesToOpen = distances[myValveId][vId] + 1;
            if (timeRemaining >= movesToOpen && !openValveIds.contains(vId)) {
                openValveIds.add(vId);
                int subPressure = explore(vId, timeRemaining - movesToOpen, openValveIds) + thisPressure * movesToOpen;
                if (subPressure > maxSubPressure) {
                    maxSubPressure = subPressure;
                }
                openValveIds.remove(openValveIds.size() - 1);
            }
        }

        return maxSubPressure;
    }

    private int[][] floydWarshall(List<Valve> valves) {
        int[][] dist = new int[valves.size()][valves.size()];
        for (int[] line : dist) {
            Arrays.fill(line, 1000);
        }
        for (Valve v : valves) {
            dist[v.getId()][v.getId()] = 0;
            for (int neighbourId : v.getNeighbourIds()) {
                dist[v.getId()][neighbourId] = 1;
            }
        }
        for (int k = 0; k < valves.size(); k++) {
            for (int i = 0; i < valves.size(); i++) {
                for (int j = 0; j < valves.size(); j++) {
                    if (dist[i][j] > dist[i][k] + dist[k][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }

        return dist;
    }

    @Override
    public String runPart2() {
        return "456";
    }

    private List<Valve> readInput() throws IOException {
        List<Valve> valves = InputProcessingUtil.readInputLines(getDay())
                .stream()
                .sorted()
                .map(Valve::new)
                .toList();
        for (int i = 0; i < valves.size(); i++) {
            Valve v = valves.get(i);
            v.setId(i);
            for (String neighbourName : v.getNeighbours()) {
                v.getNeighbourIds().add(
                        valves.indexOf(
                                valves
                                        .stream()
                                        .filter(nv -> nv.getName().equals(neighbourName))
                                        .findFirst()
                                        .get()));
            }
        }
        return valves;
    }

    @Data
    private static class Valve {
        private int id;
        private String name;
        private int flowRate;
        private List<String> neighbours;
        private List<Integer> neighbourIds;

        private Valve(String input) {
            String[] components = input.split("[ =;,]+");
            name = components[1];
            flowRate = Integer.parseInt(components[5]);
            neighbours = Arrays.stream(components, 10, components.length).toList();
            neighbourIds = new ArrayList<>();
        }
    }
}