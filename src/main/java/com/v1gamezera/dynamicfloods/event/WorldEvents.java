package com.v1gamezera.dynamicfloods.event;

import com.v1gamezera.dynamicfloods.flood.FloodManager;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.level.Level;

@Mod.EventBusSubscriber
public class WorldEvents {

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {

        if (!(event.getLevel() instanceof Level level)) {
            return;
        }

        if (!level.isClientSide() && level.dimension() == Level.OVERWORLD) {
            FloodManager.resetFlood();
        }
    }
}