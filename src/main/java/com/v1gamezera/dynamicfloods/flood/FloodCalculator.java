package com.v1gamezera.dynamicfloods.flood;

public class FloodCalculator {

    public static int calculateFloodLevel(int waterSize) {

        if (waterSize <= 0) {
            return 0;
        }

        // Poças pequenas
        if (waterSize < 20) {
            return 1;
        }

        // Lagos pequenos
        if (waterSize < 200) {
            return 3;
        }

        // Lagos grandes / rios pequenos
        if (waterSize < 1000) {
            return 5;
        }

        // Grandes corpos de água
        return 10;
    }
}