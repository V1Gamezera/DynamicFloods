package com.v1gamezera.dynamicfloods.event;

import com.v1gamezera.dynamicfloods.flood.FloodManager;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class FloodEvents {

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        event.getEntity().sendSystemMessage(
                Component.literal("🌊 DynamicFloods carregado! Prepare-se para as enchentes!")
        );

        FloodManager.increaseFlood();
    }
}