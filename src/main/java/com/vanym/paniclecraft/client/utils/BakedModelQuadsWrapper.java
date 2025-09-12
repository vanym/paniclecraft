package com.vanym.paniclecraft.client.utils;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class BakedModelQuadsWrapper extends BakedModelWrapper<IBakedModel> {
    
    public BakedModelQuadsWrapper(IBakedModel originalModel) {
        super(originalModel);
    }
    
    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        return this.wrapQuads(super.getQuads(state, side, rand));
    }
    
    protected abstract List<BakedQuad> wrapQuads(List<BakedQuad> quads);
}
