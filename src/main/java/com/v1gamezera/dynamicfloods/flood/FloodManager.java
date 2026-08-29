package com.v1gamezera.dynamicfloods.flood;

import net.minecraft.core.BlockPos;
import com.v1gamezera.dynamicfloods.config.DynamicFloodsConfig;
import net.minecraft.world.level.Level;

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

        WoodDamageManager.reset();

        System.out.println("🌊 Estado da enchente resetado!");
    }

    public static void tickFlood(Level level) {

        System.out.println("🌧️ FloodManager rodando!");

        long gameTime = level.getGameTime();

        if (!level.players().isEmpty()) {

            BlockPos playerPos = level.players().get(0).blockPosition();

            BlockPos water = WaterSourceFinder.findWater(level, playerPos, 128);

            if (water != null) {

                System.out.println("🌊 Água encontrada em: " + water);

                int waterSize = WaterAnalyzer.countWaterBlocks(level, water);

                System.out.println("🌊 Tamanho do corpo d'água: " + waterSize + " blocos");

                int calculatedLevel = FloodCalculator.calculateFloodLevel(waterSize);

                System.out.println("🌊 Força calculada da enchente: nível " + calculatedLevel);

                setFloodOrigin(water);

            } else {

                System.out.println("❌ Nenhuma água encontrada");
            }
        }

        // Sistema de detecção e deterioração da madeira
        if (floodOrigin != null) {

            WoodDamageManager.tick(
                    level,
                    floodOrigin,
                    15
            );
        }
    }
}