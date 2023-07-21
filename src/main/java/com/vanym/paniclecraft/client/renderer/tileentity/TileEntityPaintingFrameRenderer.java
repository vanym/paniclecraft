package com.vanym.paniclecraft.client.renderer.tileentity;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.block.BlockPaintingFrame;
import com.vanym.paniclecraft.core.component.painting.ISidePictureProvider;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingContainer;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;

import net.minecraft.block.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityPaintingFrameRenderer extends TileEntityPaintingRenderer {
    
    public TileEntityPaintingFrameRenderer() {
        super();
        this.renderFrameType = 0;
    }
    
    @Override
    protected BlockState getActualState(TileEntityPaintingContainer tile) {
        BlockPaintingFrame block = Core.instance.painting.blockPaintingFrame;
        BlockState state = null;
        if (tile.hasWorld()) {
            BlockState stateWorld = tile.getWorld().getBlockState(tile.getPos());
            if (block == stateWorld.getBlock()) {
                state = stateWorld;
            }
        }
        return block.getActualState(state != null ? state : block.getDefaultState(),
                                    (TileEntityPaintingFrame)tile);
    }
    
    @Override
    protected Picture getPicture(TileEntityPaintingContainer tile, int side) {
        return tile.getPicture(side);
    }
    
    @Override
    protected int getSize(TileEntityPaintingContainer tile) {
        return ISidePictureProvider.N;
    }
}
