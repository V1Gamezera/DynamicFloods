package com.v1gamezera.dynamicfloods;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.ModLoadingContext;
import com.v1gamezera.dynamicfloods.config.DynamicFloodsConfig;

@Mod("dynamicfloods")
public class DynamicFloods {

    public DynamicFloods() {

        ModLoadingContext.get().registerConfig(
                ModConfig.Type.COMMON,
                DynamicFloodsConfig.SPEC
        );

        System.out.println("🌊 DynamicFloods iniciado!");
    }
}