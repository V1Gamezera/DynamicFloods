package com.v1gamezera.dynamicfloods.event;

import com.v1gamezera.dynamicfloods.flood.FloodManager;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.level.Level;

@Mod.EventBusSubscriber
public class FloodEvents {

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {

        if (event.phase == TickEvent.Phase.END && event.level.dimension() == Level.OVERWORLD && !event.level.isClientSide()) {

            FloodManager.tickFlood(event.level.getGameTime());

        }
    }
}