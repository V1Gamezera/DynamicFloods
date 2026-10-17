
package com.v1gamezera.dynamicfloods.flood;

import com.v1gamezera.dynamicfloods.config.DynamicFloodsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class FloodSimulator {

    private static final int SIMULATION_INTERVAL = 20;
    private static final int MAX_GROUND_SEARCH = 32;

    // Cada mundo possui seu próprio temporizador.
    private static final Map<Level, Long> lastSimulation =
            new WeakHashMap<>();

    // Registra apenas a água que este simulador colocou.
    // Isso permitirá implementar o recuo da enchente depois.
    private static final Map<Level, Set<BlockPos>> createdFloodWater =
            new WeakHashMap<>();

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

    public static BlockPos findNearestWater(
            Level level,
            BlockPos center,
            int radius
    ) {
        // Busca determinística em anéis, evitando depender de
        // encontrar água por tentativas aleatórias.
        for (int r = 0; r <= radius; r++) {
            for (int x = -r; x <= r; x++) {
                for (int z = -r; z <= r; z++) {
                    if (r > 0 && Math.abs(x) != r
                            && Math.abs(z) != r) {
                        continue;
                    }

                    BlockPos column = center.offset(x, 0, z);
                    BlockPos ground = findGround(level, column);
                    BlockPos water = ground.below();

                    if (level.getFluidState(water).is(FluidTags.WATER)) {
                        return water.immutable();
                    }

                    // Também verifica alguns blocos acima e abaixo
                    // da altura do terreno encontrado.
                    for (int dy = -5; dy <= 5; dy++) {
                        BlockPos candidate = water.offset(0, dy, 0);

                        if (level.getFluidState(candidate)
                                .is(FluidTags.WATER)) {
                            return candidate.immutable();
                        }
                    }
                }
            }
        }

        return null;
    }

    public static void simulateFlood(Level level, Player player) {
        if (level.isClientSide()) {
            return;
        }

        long gameTime = level.getGameTime();
        Long lastTime = lastSimulation.get(level);

        if (lastTime != null
                && gameTime - lastTime < SIMULATION_INTERVAL) {
            return;
        }

        lastSimulation.put(level, gameTime);

        BlockPos origin = FloodManager.getFloodOrigin();

        if (origin == null) {
            System.out.println(
                    "🌊 Simulação interrompida: origem da enchente ausente."
            );
            return;
        }

        int floodLevel = FloodManager.getFloodLevel();

        if (floodLevel <= 0) {
            return;
        }

        BlockPos source = resolveWaterSource(level, origin);

        if (source == null) {
            System.out.println(
                    "🌊 Simulação interrompida: não foi encontrada "
                            + "água na origem " + origin
            );
            return;
        }

        int radius = Math.min(
                DynamicFloodsConfig.SPREAD_RADIUS.get(),
                floodLevel * 3
        );

        // O nível inicial pode espalhar água na altura atual.
        // Enchentes mais fortes também podem elevar a superfície.
        int rise = Math.min(4, Math.max(1, (floodLevel + 1) / 2));
        int waterSurfaceY = source.getY() + rise;

        Set<BlockPos> created = createdFloodWater.computeIfAbsent(
                level,
                ignored -> new HashSet<>()
        );

        int createdThisTick = 0;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {

                if (x * x + z * z > radius * radius) {
                    continue;
                }

                int worldX = source.getX() + x;
                int worldZ = source.getZ() + z;

// Se essa coluna já contém água na altura original,
// não precisamos criar mais água sobre ela.
                BlockPos waterAtSourceHeight =
                        new BlockPos(worldX, source.getY(), worldZ);

                if (level.getFluidState(waterAtSourceHeight)
                        .is(FluidTags.WATER)) {
                    continue;
                }

                int groundY = findTopSolidBelow(
                        level,
                        worldX,
                        worldZ,
                        waterSurfaceY
                );

                // Não inundar colunas cujo terreno esteja acima
                // ou na altura máxima da água.
                if (groundY == Integer.MIN_VALUE
                        || groundY >= waterSurfaceY) {
                    continue;
                }

                // Preenche a coluna baixa até o nível da água,
                // sem substituir blocos sólidos ou líquidos existentes.
                for (int y = groundY + 1; y <= waterSurfaceY; y++) {
                    BlockPos waterPos = new BlockPos(worldX, y, worldZ);
                    BlockState state = level.getBlockState(waterPos);

                    if (state.isAir()) {
                        boolean placed = level.setBlock(
                                waterPos,
                                Blocks.WATER.defaultBlockState(),
                                3
                        );

                        if (placed) {
                            created.add(waterPos.immutable());
                            createdThisTick++;
                        }
                    } else if (!state.getFluidState().is(FluidTags.WATER)) {
                        // Não atravessar paredes ou outros blocos.
                        break;
                    }
                }
            }
        }

        System.out.println(
                "🌊 Simulação: nível=" + floodLevel
                        + ", origem=" + source
                        + ", raio=" + radius
                        + ", água criada neste ciclo=" + createdThisTick
                        + ", blocos registrados=" + created.size()
        );
    }

    private static BlockPos resolveWaterSource(
            Level level,
            BlockPos origin
    ) {
        BlockPos[] candidates = {
                origin,
                origin.below(),
                origin.above(),
                origin.north(),
                origin.south(),
                origin.east(),
                origin.west()
        };

        for (BlockPos candidate : candidates) {
            if (level.getFluidState(candidate).is(FluidTags.WATER)) {
                return candidate.immutable();
            }
        }

        return null;
    }

    private static int findTopSolidBelow(
            Level level,
            int x,
            int z,
            int startY
    ) {
        int minY = Math.max(
                level.getMinBuildHeight(),
                startY - MAX_GROUND_SEARCH
        );

        int maxY = Math.min(
                startY,
                level.getMaxBuildHeight() - 1
        );

        for (int y = maxY; y >= minY; y--) {
            BlockPos pos = new BlockPos(x, y, z);
            BlockState state = level.getBlockState(pos);

            if (state.blocksMotion()
                    && state.getFluidState().isEmpty()) {
                return y;
            }
        }

        return Integer.MIN_VALUE;
    }
}