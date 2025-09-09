package com.vanym.paniclecraft.client.utils;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.IModelData;

public abstract class BakedModelQuadsWrapper extends BakedModelWrapper<IBakedModel> {
    
    public BakedModelQuadsWrapper(IBakedModel originalModel) {
        super(originalModel);
    }
    
    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
        return this.wrapQuads(super.getQuads(state, side, rand));
    }
    
    @Override
    public List<BakedQuad> getQuads(
            BlockState state,
            Direction side,
            Random rand,
            IModelData extraData) {
        return this.wrapQuads(super.getQuads(state, side, rand, extraData));
    }
    
    protected abstract List<BakedQuad> wrapQuads(List<BakedQuad> quads);
}
