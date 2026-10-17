
package com.v1gamezera.dynamicfloods.flood;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class WaterAnalyzer {

    // Não precisamos contar mais de 1000 blocos
    // para calcular a força atual da enchente.
    private static final int MAX_WATER_BLOCKS_TO_COUNT = 1000;

    public static int countWaterBlocks(Level level, BlockPos start) {

        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();

        BlockPos startPos = start.immutable();

        queue.add(startPos);
        visited.add(startPos);

        int waterCount = 0;

        while (!queue.isEmpty()) {

            BlockPos current = queue.poll();

            if (!isWater(level, current)) {
                continue;
            }

            waterCount++;

            // Para a busca assim que atingir o limite.
            if (waterCount >= MAX_WATER_BLOCKS_TO_COUNT) {
                return waterCount;
            }

            for (BlockPos neighbor : getNeighbors(current)) {

                if (visited.add(neighbor)
                        && isWater(level, neighbor)) {

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
