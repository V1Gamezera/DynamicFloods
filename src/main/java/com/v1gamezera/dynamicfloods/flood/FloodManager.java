package com.v1gamezera.dynamicfloods.flood;

public class FloodManager {

    private static int floodLevel = 0;

    public static int getFloodLevel() {
        return floodLevel;
    }

    public static void increaseFlood() {
        floodLevel++;

        System.out.println("🌊 Nível da enchente: " + floodLevel);
    }

    public static void resetFlood() {
        floodLevel = 0;

        System.out.println("🌊 Enchente resetada!");
    }
}