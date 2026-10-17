
package com.v1gamezera.dynamicfloods.event;

import com.v1gamezera.dynamicfloods.flood.FloodManager;
import com.v1gamezera.dynamicfloods.flood.FloodSimulator;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class FloodEvents {

    @SubscribeEvent
    public static void onWorldTick(
            TickEvent.LevelTickEvent event
    ) {
        if (event.phase != TickEvent.Phase.END
                || event.level.dimension() != Level.OVERWORLD
                || event.level.isClientSide()
                || !event.level.isRaining()) {
            return;
        }

        Level level = event.level;

        // O FloodManager passa a ser responsável por encontrar
        // e validar a origem da enchente.
        FloodManager.tickFlood(level);

        // Só executa a simulação após validar uma origem.
        if (!level.players().isEmpty()
                && FloodManager.getFloodOrigin() != null) {

            var player = level.players().get(0);

            FloodSimulator.simulateFlood(level, player);
        }
    }
}
