package com.vanym.paniclecraft.client.utils;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.BakedModelWrapper;

public class BakedModelStatedWrapper<T extends IBakedModel> extends BakedModelWrapper<T> {
    
    protected final BlockState state;
    
    public BakedModelStatedWrapper(T originalModel, BlockState state) {
        super(originalModel);
        this.state = state;
    }
    
    @Override
    public List<BakedQuad> getQuads(
            @Nullable BlockState state,
            @Nullable Direction side,
            Random rand) {
        return super.getQuads(this.state, side, rand);
    }
    
    @Override
    public boolean isAmbientOcclusion(BlockState state) {
        return super.isAmbientOcclusion(this.state);
    }
}
