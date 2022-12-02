package com.shnako.solutions.day02;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;

import java.io.IOException;
import java.security.InvalidParameterException;

/*
I'm using a RockPaperScissor class that has 3 parameters:
- elfChoice
- myChoice
- result
Each of these contains a value of 1, 2 or 3 (1 - rock / lose,  2 - paper / draw, 3 - scissors / win).
In the first part we know the elfChoice and myChoice, and need to determine the result.
In the second part we know the elfChoice and the result, and need to determine myChoice.
Once we have all 3, we can determine the points for that round.
 */

public class Solution extends SolutionBase {
    @Override
    public String runPart1() throws IOException {
        return runPart(1);
    }

    @Override
    public String runPart2() throws IOException {
        return runPart(2);
    }

    private String runPart(int part) throws IOException {
        return (Integer) InputProcessingUtil.readInputLines(getDay())
                .stream()
                .map(line -> new RockPaperScissor(line, part))
                .map(RockPaperScissor::getPoints)
                .mapToInt(Integer::intValue)
                .sum() + "";
    }

    private static class RockPaperScissor {
        private final int elfChoice;
        private final int myChoice;
        private final int result;

        public RockPaperScissor(String round, int part) {
            String[] choices = round.split(" ");
            elfChoice = mapChoiceToInt(choices[0]);
            int secondValue = mapChoiceToInt(choices[1]);

            if (part == 1) {
                myChoice = secondValue;
                result = determineResult();
            } else {
                result = secondValue;
                myChoice = determineMyChoice();
            }
        }

        private int mapChoiceToInt(String choice) {
            return switch (choice) {
                case "A", "X" -> 1;
                case "B", "Y" -> 2;
                case "C", "Z" -> 3;
                default -> throw new InvalidParameterException("Unknown RPS choice.");
            };
        }

        private int determineResult() {
            if (elfChoice == myChoice) {
                return 2;
            }
            if (elfChoice == 1 && myChoice == 3) {
                return 1;
            }
            if (elfChoice < myChoice || (elfChoice == 3 && myChoice == 1)) {
                return 3;
            }
            return 1;
        }

        private int determineMyChoice() {
            return switch (result) {
                case 1 -> switch (elfChoice) {
                    case 1 -> 3;
                    case 2 -> 1;
                    case 3 -> 2;
                    default -> throw new InvalidParameterException("Unknown RPS choice.");
                };
                case 2 -> elfChoice;
                case 3 -> switch (elfChoice) {
                    case 1 -> 2;
                    case 2 -> 3;
                    case 3 -> 1;
                    default -> throw new InvalidParameterException("Unknown RPS choice.");
                };
                default -> throw new InvalidParameterException("Unknown expected result.");
            };
        }

        private int getPoints() {
            return myChoice + switch (result) {
                case 1 -> 0;
                case 2 -> 3;
                case 3 -> 6;
                default -> throw new InvalidParameterException("Unknown result.");
            };
        }
    }
}