package utils;

import group6.java.group6.utils.TimeUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimeUtilsTest {

    @Test
    public void testFormatTimeDouble() {
        assertEquals("0:00", TimeUtils.formatTime(0.0));
        assertEquals("1:00", TimeUtils.formatTime(60.0));
        assertEquals("1:05", TimeUtils.formatTime(65.0));
        assertEquals("0:59", TimeUtils.formatTime(59.9));
        assertEquals("10:05", TimeUtils.formatTime(605.0));
    }

    @Test
    public void testParseFormattedDuration() {
        assertEquals(209.0, TimeUtils.parseFormattedDuration(3.29), 0.001);
        assertEquals(60.0, TimeUtils.parseFormattedDuration(1.00), 0.001);
        assertEquals(65.0, TimeUtils.parseFormattedDuration(1.05), 0.001);
    }
}
