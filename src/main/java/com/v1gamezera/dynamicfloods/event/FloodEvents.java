package com.v1gamezera.dynamicfloods.event;

import com.v1gamezera.dynamicfloods.flood.FloodManager;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.level.Level;
import com.v1gamezera.dynamicfloods.flood.FloodSimulator;

@Mod.EventBusSubscriber
public class FloodEvents {

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {

        if (event.phase == TickEvent.Phase.END
                && event.level.dimension() == Level.OVERWORLD
                && !event.level.isClientSide()
                && event.level.isRaining()) {

            FloodManager.tickFlood(event.level.getGameTime());

            if (!event.level.players().isEmpty()) {

                var player = event.level.players().get(0);

                if (FloodManager.getFloodOrigin() == null) {
                    FloodManager.setFloodOrigin(player.blockPosition());
                }

                FloodSimulator.simulateFlood(
                        event.level,
                        player
                );
            }

        }
    }
}