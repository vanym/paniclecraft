package com.vanym.paniclecraft.block;

import com.vanym.paniclecraft.utils.MainUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;

public abstract class BlockPaintingContainer extends BlockContainerMod3 {
    
    @SideOnly(Side.CLIENT)
    public static enum SpecialRendererPhase {
        NONE, FRAME, FRAMEINSIDE, PAINTING;
        
        public boolean isNone() {
            return (this == NONE);
        }
    }
    
    @SideOnly(Side.CLIENT)
    protected SpecialRendererPhase specialRendererPhase = SpecialRendererPhase.NONE;
    
    @SideOnly(Side.CLIENT)
    protected AxisAlignedBB specialRendererBox;
    
    protected final double paintingWidth;
    
    public BlockPaintingContainer(Material material) {
        super(material);
        this.paintingWidth = (1.0D / 16D);
    }
    
    public double getPaintingWidth() {
        return this.paintingWidth;
    }
    
    @SideOnly(Side.CLIENT)
    public void setRendererPhase(SpecialRendererPhase sRP) {
        this.specialRendererPhase = sRP;
    }
    
    @SideOnly(Side.CLIENT)
    public void setRendererBox(AxisAlignedBB sRB) {
        this.specialRendererBox = sRB;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        if (!this.specialRendererPhase.isNone()) {
            // Have to use distinct rendering box to avoid glitches
            if (MainUtils.isTouchingSide(side, this.specialRendererBox)
                && world.getBlock(x, y, z).isOpaqueCube()) {
                return false;
            } else {
                return true;
            }
        }
        return super.shouldSideBeRendered(world, x, y, z, side);
    }
    
    @SideOnly(Side.CLIENT)
    public abstract boolean shouldSideBeRendered(int side, int meta, TileEntity tile);
}
