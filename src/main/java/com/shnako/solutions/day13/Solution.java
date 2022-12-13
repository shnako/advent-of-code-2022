package com.shnako.solutions.day13;

import com.shnako.solutions.SolutionBase;
import com.shnako.util.InputProcessingUtil;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
 For this solution, we use the Sequence object to represent and traverse each sequence.
 This object contains either a list of sub-sequences, an integer or nothing.
 We read and compare these objects recursively.

 For part 1, we implement the Comparable interface to recursively compare the pairs.
 The result is the sum of each pair index where the left pair < the right pair.

 For part 2, we extract the list of sequences for the pairs and then insert the divider packets into it.
 We implement the Comparator interface to sort the sequences.
 The result is the multiplication of the indices of the divider packages.
 */
public class Solution extends SolutionBase {
    @Override
    public String runPart1() throws IOException {
        List<Pair<Sequence, Sequence>> packetPairs = readInput();

        int sum = 0;
        for (int i = 0; i < packetPairs.size(); i++) {
            if (packetPairs.get(i).getLeft().compareTo(packetPairs.get(i).getRight()) < 0) {
                sum += i + 1;
            }
        }

        return sum + "";
    }

    @Override
    public String runPart2() throws IOException {
        List<Pair<Sequence, Sequence>> packetPairs = readInput();

        var packets = new ArrayList<Sequence>();
        for (var pair : packetPairs) {
            packets.add(pair.getLeft());
            packets.add(pair.getRight());
        }
        packets.add(new Sequence("[[2]]", true));
        packets.add(new Sequence("[[6]]", true));

        Collections.sort(packets);

        int result = 1;
        for (int i = 0; i < packets.size(); i++) {
            if (packets.get(i).isDivider()) {
                result *= i + 1;
            }
        }

        return result + "";
    }

    private int findMatchingClosingIndex(String packet, int openingIndex) {
        int openBrackets = 0;
        for (int i = openingIndex + 1; i < packet.length(); i++) {
            switch (packet.charAt(i)) {
                case '[' -> openBrackets++;
                case ']' -> {
                    if (openBrackets == 0) {
                        return i;
                    }
                    openBrackets--;
                }
            }
        }
        throw new RuntimeException("Could not find the closing bracket for " + openingIndex + " in " + packet);
    }

    private List<Pair<Sequence, Sequence>> readInput() throws IOException {
        List<String> packets = InputProcessingUtil.readInputLines(getDay());

        List<Pair<Sequence, Sequence>> pairs = new ArrayList<>();
        for (int i = 0; i < packets.size(); i += 3) {
            var left = new Sequence(packets.get(i));
            var right = new Sequence(packets.get(i + 1));
            pairs.add(Pair.of(left, right));
        }

        return pairs;
    }

    @Data
    private class Sequence implements Comparable<Sequence>, Comparator<Sequence> {
        private Integer integer;

        private List<Sequence> list;

        private boolean isDivider;

        private Sequence(int integer) {
            this.integer = integer;
        }

        private Sequence(List<Sequence> list) {
            this.list = list;
        }

        private Sequence(String line, boolean isDivider) {
            this(line);
            this.isDivider = isDivider;
        }

        private Sequence(String line) {
            Integer integer = null;
            list = new ArrayList<>();
            for (int i = 0; i < line.length(); i++) {
                switch (line.charAt(i)) {
                    case ',' -> {
                        if (integer != null) {
                            list.add(new Sequence(integer));
                            integer = null;
                        }
                    }
                    case '[' -> {
                        int matchingClosingBracketIndex = findMatchingClosingIndex(line, i);
                        list.add(new Sequence(line.substring(i + 1, matchingClosingBracketIndex)));
                        i = matchingClosingBracketIndex;
                    }
                    default -> {
                        if (integer == null) {
                            integer = 0;
                        }
                        integer = 10 * integer + (line.charAt(i) - '0');
                    }
                }
            }
            if (integer != null) {
                list.add(new Sequence(integer));
            }
        }

        private boolean isInteger() {
            return integer != null;
        }

        private boolean isList() {
            return list != null;
        }

        @Override
        public int compareTo(@NotNull Solution.Sequence other) {
            return compare(this, other);
        }

        @Override
        public int compare(Sequence left, Sequence right) {
            if (!left.isList() && right.isList()) {
                return compare(new Sequence(List.of(left)), right);
            }
            if (left.isList() && !right.isList()) {
                return compare(left, new Sequence(List.of(right)));
            }
            if (left.isInteger() && right.isInteger()) {
                if (left.getInteger().equals(right.getInteger())) {
                    return 0;
                }
                return left.getInteger() < right.getInteger() ? -1 : 1;
            }
            if (left.isList() && right.isList()) {
                for (int i = 0; i < left.getList().size() && i < right.getList().size(); i++) {
                    int comparisonResult = compare(left.getList().get(i), right.getList().get(i));
                    if (comparisonResult != 0) {
                        return comparisonResult;
                    }
                }
                if (left.getList().size() == right.getList().size()) {
                    return 0;
                }
                return left.getList().size() < right.getList().size() ? -1 : 1;
            }
            throw new RuntimeException("Could not compare " + left + " and " + right);
        }
    }
}