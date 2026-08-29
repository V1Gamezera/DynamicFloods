package com.v1gamezera.dynamicfloods.client;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = "dynamicfloods",
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class DynamicFloodsClient {

    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {

        event.register(
                new ResourceLocation(
                        "dynamicfloods",
                        "block/oak_planks_wet"
                )
        );

        System.out.println("🎨 Modelo de madeira molhada registrado!");
    }

    @SubscribeEvent
    public static void modifyModels(ModelEvent.ModifyBakingResult event) {

        ModelResourceLocation normalLocation =
                new ModelResourceLocation(
                        new ResourceLocation("minecraft", "oak_planks"),
                        ""
                );

        ResourceLocation wetLocation =
                new ResourceLocation(
                        "dynamicfloods",
                        "block/oak_planks_wet"
                );

        BakedModel normalModel = event.getModels().get(normalLocation);
        BakedModel wetModel = event.getModels().get(wetLocation);

        System.out.println("🔎 Modelo normal: " + normalModel);
        System.out.println("🔎 Modelo molhado: " + wetModel);

        if (normalModel != null && wetModel != null) {

            event.getModels().put(
                    normalLocation,
                    new WetWoodBakedModel(normalModel, wetModel)
            );

            System.out.println("🪵 Modelo da madeira conectado ao sistema de madeira molhada!");
        }
    }
}