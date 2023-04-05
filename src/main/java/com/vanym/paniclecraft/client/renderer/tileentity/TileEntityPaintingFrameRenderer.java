package com.vanym.paniclecraft.client.renderer.tileentity;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.block.BlockPaintingFrame;
import com.vanym.paniclecraft.core.component.painting.ISidePictureProvider;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingContainer;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;

import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityPaintingFrameRenderer extends TileEntityPaintingRenderer {
    
    public TileEntityPaintingFrameRenderer() {
        super();
        this.renderFrameType = 0;
    }
    
    @Override
    protected int getN() {
        return ISidePictureProvider.N;
    }
    
    @Override
    protected IBlockState getActualState(IBlockState state, TileEntityPaintingContainer tile) {
        BlockPaintingFrame block = Core.instance.painting.blockPaintingFrame;
        return block.getActualState(state, (TileEntityPaintingFrame)tile);
    }
    
    @Override
    protected Picture getPicture(TileEntityPaintingContainer tile, int side) {
        return tile.getPicture(side);
    }
}
