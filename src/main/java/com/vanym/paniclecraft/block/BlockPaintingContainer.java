package com.vanym.paniclecraft.block;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.painting.ISidePictureProvider;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.item.ItemPainting;
import com.vanym.paniclecraft.utils.MainUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

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
    
    public static void rotatePicture(
            EntityPlayer player,
            Picture picture,
            ForgeDirection side,
            boolean place) {
        if (side != ForgeDirection.DOWN && side != ForgeDirection.UP) {
            return;
        }
        int rot = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        if ((side == ForgeDirection.UP) != place) {
            rot = (4 - rot) % 4;
        }
        picture.rotate(rot);
    }
    
    public static ForgeDirection getStackDirection(EntityPlayer player, ForgeDirection side) {
        ForgeDirection dir = side.getOpposite();
        Vec3 dirvec = MainUtils.getVecByDirection(dir);
        Vec3 lookvec = MainUtils.getVecByDirection(ForgeDirection.SOUTH);
        lookvec.rotateAroundX(-(player.rotationPitch * 0.999F) * (float)Math.PI / 180.0F);
        lookvec.rotateAroundY(-player.rotationYaw * (float)Math.PI / 180.0F);
        Vec3 stackvec = dirvec.subtract(lookvec);
        ForgeDirection stackdir = MainUtils.getDirectionByVec(stackvec);
        if (stackdir == dir || stackdir == side) {
            return ForgeDirection.UNKNOWN;
        }
        return stackdir;
    }
    
    public static ItemStack getPictureAsItem(Picture picture) {
        ItemStack itemS = new ItemStack(Core.instance.painting.itemPainting);
        if (picture == null) {
            return itemS;
        }
        NBTTagCompound itemTag = new NBTTagCompound();
        NBTTagCompound pictureTag = new NBTTagCompound();
        picture.writeToNBT(pictureTag);
        itemS.setTagCompound(itemTag);
        itemTag.setTag(ItemPainting.TAG_PICTURE, pictureTag);
        return itemS;
    }
    
    public static Picture getPicture(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile == null || !(tile instanceof ISidePictureProvider)) {
            return null;
        }
        ISidePictureProvider tileP = (ISidePictureProvider)tile;
        return tileP.getPainting(side);
    }
    
    public static Picture getPicture(IBlockAccess world, MovingObjectPosition target) {
        if (target.typeOfHit != MovingObjectType.BLOCK) {
            return null;
        }
        return getPicture(world, target.blockX, target.blockY, target.blockZ, target.sideHit);
    }
}
