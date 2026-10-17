package com.v1gamezera.dynamicfloods.flood;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class WaterSourceFinder {

    public static BlockPos findWater(
            Level level,
            BlockPos center,
            int maxRadius
    ) {

        // Procura do centro para as extremidades,
        // examinando cada coluna horizontal apenas uma vez.
        for (int radius = 0; radius <= maxRadius; radius++) {

            // Verifica o centro na primeira iteração.
            if (radius == 0) {
                BlockPos water = findWaterInColumn(
                        level, center, 0, 0
                );

                if (water != null) {
                    return water;
                }

                continue;
            }

            // Examina as bordas norte e sul do quadrado.
            for (int x = -radius; x <= radius; x++) {

                BlockPos water = findWaterInColumn(
                        level, center, x, -radius
                );

                if (water != null) {
                    return water;
                }

                water = findWaterInColumn(
                        level, center, x, radius
                );

                if (water != null) {
                    return water;
                }
            }

            // Examina as bordas leste e oeste,
            // ignorando os cantos já examinados.
            for (int z = -radius + 1;
                 z < radius;
                 z++) {

                BlockPos water = findWaterInColumn(
                        level, center, -radius, z
                );

                if (water != null) {
                    return water;
                }

                water = findWaterInColumn(
                        level, center, radius, z
                );

                if (water != null) {
                    return water;
                }
            }
        }

        return null;
    }

    private static BlockPos findWaterInColumn(
            Level level,
            BlockPos center,
            int x,
            int z
    ) {

        // Mantém a busca vertical do código anterior.
        for (int y = -5; y <= 5; y++) {

            BlockPos pos = center.offset(x, y, z);

            if (level.getBlockState(pos).is(Blocks.WATER)) {
                return pos;
            }
        }

        return null;
    }
}