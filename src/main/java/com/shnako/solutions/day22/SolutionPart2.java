package com.shnako.solutions.day22;

import com.shnako.util.InputProcessingUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
This solution is not generic - it's customized to my input, as making it generic would've taken far too long.
Furthermore, the shape of the example input is different from the shape of the real input,
so there are 2 sets of variables, one for the real input and one for the example input, commented out.
The set of variables represent:
- FACE_SIZE - the length of the cube edge.
- FACE_START_POINTS - the coordinates of the top left corner of each cube face in the input.
- TRANSITIONS - this details how the cube faces are connected.
                Specifically, it tells us for each cube face, when exiting in each direction,
                which face we will enter and heading in what direction.
                To find this, I've looked at the input and then drew the face numbers and orientation on boxes.
                Then it was fairly simple (though time-consuming) to determine these transitions for each input file.

The other part needed to correctly transition between faces is to know how the coordinates translate between faces.
This is detailed in the switch statement within the move method below.
The boxes I've drawn on came in very handy to determine these.

Once we have all this information, we can navigate the cube faces.
Once we have a final location, we can again use the FACE_START_POINTS coordinates
to determine the coordinates as per the input and thus the result.
 */
@SuppressWarnings({"PointlessArithmeticExpression"})
public class SolutionPart2 {
    private static final int[][] DIRECTIONS = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}}; // R, D, L, U

    private static final int RIGHT = 0;
    private static final int DOWN = 1;
    private static final int LEFT = 2;
    private static final int UP = 3;

    // region REAL INPUT
    private static final int FACE_SIZE = 50;
    private static final List<Pair<Integer, Integer>> FACE_START_POINTS = List.of(
            Pair.of(0 * FACE_SIZE, 1 * FACE_SIZE),
            Pair.of(0 * FACE_SIZE, 2 * FACE_SIZE),
            Pair.of(1 * FACE_SIZE, 1 * FACE_SIZE),
            Pair.of(2 * FACE_SIZE, 0 * FACE_SIZE),
            Pair.of(2 * FACE_SIZE, 1 * FACE_SIZE),
            Pair.of(3 * FACE_SIZE, 0 * FACE_SIZE)
    );

    // Face 1 -> Direction 1 -> (Face 2, Direction 2)
    private static final List<List<Pair<Integer, Integer>>> TRANSITIONS = List.of(
            List.of( // Face 0
                    Pair.of(1, RIGHT), // Right
                    Pair.of(2, DOWN), // Down
                    Pair.of(3, RIGHT), // Left
                    Pair.of(5, RIGHT) // Up
            ),
            List.of( // Face 1
                    Pair.of(4, LEFT), // Right
                    Pair.of(2, LEFT), // Down
                    Pair.of(0, LEFT), // Left
                    Pair.of(5, UP) // Up
            ),
            List.of( // Face 2
                    Pair.of(1, UP), // Right
                    Pair.of(4, DOWN), // Down
                    Pair.of(3, DOWN), // Left
                    Pair.of(0, UP) // Up
            ),
            List.of( // Face 3
                    Pair.of(4, RIGHT), // Right
                    Pair.of(5, DOWN), // Down
                    Pair.of(0, RIGHT), // Left
                    Pair.of(2, RIGHT) // Up
            ),
            List.of( // Face 4
                    Pair.of(1, LEFT), // Right
                    Pair.of(5, LEFT), // Down
                    Pair.of(3, LEFT), // Left
                    Pair.of(2, UP) // Up
            ),
            List.of( // Face 5
                    Pair.of(4, UP), // Right
                    Pair.of(1, DOWN), // Down
                    Pair.of(0, DOWN), // Left
                    Pair.of(3, UP) // Up
            )
    );
    // endregion

////     region EXAMPLE INPUT
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
//    // Face 1 -> Direction 1 -> (Face 2, Direction 2)
//    private static final List<List<Pair<Integer, Integer>>> TRANSITIONS = List.of(
//            List.of( // Face 0
//                    Pair.of(5, LEFT), // Right
//                    Pair.of(3, DOWN), // Down
//                    Pair.of(2, DOWN), // Left
//                    Pair.of(1, DOWN) // Up
//            ),
//            List.of( // Face 1
//                    Pair.of(2, RIGHT), // Right
//                    Pair.of(4, UP), // Down
//                    Pair.of(5, UP), // Left
//                    Pair.of(0, DOWN) // Up
//            ),
//            List.of( // Face 2
//                    Pair.of(3, RIGHT), // Right
//                    Pair.of(4, RIGHT), // Down
//                    Pair.of(1, LEFT), // Left
//                    Pair.of(0, RIGHT) // Up
//            ),
//            List.of( // Face 3
//                    Pair.of(5, DOWN), // Right
//                    Pair.of(4, DOWN), // Down
//                    Pair.of(2, LEFT), // Left
//                    Pair.of(0, UP) // Up
//            ),
//            List.of( // Face 4
//                    Pair.of(5, RIGHT), // Right
//                    Pair.of(1, UP), // Down
//                    Pair.of(2, UP), // Left
//                    Pair.of(3, UP) // Up
//            ),
//            List.of( // Face 5
//                    Pair.of(0, LEFT), // Right
//                    Pair.of(1, RIGHT), // Down
//                    Pair.of(4, LEFT), // Left
//                    Pair.of(3, LEFT) // Up
//            )
//    );
//    // endregion

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
            next.face = TRANSITIONS.get(p.face).get(p.direction).getLeft();
            next.direction = TRANSITIONS.get(p.face).get(p.direction).getRight();

            switch (p.direction) {
                case RIGHT -> { // Exit right
                    switch (next.direction) {
                        case RIGHT -> { // Enter moving right
                            next.x = p.x;
                            next.y = 0;
                        }
                        case DOWN -> { // Enter moving down
                            next.x = 0;
                            next.y = FACE_SIZE - p.x - 1;
                        }
                        case LEFT -> { // Enter moving left
                            next.x = FACE_SIZE - p.x - 1;
                            next.y = FACE_SIZE - 1;
                        }
                        case UP -> { // Enter moving up
                            next.x = FACE_SIZE - 1;
                            next.y = p.x;
                        }
                    }
                }
                case DOWN -> { // Exit down
                    switch (next.direction) {
                        case RIGHT -> { // Enter moving right
                            next.x = FACE_SIZE - p.y - 1;
                            next.y = 0;
                        }
                        case DOWN -> { // Enter moving down
                            next.x = 0;
                            next.y = p.y;
                        }
                        case LEFT -> { // Enter moving left
                            next.x = p.y;
                            next.y = FACE_SIZE - 1;
                        }
                        case UP -> { // Enter moving up
                            next.x = FACE_SIZE - 1;
                            next.y = FACE_SIZE - p.y - 1;
                        }
                    }
                }
                case LEFT -> { // Exit left
                    switch (next.direction) {
                        case RIGHT -> { // Enter moving right
                            next.x = FACE_SIZE - p.x - 1;
                            next.y = 0;
                        }
                        case DOWN -> { // Enter moving down
                            next.x = 0;
                            next.y = p.x;
                        }
                        case LEFT -> { // Enter moving left
                            next.x = p.x;
                            next.y = FACE_SIZE - 1;
                        }
                        case UP -> { // Enter moving up
                            next.x = FACE_SIZE - 1;
                            next.y = FACE_SIZE - p.x - 1;
                        }
                    }
                }
                case UP -> { // Exit up
                    switch (next.direction) {
                        case RIGHT -> { // Enter moving right
                            next.x = p.y;
                            next.y = 0;
                        }
                        case DOWN -> { // Enter moving down
                            next.x = 0;
                            next.y = FACE_SIZE - p.y - 1;
                        }
                        case LEFT -> { // Enter moving left
                            next.x = FACE_SIZE - p.y - 1;
                            next.y = FACE_SIZE - 1;
                        }
                        case UP -> { // Enter moving up
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
