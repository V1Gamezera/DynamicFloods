package com.v1gamezera.dynamicfloods.flood;

public class FloodManager {

    private static int floodLevel = 0;
    private static long lastFloodTime = -1;
    private static final int FLOOD_INTERVAL = 200;

    public static int getFloodLevel() {
        return floodLevel;
    }

    public static void increaseFlood(long gameTime) {
        floodLevel++;

        System.out.println("🌊 Nível da enchente agora é: " + floodLevel + " (tick: " + gameTime + ")");
    }

    public static void resetFlood() {
        floodLevel = 0;

        System.out.println("🌊 Enchente resetada!");
    }

    public static void tickFlood(long gameTime) {

        if (lastFloodTime == -1) {
            lastFloodTime = gameTime;
            return;
        }

        if (gameTime - lastFloodTime >= FLOOD_INTERVAL) {
            increaseFlood(gameTime);
            lastFloodTime = gameTime;
        }
    }
}