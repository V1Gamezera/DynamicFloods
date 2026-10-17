package com.v1gamezera.dynamicfloods.flood;

import com.v1gamezera.dynamicfloods.config.DynamicFloodsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class FloodManager {

    private static int floodLevel = 0;
    private static BlockPos floodOrigin;
    private static long lastFloodTime = -1;

    // Controle da frequência das buscas por água
    private static long lastWaterSearchTime = -1;
    private static final long WATER_SEARCH_INTERVAL = 100;

    // Nível estimado na última busca
    private static int targetFloodLevel = 0;

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
        floodOrigin = pos.immutable();

        System.out.println(
                "🌊 Origem da enchente definida em: " + floodOrigin
        );
    }

    public static BlockPos getFloodOrigin() {
        return floodOrigin;
    }

    public static void increaseFlood(long gameTime) {

        floodLevel++;

        System.out.println(
                "🌊 Nível da enchente: " + floodLevel
                        + " | Estágio: " + getFloodStage()
                        + " (tick: " + gameTime + ")"
        );
    }

    public static void resetFlood() {

        floodLevel = 0;
        floodOrigin = null;
        lastFloodTime = -1;
        lastWaterSearchTime = -1;
        targetFloodLevel = 0;

        WoodDamageManager.reset();

        System.out.println("🌊 Estado da enchente resetado!");
    }

    public static void tickFlood(Level level) {

        long gameTime = level.getGameTime();

        if (!level.players().isEmpty()) {

            // Procura água somente a cada 100 ticks
            boolean shouldSearchWater =
                    lastWaterSearchTime < 0
                            || gameTime - lastWaterSearchTime
                            >= WATER_SEARCH_INTERVAL;

            if (shouldSearchWater) {

                lastWaterSearchTime = gameTime;

                BlockPos playerPos =
                        level.players().get(0).blockPosition();

                BlockPos water =
                        WaterSourceFinder.findWater(
                                level,
                                playerPos,
                                128
                        );

                if (water != null) {

                    System.out.println(
                            "🌊 Água encontrada em: " + water
                    );

                    int waterSize =
                            WaterAnalyzer.countWaterBlocks(
                                    level,
                                    water
                            );

                    System.out.println(
                            "🌊 Tamanho do corpo d'água: "
                                    + waterSize + " blocos"
                    );

                    int calculatedLevel =
                            FloodCalculator.calculateFloodLevel(
                                    waterSize
                            );

                    targetFloodLevel = Math.min(
                            calculatedLevel,
                            DynamicFloodsConfig.MAX_FLOOD_LEVEL.get()
                    );

                    System.out.println(
                            "🌊 Força calculada da enchente: nível "
                                    + targetFloodLevel
                    );

                    if (floodOrigin == null) {

                        setFloodOrigin(water);
                        lastFloodTime = gameTime;

                        System.out.println(
                                "⏱️ Temporizador de crescimento iniciado!"
                        );

                    } else if (lastFloodTime < 0) {

                        lastFloodTime = gameTime;

                        System.out.println(
                                "⏱️ Temporizador de crescimento iniciado!"
                        );
                    }

                } else {

                    System.out.println(
                            "❌ Nenhuma água encontrada"
                    );

                    targetFloodLevel = 0;
                    lastFloodTime = -1;
                }
            }

            // O temporizador é verificado sem procurar água novamente
            if (targetFloodLevel > floodLevel
                    && lastFloodTime >= 0
                    && gameTime - lastFloodTime
                    >= DynamicFloodsConfig.FLOOD_INTERVAL.get()) {

                increaseFlood(gameTime);
                lastFloodTime = gameTime;
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