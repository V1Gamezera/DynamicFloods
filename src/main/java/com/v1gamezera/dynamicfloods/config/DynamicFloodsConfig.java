package com.v1gamezera.dynamicfloods.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class DynamicFloodsConfig {

    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue ENABLE_FLOODS;
    public static final ForgeConfigSpec.BooleanValue REQUIRE_RAIN;

    public static final ForgeConfigSpec.IntValue FLOOD_INTERVAL;
    public static final ForgeConfigSpec.IntValue MAX_FLOOD_LEVEL;
    public static final ForgeConfigSpec.IntValue SPREAD_RADIUS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("DynamicFloods");

        ENABLE_FLOODS = builder
                .comment("Ativa ou desativa as enchentes")
                .define("enableFloods", true);

        REQUIRE_RAIN = builder
                .comment("A enchente só acontece quando estiver chovendo")
                .define("requireRain", true);

        FLOOD_INTERVAL = builder
                .comment("Tempo em ticks para aumentar o nível da enchente")
                .defineInRange("floodInterval", 600, 20, 72000);

        MAX_FLOOD_LEVEL = builder
                .comment("Nível máximo da enchente")
                .defineInRange("maxFloodLevel", 5, 1, 20);

        SPREAD_RADIUS = builder
                .comment("Distância que a água pode se espalhar")
                .defineInRange("spreadRadius", 15, 1, 100);

        builder.pop();

        SPEC = builder.build();
    }
}