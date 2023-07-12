package com.vanym.paniclecraft.client.utils;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.IModelData;

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
    public List<BakedQuad> getQuads(
            @Nullable BlockState state,
            @Nullable Direction side,
            Random rand,
            IModelData extraData) {
        return super.getQuads(this.state, side, rand, extraData);
    }
    
    @Override
    public boolean isAmbientOcclusion(BlockState state) {
        return super.isAmbientOcclusion(this.state);
    }
    
    @Override
    public IModelData getModelData(
            IEnviromentBlockReader world,
            BlockPos pos,
            BlockState state,
            IModelData tileData) {
        return tileData;
    }
}
