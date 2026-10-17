
package com.v1gamezera.dynamicfloods.flood;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class WaterSourceFinder {

    // Ignora poças e pequenas fontes artificiais.
    private static final int MIN_WATER_BODY_SIZE = 20;

    public static BlockPos findWater(
            Level level,
            BlockPos center,
            int maxRadius
    ) {
        // Procura em anéis, do jogador para fora.
        // Corpos pequenos são ignorados e a busca continua.
        for (int radius = 0; radius <= maxRadius; radius++) {

            if (radius == 0) {
                BlockPos water = findWaterInColumn(
                        level, center, 0, 0
                );

                if (water != null) {
                    return water;
                }

                continue;
            }

            // Bordas norte e sul.
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

            // Bordas leste e oeste, sem repetir os cantos.
            for (int z = -radius + 1; z < radius; z++) {

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
        for (int y = -5; y <= 5; y++) {

            BlockPos pos = center.offset(x, y, z);

            if (!level.getBlockState(pos).is(Blocks.WATER)) {
                continue;
            }

            // Mede o corpo d'água antes de aceitá-lo.
            int waterSize = WaterAnalyzer.countWaterBlocks(
                    level,
                    pos
            );

            if (waterSize >= MIN_WATER_BODY_SIZE) {
                return pos.immutable();
            }

            // Se for pequeno, continua procurando outras águas.
        }

        return null;
    }
}
