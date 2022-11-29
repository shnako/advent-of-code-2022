package com.shnako.solutions.day00;

import com.shnako.SolutionBaseTest;
import com.shnako.solutions.SolutionBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SolutionTest extends SolutionBaseTest {
    private final SolutionBase solution = new Solution();

    @Test
    public void testPart1() {
        assertEquals("123", solution.runPart1());
    }

    @Test
    public void testPart2() {
        assertEquals("456", solution.runPart2());
    }
}
