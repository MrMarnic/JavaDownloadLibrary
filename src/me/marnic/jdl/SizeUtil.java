package me.marnic.jdl;

/**
 * Copyright (c) 12.01.2019
 * Developed by MrMarnic
 * GitHub: https://github.com/MrMarnic
 */
public class SizeUtil {
    public static double toKBFB(int bytes) {
        return ((double) bytes / 1024);
    }

    public static double toMBFB(int bytes) {
        return ((double) bytes / (1024 * 1024));
    }

    public static double toGBFB(int bytes) {
        return ((double) bytes / 1024 * 1024 * 1024);
    }


    public static double toMBFKB(int kb) {
        return ((double) kb / 1024);
    }

    public static double toGBFKB(int kb) {
        return ((double) kb / (1024 * 1024));
    }


    public static double toGBFMB(int mb) {
        return ((double) mb / 1024);
    }

    public static String toHumanReadableFromBytes(int bytes) {
        if (bytes < 0) {
            return "unknown size";
        }

        String[] units = {"B", "KB", "MB", "GB"};
        int unitIndex = 0;
        double value = bytes;

        while (value >= 1024 && unitIndex < units.length - 1) {
            value /= 1024.0;
            unitIndex++;
        }

        String formattedValue = String.format("%.2f", value);
        return formattedValue + " " + units[unitIndex];
    }
}
