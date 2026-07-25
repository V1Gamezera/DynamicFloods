package com.v1gamezera.dynamicfloods.flood;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class WaterAnalyzer {

    public static int countWaterBlocks(Level level, BlockPos start) {

        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();

        queue.add(start);
        visited.add(start);

        int waterCount = 0;

        while (!queue.isEmpty()) {

            BlockPos current = queue.poll();

            if (!isWater(level, current)) {
                continue;
            }

            waterCount++;

            for (BlockPos neighbor : getNeighbors(current)) {

                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        return waterCount;
    }


    private static boolean isWater(Level level, BlockPos pos) {
        return level.getBlockState(pos).is(Blocks.WATER);
    }


    private static BlockPos[] getNeighbors(BlockPos pos) {

        return new BlockPos[] {
                pos.north(),
                pos.south(),
                pos.east(),
                pos.west()
        };
    }
}