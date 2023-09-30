package com.vanym.paniclecraft.block;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.container.ContainerPaintingViewServer;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.core.component.painting.WorldPicturePoint;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;
import com.vanym.paniclecraft.utils.GeometryUtils;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class BlockPaintingContainer extends BlockContainerMod3 {
    
    @SideOnly(Side.CLIENT)
    public static enum SpecialRendererPhase {
        NONE, FRAME, FRAMEINSIDE, PICTURE;
        
        public boolean isNone() {
            return (this == NONE);
        }
    }
    
    @SideOnly(Side.CLIENT)
    protected SpecialRendererPhase specialRendererPhase;
    
    @SideOnly(Side.CLIENT)
    protected AxisAlignedBB specialRendererBox;
    
    protected final double paintingOutlineSize;
    
    public BlockPaintingContainer(Material material) {
        super(material);
        this.paintingOutlineSize = (1.0D / 16D);
        if (FMLCommonHandler.instance().getSide().isClient()) {
            this.setRendererPhase(SpecialRendererPhase.NONE);
        }
    }
    
    public double getPaintingOutlineSize() {
        return this.paintingOutlineSize;
    }
    
    @Override
    public boolean onBlockActivated(
            World world,
            int x,
            int y,
            int z,
            EntityPlayer player,
            int side,
            float hitX,
            float hitY,
            float hitZ) {
        if (!Core.instance.painting.config.openViewByClick
            || player.isSneaking()
            || player.getHeldItem() != null) {
            return false;
        }
        if (world.isRemote) {
            return true;
        }
        WorldPicturePoint point =
                new WorldPicturePoint(WorldPictureProvider.ANYTILE, world, x, y, z, side);
        ContainerPaintingViewServer view = ContainerPaintingViewServer.makeFullView(point, 128);
        if (view != null && player instanceof EntityPlayerMP) {
            view.setEditable(player.capabilities.isCreativeMode
                && player.canCommandSenderUseCommand(2, ""));
            ContainerPaintingViewServer.openGui((EntityPlayerMP)player, view);
        }
        return true;
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
            if (GeometryUtils.isTouchingSide(side, this.specialRendererBox)
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
    
    public static int getRotate(Entity player, ForgeDirection side, boolean place) {
        if (side != ForgeDirection.DOWN && side != ForgeDirection.UP) {
            return 0;
        }
        int rot = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        if ((side == ForgeDirection.UP) != place) {
            rot = (4 - rot) % 4;
        }
        return rot;
    }
    
    public static void rotatePicture(
            EntityPlayer player,
            Picture picture,
            ForgeDirection side,
            boolean place) {
        int rot = getRotate(player, side, place);
        picture.rotate(rot);
    }
    
    public static ForgeDirection getStackDirection(EntityPlayer player, ForgeDirection side) {
        ForgeDirection dir = side.getOpposite();
        Vec3 dirvec = GeometryUtils.getVecByDirection(dir);
        Vec3 lookvec = GeometryUtils.getVecByDirection(ForgeDirection.SOUTH);
        lookvec.rotateAroundX(-(player.rotationPitch * 0.999F) * (float)Math.PI / 180.0F);
        lookvec.rotateAroundY(-player.rotationYaw * (float)Math.PI / 180.0F);
        Vec3 stackvec = dirvec.subtract(lookvec);
        ForgeDirection stackdir = GeometryUtils.getDirectionByVec(stackvec);
        if (stackdir == dir || stackdir == side) {
            return ForgeDirection.UNKNOWN;
        }
        return stackdir;
    }
}
