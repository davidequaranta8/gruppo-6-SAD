package group6.java.group6.utils;

public class TimeUtils {

    public static String formatTime(double seconds){
            int m = (int) seconds / 60;
            int s = (int) seconds % 60;
            return String.format("%d:%02d", m, s);
        }

    public static double parseFormattedDuration(double formattedDuration) {
        int minutes = (int) formattedDuration;
        int seconds = (int) Math.round((formattedDuration - minutes) * 100);
        return minutes * 60 + seconds;
    }
    }

