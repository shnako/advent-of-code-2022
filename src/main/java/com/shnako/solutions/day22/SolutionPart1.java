package com.shnako.solutions.day22;

import com.shnako.util.InputProcessingUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class SolutionPart1 {
    private static final int[][] DIRECTIONS = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

    public static String runPart1(String day) throws IOException {
        List<String> components = InputProcessingUtil.readInputLines(day);
        char[][] map = readInputBoardMap(components.subList(0, components.size() - 2));
        List<Pair<Integer, Character>> path = readInputPath(components.get(components.size() - 1));

        Triple<Integer, Integer, Integer> initialPosition = getInitialPosition(map);
        Triple<Integer, Integer, Integer> finalPosition = navigate(initialPosition, map, path);


        return 1000 * (finalPosition.getLeft() + 1) + 4 * (finalPosition.getMiddle() + 1) + finalPosition.getRight() + "";
    }

    private static Triple<Integer, Integer, Integer> getInitialPosition(char[][] map) {
        int startingY;
        for (startingY = 0; ; startingY++) {
            if (map[0][startingY] == '.') {
                break;
            }
        }
        return Triple.of(0, startingY, 0);
    }

    private static Triple<Integer, Integer, Integer> navigate(Triple<Integer, Integer, Integer> initialPosition, char[][] map, List<Pair<Integer, Character>> path) {
        int x = initialPosition.getLeft();
        int y = initialPosition.getMiddle();
        int direction = initialPosition.getRight();

        for (Pair<Integer, Character> movement : path) {
            if (movement.getRight() != null) {
                if (movement.getRight() == 'R') {
                    direction = (direction + 1) % DIRECTIONS.length;
                } else if (movement.getRight() == 'L') {
                    direction = (direction - 1 + DIRECTIONS.length) % DIRECTIONS.length;
                }
            } else {
                for (int i = 0; i < movement.getLeft(); i++) {
                    int nextX = x + DIRECTIONS[direction][0];
                    int nextY = y + DIRECTIONS[direction][1];

                    if (direction == 0) { // Right
                        while (nextY > map[nextX].length - 1 || map[nextX][nextY] == ' ') {
                            nextY++;
                            if (nextY > map[nextX].length - 1) {
                                nextY = 0;
                            }
                        }
                    }

                    if (direction == 1) { // Down
                        while (nextX > map.length - 1 || map[nextX][nextY] == ' ') {
                            nextX++;
                            if (nextX > map.length - 1) {
                                nextX = 0;
                            }
                        }
                    }


                    if (direction == 2) { // Left
                        while (nextY < 0 || map[nextX][nextY] == ' ') {
                            nextY--;
                            if (nextY < 0) {
                                nextY = map[nextX].length - 1;
                            }
                        }
                    }

                    if (direction == 3) { // Up
                        while (nextX < 0 || map[nextX][nextY] == ' ') {
                            nextX--;
                            if (nextX < 0) {
                                nextX = map.length - 1;
                            }
                        }
                    }

                    if (map[nextX][nextY] == '#') {
                        break;
                    }

                    x = nextX;
                    y = nextY;
                }
            }
        }

        return Triple.of(x, y, direction);
    }

    private static char[][] readInputBoardMap(List<String> inputLines) {
        int maxWidth = inputLines
                .stream()
                .map(String::length)
                .mapToInt(Integer::intValue)
                .max()
                .getAsInt();

        char[][] map = new char[inputLines.size()][maxWidth];

        for (int x = 0; x < inputLines.size(); x++) {
            for (int y = 0; y < inputLines.get(x).length() || y < maxWidth; y++) {
                if (y < inputLines.get(x).length()) {
                    map[x][y] = inputLines.get(x).charAt(y);
                } else {
                    map[x][y] = ' ';
                }
            }
        }

        return map;
    }

    @SuppressWarnings("DuplicatedCode")
    private static List<Pair<Integer, Character>> readInputPath(String inputPath) {
        List<Pair<Integer, Character>> path = new ArrayList<>();

        int number = 0;
        for (int i = 0; i < inputPath.length(); i++) {
            if (inputPath.charAt(i) == 'L' || inputPath.charAt(i) == 'R') {
                path.add(Pair.of(number, null));
                path.add(Pair.of(null, inputPath.charAt(i)));
                number = 0;
            } else {
                number = 10 * number + (inputPath.charAt(i) - '0');
            }
        }
        path.add(Pair.of(number, null));

        return path;
    }
}
