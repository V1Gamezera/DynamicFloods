package com.v1gamezera.dynamicfloods.flood;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class FloodSimulator {

    private static long lastSimulation = 0;
    private static final int SIMULATION_INTERVAL = 20;

    public static BlockPos findGround(Level level, BlockPos pos) {

        BlockPos current = pos;

        while (current.getY() > level.getMinBuildHeight()) {

            if (!level.getBlockState(current.below()).isAir()) {
                return current;
            }

            current = current.below();
        }

        return pos;
    }

    public static BlockPos findNearestWater(Level level, BlockPos center, int radius) {

        RandomSource random = level.getRandom();

        for (int i = 0; i < 500; i++) {

            int x = center.getX() + random.nextInt(radius * 2 + 1) - radius;
            int z = center.getZ() + random.nextInt(radius * 2 + 1) - radius;

            BlockPos pos = findGround(level, new BlockPos(x, center.getY(), z));

            BlockState state = level.getBlockState(pos.below());

            if (state.is(Blocks.WATER)) {
                return pos;
            }
        }

        return null;
    }

    public static void simulateFlood(Level level, Player player) {


        long gameTime = level.getGameTime();

        if (gameTime - lastSimulation < SIMULATION_INTERVAL) {
            return;
        }

        lastSimulation = gameTime;

        BlockPos floodOrigin = FloodManager.getFloodOrigin();

        if (floodOrigin == null) {
            return;
        }

        int floodLevel = FloodManager.getFloodLevel();

        if (floodLevel <= 0) {
            return;
        }

        int radius = 1 + floodLevel;
        int height = floodLevel / 3;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {

                if ((x * x) + (z * z) > (radius * radius)) {
                    continue;
                }

                for (int y = 0; y <= height; y++) {

                    BlockPos waterPos = floodOrigin.offset(x, y, z);

                    if (level.getBlockState(waterPos).isAir()) {
                        level.setBlock(waterPos, Blocks.WATER.defaultBlockState(), 3);
                    }

                }

            }
        }
    }
}