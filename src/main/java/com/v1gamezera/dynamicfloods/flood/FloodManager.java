package com.v1gamezera.dynamicfloods.flood;

import net.minecraft.core.BlockPos;
import com.v1gamezera.dynamicfloods.config.DynamicFloodsConfig;

public class FloodManager {

    private static int floodLevel = 0;
    private static BlockPos floodOrigin;
    private static long lastFloodTime = -1;

    public static int getFloodLevel() {
        return floodLevel;
    }
    public static String getFloodStage() {

        if (floodLevel <= 0) {
            return "Sem enchente";
        }

        if (floodLevel <= 3) {
            return "Pequena enchente";
        }

        if (floodLevel <= 7) {
            return "Enchente moderada";
        }

        return "Grande enchente";
    }
    public static void setFloodOrigin(BlockPos pos) {
        floodOrigin = pos;

        System.out.println("🌊 Origem da enchente definida em: " + pos);
    }

    public static BlockPos getFloodOrigin() {
        return floodOrigin;
    }

    public static void increaseFlood(long gameTime) {
        floodLevel++;

        System.out.println("🌊 Nível da enchente: " + floodLevel
                + " | Estágio: " + getFloodStage()
                + " (tick: " + gameTime + ")");
    }

    public static void resetFlood() {

        floodLevel = 0;
        floodOrigin = null;
        lastFloodTime = -1;

        System.out.println("🌊 Estado da enchente resetado!");
    }

    public static void tickFlood(long gameTime) {

        if (lastFloodTime == -1) {
            lastFloodTime = gameTime;
            return;
        }

        if (gameTime - lastFloodTime >= DynamicFloodsConfig.FLOOD_INTERVAL.get()) {
            increaseFlood(gameTime);
            lastFloodTime = gameTime;
        }
    }
}