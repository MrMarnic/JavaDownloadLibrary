package me.marnic.jdl;

/**
 * Copyright (c) 12.01.2019
 * Developed by MrMarnic
 * GitHub: https://github.com/MrMarnic
 */
public class SizeUtil {
    public static double toKBFB(int bytes) {
        return ((double)bytes/1000);
    }

    public static double toMBFB(int bytes) {
        return ((double)bytes/1000000);
    }

    public static double toGBFB(int bytes) {
        return ((double)bytes/1000000000);
    }



    public static double toMBFKB(int kb) {
        return ((double)kb/1000);
    }

    public static double toGBFKB(int kb) {
        return ((double)kb/1000000);
    }



    public static double toGBFMB(int mb) {
        return ((double)mb/1000);
    }
}
