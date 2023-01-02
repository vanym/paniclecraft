package com.vanym.paniclecraft.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

public class RenderBlocksPainting extends RenderBlocks {
    
    protected int maxAmbientOcclusion = 2;
    
    public RenderBlocksPainting(IBlockAccess world) {
        super(world);
    }
    
    public RenderBlocksPainting() {
        super();
    }
    
    public void setMaxAmbientOcclusion(int maxAmbientOcclusion) {
        this.maxAmbientOcclusion = maxAmbientOcclusion;
    }
    
    public void resetMaxAmbientOcclusion() {
        this.setMaxAmbientOcclusion(2);
    }
    
    @Override
    public boolean renderStandardBlock(Block block, int x, int y, int z) {
        // ignoring colorMultiplier
        float r = 1.0F;
        float g = 1.0F;
        float b = 1.0F;
        
        if (this.maxAmbientOcclusion >= 1 && Minecraft.isAmbientOcclusionEnabled()
            && block.getLightValue() == 0) {
            if (this.maxAmbientOcclusion >= 2 && this.partialRenderBounds) {
                return this.renderStandardBlockWithAmbientOcclusionPartial(block, x, y, z, r, g, b);
            } else {
                return this.renderStandardBlockWithAmbientOcclusion(block, x, y, z, r, g, b);
            }
        } else {
            return this.renderStandardBlockWithColorMultiplier(block, x, y, z, r, g, b);
        }
    }
}
