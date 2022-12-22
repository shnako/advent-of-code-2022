package com.shnako.solutions.day22;

import com.shnako.solutions.SolutionBase;

import java.io.IOException;

public class Solution extends SolutionBase {
    @Override
    public String runPart1() throws IOException {
        return SolutionPart1.runPart1(getDay());
    }

    @Override
    public String runPart2() throws IOException {
        return SolutionPart2.run(getDay());
    }
}