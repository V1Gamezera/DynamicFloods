package com.v1gamezera.dynamicfloods.flood;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class WaterSourceFinder {

    public static BlockPos findWater(Level level, BlockPos center, int maxRadius) {

        for (int radius = 1; radius <= maxRadius; radius++) {

            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {

                    for (int y = -5; y <= 5; y++) {

                        BlockPos pos = center.offset(x, y, z);

                        if (isWater(level, pos)) {
                            return pos;
                        }
                    }
                }
            }
        }

        return null;
    }


    private static boolean isWater(Level level, BlockPos pos) {
        return level.getBlockState(pos).is(Blocks.WATER);
    }
}