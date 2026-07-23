package com.v1gamezera.dynamicfloods.flood;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;

public class FloodSimulator {

    private static long lastSimulation = 0;
    private static final int SIMULATION_INTERVAL = 20;

    public static void simulateFlood(Level level, Player player) {

        long gameTime = level.getGameTime();

        if (gameTime - lastSimulation < SIMULATION_INTERVAL) {
            return;
        }

        lastSimulation = gameTime;

        BlockPos playerPos = player.blockPosition();

        int floodLevel = FloodManager.getFloodLevel();

        int radius = 3 + floodLevel;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {

                BlockPos waterPos = playerPos.offset(x, -1, z);

                if (level.getBlockState(waterPos).isAir()) {
                    level.setBlock(waterPos, Blocks.WATER.defaultBlockState(), 3);
                }

            }
        }

        System.out.println("🌊 Simulando enchente nível "
                + floodLevel
                + " perto de: "
                + playerPos);

    }
}