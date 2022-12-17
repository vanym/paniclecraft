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
    
    protected final double paintingOutlineSize;
    
    public BlockPaintingContainer(Material material) {
        super(material);
        this.paintingOutlineSize = (1.0D / 16D);
    }
    
    public double getPaintingOutlineSize() {
        return this.paintingOutlineSize;
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
    
    public static int getPictureX(int width, int side, float x, float y, float z) {
        int dx = (int)(x * width);
        int dy = (int)(z * width);
        
        switch (side) {
            case 0:
            case 1:
            case 2:
                return width - 1 - dx;
            case 3:
                return dx;
            case 4:
                return dy;
            case 5:
                return width - 1 - dy;
            default:
                return -1;
        }
    }
    
    public static int getPictureY(int height, int side, float x, float y, float z) {
        int dy = (int)(y * height);
        int dz = (int)(z * height);
        switch (side) {
            case 0:
                return dz;
            case 1:
                return height - 1 - dz;
            case 2:
            case 3:
            case 4:
            case 5:
                return height - 1 - dy;
            default:
                return -1;
        }
    }
}
