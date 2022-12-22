package com.shnako.solutions.day22;

import com.shnako.util.InputProcessingUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"PointlessArithmeticExpression"})
public class SolutionPart2 {
    private static final int[][] DIRECTIONS = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}}; // R, D, L, U

//    // region EXAMPLE
//    private static final int FACE_SIZE = 4;
//    private static final List<Pair<Integer, Integer>> FACE_START_POINTS = List.of(
//            Pair.of(0 * FACE_SIZE, 2 * FACE_SIZE),
//            Pair.of(1 * FACE_SIZE, 0 * FACE_SIZE),
//            Pair.of(1 * FACE_SIZE, 1 * FACE_SIZE),
//            Pair.of(1 * FACE_SIZE, 2 * FACE_SIZE),
//            Pair.of(2 * FACE_SIZE, 2 * FACE_SIZE),
//            Pair.of(2 * FACE_SIZE, 3 * FACE_SIZE)
//    );
//
//    // F1 -> DIR1 -> (F2, DIR2)
//    private static final List<List<Pair<Integer, Integer>>> TRANSLATIONS = List.of(
//            List.of( // Face 0
//                    Pair.of(5, 2), // Right
//                    Pair.of(3, 1), // Down
//                    Pair.of(2, 1), // Left
//                    Pair.of(1, 1) // Up
//            ),
//            List.of( // Face 1
//                    Pair.of(2, 0), // Right
//                    Pair.of(4, 3), // Down
//                    Pair.of(5, 3), // Left
//                    Pair.of(0, 1) // Up
//            ),
//            List.of( // Face 2
//                    Pair.of(3, 0), // Right
//                    Pair.of(4, 0), // Down
//                    Pair.of(1, 2), // Left
//                    Pair.of(0, 0) // Up
//            ),
//            List.of( // Face 3
//                    Pair.of(5, 1), // Right
//                    Pair.of(4, 1), // Down
//                    Pair.of(2, 2), // Left
//                    Pair.of(0, 3) // Up
//            ),
//            List.of( // Face 4
//                    Pair.of(5, 0), // Right
//                    Pair.of(1, 3), // Down
//                    Pair.of(2, 3), // Left
//                    Pair.of(3, 3) // Up
//            ),
//            List.of( // Face 5
//                    Pair.of(0, 2), // Right
//                    Pair.of(1, 0), // Down
//                    Pair.of(4, 2), // Left
//                    Pair.of(3, 2) // Up
//            )
//    );
//    // endregion

    // region INPUT
    private static final int FACE_SIZE = 50;
    private static final List<Pair<Integer, Integer>> FACE_START_POINTS = List.of(
            Pair.of(0 * FACE_SIZE, 1 * FACE_SIZE),
            Pair.of(0 * FACE_SIZE, 2 * FACE_SIZE),
            Pair.of(1 * FACE_SIZE, 1 * FACE_SIZE),
            Pair.of(2 * FACE_SIZE, 0 * FACE_SIZE),
            Pair.of(2 * FACE_SIZE, 1 * FACE_SIZE),
            Pair.of(3 * FACE_SIZE, 0 * FACE_SIZE)
    );

    // F1 -> DIR1 -> (F2, DIR2)
    private static final List<List<Pair<Integer, Integer>>> TRANSLATIONS = List.of(
            List.of( // Face 0
                    Pair.of(1, 0), // Right
                    Pair.of(2, 1), // Down
                    Pair.of(3, 0), // Left
                    Pair.of(5, 0) // Up
            ),
            List.of( // Face 1
                    Pair.of(4, 2), // Right
                    Pair.of(2, 2), // Down
                    Pair.of(0, 2), // Left
                    Pair.of(5, 3) // Up
            ),
            List.of( // Face 2
                    Pair.of(1, 3), // Right
                    Pair.of(4, 1), // Down
                    Pair.of(3, 1), // Left
                    Pair.of(0, 3) // Up
            ),
            List.of( // Face 3
                    Pair.of(4, 0), // Right
                    Pair.of(5, 1), // Down
                    Pair.of(0, 0), // Left
                    Pair.of(2, 0) // Up
            ),
            List.of( // Face 4
                    Pair.of(1, 2), // Right
                    Pair.of(5, 2), // Down
                    Pair.of(3, 2), // Left
                    Pair.of(2, 3) // Up
            ),
            List.of( // Face 5
                    Pair.of(4, 3), // Right
                    Pair.of(1, 1), // Down
                    Pair.of(0, 1), // Left
                    Pair.of(3, 3) // Up
            )
    );
    // endregion

    public static String run(String day) throws IOException {
        List<String> components = InputProcessingUtil.readInputLines(day);
        List<char[][]> faces = readInputFaces(components.subList(0, components.size() - 2));
        List<Pair<Integer, Character>> path = readInputPath(components.get(components.size() - 1));

        Position position = getInitialPosition(faces.get(0));
        for (Pair<Integer, Character> movement : path) {
            if (movement.getRight() != null) {
                if (movement.getRight() == 'R') {
                    position.direction = (position.direction + 1) % DIRECTIONS.length;
                } else if (movement.getRight() == 'L') {
                    position.direction = (position.direction - 1 + DIRECTIONS.length) % DIRECTIONS.length;
                }
            } else {
                for (int i = 0; i < movement.getLeft(); i++) {
                    boolean hasMoved = move(position, faces);
                    if (!hasMoved) {
                        break;
                    }
                }
            }
        }

        return 1000 * (FACE_START_POINTS.get(position.getFace()).getLeft() + position.getX() + 1)
                + 4 * (FACE_START_POINTS.get(position.getFace()).getRight() + position.getY() + 1)
                + position.getDirection() + "";
    }

    private static Position getInitialPosition(char[][] map) {
        int startingY;
        for (startingY = 0; ; startingY++) {
            if (map[0][startingY] == '.') {
                break;
            }
        }
        return new Position(0, 0, startingY, 0);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private static boolean move(Position p, List<char[][]> faces) {
        Position next = Position.builder()
                .face(p.face)
                .direction(p.direction)
                .x(p.x + DIRECTIONS[p.direction][0])
                .y(p.y + DIRECTIONS[p.direction][1])
                .build();

        if (next.x < 0 || next.y < 0 || next.x > FACE_SIZE - 1 || next.y > FACE_SIZE - 1) {
            next.face = TRANSLATIONS.get(p.face).get(p.direction).getLeft();
            next.direction = TRANSLATIONS.get(p.face).get(p.direction).getRight();

            switch (p.direction) {
                case 0 -> { // Exit right
                    switch (next.direction) {
                        case 0 -> { // Enter moving right
                            next.x = p.x;
                            next.y = 0;
                        }
                        case 1 -> { // Enter moving down
                            next.x = 0;
                            next.y = FACE_SIZE - p.x - 1;
                        }
                        case 2 -> { // Enter moving left
                            next.x = FACE_SIZE - p.x - 1;
                            next.y = FACE_SIZE - 1;
                        }
                        case 3 -> { // Enter moving up
                            next.x = FACE_SIZE - 1;
                            next.y = p.x;
                        }
                    }
                }
                case 1 -> { // Exit down
                    switch (next.direction) {
                        case 0 -> { // Enter moving right
                            next.x = FACE_SIZE - p.y - 1;
                            next.y = 0;
                        }
                        case 1 -> { // Enter moving down
                            next.x = 0;
                            next.y = p.y;
                        }
                        case 2 -> { // Enter moving left
                            next.x = p.y;
                            next.y = FACE_SIZE - 1;
                        }
                        case 3 -> { // Enter moving up
                            next.x = FACE_SIZE - 1;
                            next.y = FACE_SIZE - p.y - 1;
                        }
                    }
                }
                case 2 -> { // Exit left
                    switch (next.direction) {
                        case 0 -> { // Enter moving right
                            next.x = FACE_SIZE - p.x - 1;
                            next.y = 0;
                        }
                        case 1 -> { // Enter moving down
                            next.x = 0;
                            next.y = p.x;
                        }
                        case 2 -> { // Enter moving left
                            next.x = p.x;
                            next.y = FACE_SIZE - 1;
                        }
                        case 3 -> { // Enter moving up
                            next.x = FACE_SIZE - 1;
                            next.y = FACE_SIZE - p.x - 1;
                        }
                    }
                }
                case 3 -> { // Exit up
                    switch (next.direction) {
                        case 0 -> { // Enter moving right
                            next.x = p.y;
                            next.y = 0;
                        }
                        case 1 -> { // Enter moving down
                            next.x = 0;
                            next.y = FACE_SIZE - p.y - 1;
                        }
                        case 2 -> { // Enter moving left
                            next.x = FACE_SIZE - p.y - 1;
                            next.y = FACE_SIZE - 1;
                        }
                        case 3 -> { // Enter moving up
                            next.x = FACE_SIZE - 1;
                            next.y = p.y;
                        }
                    }
                }
            }
        }

        if (faces.get(next.face)[next.x][next.y] == '#') {
            return false;
        }

        p.setFace(next.face);
        p.setX(next.x);
        p.setY(next.y);
        p.setDirection(next.direction);

        return true;
    }

    private static List<char[][]> readInputFaces(List<String> inputLines) {
        List<char[][]> faces = new ArrayList<>();
        for (int face = 0; face < 6; face++) {
            faces.add(new char[FACE_SIZE][FACE_SIZE]);
            for (int x = 0; x < FACE_SIZE; x++) {
                for (int y = 0; y < FACE_SIZE; y++) {
                    faces.get(face)[x][y] = inputLines
                            .get(FACE_START_POINTS.get(face).getLeft() + x)
                            .charAt(FACE_START_POINTS.get(face).getRight() + y);
                }
            }
        }

        return faces;
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

    @Data
    @AllArgsConstructor
    @Builder
    private static class Position {
        private int face;
        private int x;
        private int y;
        private int direction;
    }
}
