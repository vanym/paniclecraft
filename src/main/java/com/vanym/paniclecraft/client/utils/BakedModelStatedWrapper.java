package com.vanym.paniclecraft.client.utils;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BakedModelStatedWrapper<T extends IBakedModel> extends BakedModelWrapper<T> {
    
    protected final IBlockState state;
    
    public BakedModelStatedWrapper(T originalModel, IBlockState state) {
        super(originalModel);
        this.state = state;
    }
    
    @Override
    public List<BakedQuad> getQuads(
            @Nullable IBlockState state,
            @Nullable EnumFacing side,
            long rand) {
        return super.getQuads(this.state, side, rand);
    }
    
    @Override
    public boolean isAmbientOcclusion(IBlockState state) {
        return super.isAmbientOcclusion(this.state);
    }
}
