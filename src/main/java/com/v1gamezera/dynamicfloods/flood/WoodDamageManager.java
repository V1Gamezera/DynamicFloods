package com.v1gamezera.dynamicfloods.flood;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;

public class WoodDamageManager {

    // Guarda quando cada bloco de madeira começou a ficar molhado
    private static final Map<BlockPos, Long> wetWood = new HashMap<>();

    // Guarda quando cada bloco começou a secar
    private static final Map<BlockPos, Long> dryingWood = new HashMap<>();

    // Tempo para cada estágio
    private static final long WET_TIME = 20 * 10;          // 10 segundos
    private static final long WATERLOGGED_TIME = 20 * 30;  // 30 segundos
    private static final long BREAK_TIME = 20 * 60;        // 60 segundos

    // Tempo que a madeira precisa ficar sem água para secar
    private static final long DRY_TIME = 20 * 10;          // 10 segundos


    public static void tick(Level level, BlockPos origin, int radius) {

        if (origin == null) {
            return;
        }

        long gameTime = level.getGameTime();

        // Procura madeira ao redor da enchente
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {

                    BlockPos pos = origin.offset(x, y, z);

                    // Só queremos blocos de madeira/planks
                    if (!level.getBlockState(pos).is(BlockTags.PLANKS)) {
                        continue;
                    }

                    BlockPos immutablePos = pos.immutable();

                    // Verifica se existe água encostando na madeira
                    if (isTouchingWater(level, immutablePos)) {

                        // A madeira está molhada novamente.
                        // Se ela estava secando, cancela a secagem.
                        dryingWood.remove(immutablePos);

                        // Se acabou de ficar molhada
                        if (!wetWood.containsKey(immutablePos)) {

                            wetWood.put(immutablePos, gameTime);

                            System.out.println(
                                    "💧 Madeira ficou molhada em: " + immutablePos
                            );
                        }

                        long wetTime = gameTime - wetWood.get(immutablePos);

                        // Estágio 1
                        if (wetTime >= WET_TIME && wetTime < WATERLOGGED_TIME) {

                            System.out.println(
                                    "🪵 Madeira molhada: " + immutablePos
                            );
                        }

                        // Estágio 2
                        else if (wetTime >= WATERLOGGED_TIME && wetTime < BREAK_TIME) {

                            System.out.println(
                                    "🌊 Madeira encharcada: " + immutablePos
                            );
                        }

                        // Estágio 3
                        else if (wetTime >= BREAK_TIME) {

                            System.out.println(
                                    "💥 Madeira quebrada pela água: " + immutablePos
                            );

                            level.destroyBlock(immutablePos, true);

                            wetWood.remove(immutablePos);
                            dryingWood.remove(immutablePos);
                        }

                    } else {

                        // Não existe água encostando na madeira.
                        // Se ela estava molhada, começa a secar.
                        if (wetWood.containsKey(immutablePos)) {

                            // Começou a secar agora
                            if (!dryingWood.containsKey(immutablePos)) {

                                dryingWood.put(immutablePos, gameTime);

                                System.out.println(
                                        "☀️ Madeira começou a secar em: " + immutablePos
                                );
                            }

                            long dryingTime =
                                    gameTime - dryingWood.get(immutablePos);

                            System.out.println(
                                    "☀️ Secando madeira: " + immutablePos
                                            + " | Tempo: " + dryingTime
                                            + "/" + DRY_TIME
                            );

// Terminou de secar
                            if (dryingTime >= DRY_TIME) {

                                wetWood.remove(immutablePos);
                                dryingWood.remove(immutablePos);

                                // Avisa o Minecraft que o bloco precisa ser renderizado novamente
                                level.sendBlockUpdated(
                                        immutablePos,
                                        level.getBlockState(immutablePos),
                                        level.getBlockState(immutablePos),
                                        Block.UPDATE_CLIENTS
                                );

                                System.out.println(
                                        "🌳 Madeira secou completamente em: " + immutablePos
                                );
                            }
                        }
                    }
                }
            }
        }
    }


    private static boolean isTouchingWater(Level level, BlockPos pos) {

        BlockPos[] neighbors = {
                pos.above(),
                pos.below(),
                pos.north(),
                pos.south(),
                pos.east(),
                pos.west()
        };

        for (BlockPos neighbor : neighbors) {

            if (level.getFluidState(neighbor).is(FluidTags.WATER)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isWet(BlockPos pos) {
        return wetWood.containsKey(pos);
    }

    public static void reset() {
        wetWood.clear();
        dryingWood.clear();

        System.out.println("🪵 Estado das madeiras resetado!");
    }
}