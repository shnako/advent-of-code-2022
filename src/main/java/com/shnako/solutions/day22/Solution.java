package com.shnako.solutions.day22;

import com.shnako.solutions.SolutionBase;

import java.io.IOException;

/*
This problem is complex and there are significant differences between part 1 and part 2,
so I've split them into separate files called from here. Have a look at them for the solutions and explanations.
 */
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