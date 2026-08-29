package com.v1gamezera.dynamicfloods.client;

import com.v1gamezera.dynamicfloods.flood.WoodDamageManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.RandomSource;

import java.util.List;

public class WetWoodBakedModel extends BakedModelWrapper<BakedModel> {

    private static final ModelProperty<BlockAndTintGetter> LEVEL =
            new ModelProperty<>();

    private static final ModelProperty<BlockPos> POS =
            new ModelProperty<>();

    private final BakedModel wetModel;

    public WetWoodBakedModel(BakedModel originalModel, BakedModel wetModel) {
        super(originalModel);
        this.wetModel = wetModel;
    }

    @Override
    public List<BakedQuad> getQuads(
            BlockState state,
            Direction side,
            RandomSource rand,
            ModelData data,
            RenderType renderType
    ) {

        if (data.has(LEVEL) && data.has(POS)) {

            BlockAndTintGetter level = data.get(LEVEL);
            BlockPos pos = data.get(POS);

            if (level != null && pos != null) {

                if (WoodDamageManager.isWet(pos)) {

                    return wetModel.getQuads(
                            state,
                            side,
                            rand,
                            data,
                            renderType
                    );
                }
            }
        }

        return super.getQuads(
                state,
                side,
                rand,
                data,
                renderType
        );
    }

    @Override
    public ModelData getModelData(
            BlockAndTintGetter level,
            BlockPos pos,
            BlockState state,
            ModelData modelData
    ) {

        ModelData parentData =
                super.getModelData(level, pos, state, modelData);

        return ModelData.builder()
                .with(LEVEL, level)
                .with(POS, pos)
                .build();
    }
}