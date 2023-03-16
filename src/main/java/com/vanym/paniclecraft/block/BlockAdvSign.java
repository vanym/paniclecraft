package com.vanym.paniclecraft.block;

import java.util.Random;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.item.ItemAdvSign;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;
import com.vanym.paniclecraft.utils.TileOnSide;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockAdvSign extends BlockContainerMod3 {
    
    public BlockAdvSign() {
        super(Material.wood);
        this.setHardness(1.0F);
        this.setBlockName("advSign");
        this.setBlockBounds(0.25F, 0.25F, 0.25F, 0.75F, 0.75F, 0.75F);
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityAdvSign();
    }
    
    @Override
    public Item getItemDropped(int meta, Random rand, int fortune) {
        return Core.instance.advSign.itemAdvSign;
    }
    
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }
    
    @Override
    public void setBlockBoundsBasedOnState(
            IBlockAccess world,
            int x,
            int y,
            int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (!TileEntityAdvSign.class.isInstance(tile)) {
            return;
        }
        TileEntityAdvSign tileAS = (TileEntityAdvSign)tile;
        SignSide pside = SignSide.getSide(tileAS.getBlockMetadata());
        AxisAlignedBB box;
        if (tileAS.onStick()) {
            box = AxisAlignedBB.getBoundingBox(0.25D, 0.25D, 0.0D, 0.75D, 0.75D, 1.0D);
        } else {
            double direction = MathHelper.wrapAngleTo180_double(tileAS.getDirection());
            if (pside == SignSide.DOWN) {
                direction *= -1.0D;
            }
            double radians = Math.toRadians(direction);
            double sin = Math.sin(radians);
            double cos = Math.cos(radians);
            
            double xl = -0.5D, xr = 0.5D;
            double yt = -0.28125D, yb = 0.5D - 0.28125D;
            
            double nxlt = xl * cos - yt * sin;
            double nylt = yt * cos + xl * sin;
            
            double nxrt = xr * cos - yt * sin;
            double nyrt = yt * cos + xr * sin;
            
            double nxlb = xl * cos - yb * sin;
            double nylb = yb * cos + xl * sin;
            
            double nxrb = xr * cos - yb * sin;
            double nyrb = yb * cos + xr * sin;
            
            double mul2 = ((180.0D + direction) % 90.0D) / 90.0D;
            double mul1 = 1.0D - mul2;
            
            double nx1 = nxlt * mul1 + nxlb * mul2;
            double ny1 = nylt * mul1 + nylb * mul2;
            
            double nx2 = nxrb * mul1 + nxrt * mul2;
            double ny2 = nyrb * mul1 + nyrt * mul2;
            
            box = AxisAlignedBB.getBoundingBox(0.5D + nx1, 0.5D + ny1, 0.0D,
                                               0.5D + nx2, 0.5D + ny2, 0.125D);
        }
        box = pside.axes.fromSideCoords(box);
        this.setBlockBounds((float)box.minX, (float)box.minY, (float)box.minZ,
                            (float)box.maxX, (float)box.maxY, (float)box.maxZ);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }
    
    @Override
    public int getRenderType() {
        return -1;
    }
    
    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    @Override
    public boolean isOpaqueCube() {
        return false;
    }
    
    @Override
    public int quantityDropped(Random rand) {
        return 0;
    }
    
    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntityAdvSign tileAS = (TileEntityAdvSign)world.getTileEntity(x, y, z);
        this.dropBlockAsItem(world, x, y, z, ItemAdvSign.getSavedSign(tileAS));
        super.breakBlock(world, x, y, z, block, meta);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {}
    
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return Blocks.planks.getBlockTextureFromSide(side);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getPickBlock(
            MovingObjectPosition target,
            World world,
            int x,
            int y,
            int z,
            EntityPlayer player) {
        TileEntity tile = world.getTileEntity(x, y, z);
        return ItemAdvSign.getSavedSign(tile instanceof TileEntityAdvSign ? (TileEntityAdvSign)tile
                                                                          : null);
    }
    
    protected static enum SignSide {
        DOWN(ForgeDirection.WEST), // -Y
        UP(ForgeDirection.EAST), // +Y
        NORTH(ForgeDirection.WEST), // -Z
        SOUTH(ForgeDirection.EAST), // +Z
        WEST(ForgeDirection.SOUTH), // -X
        EAST(ForgeDirection.NORTH), // +X
        UNKNOWN(ForgeDirection.UNKNOWN);
        
        public final TileOnSide axes;
        
        SignSide(ForgeDirection xDir) {
            this.axes = new TileOnSide(xDir, ForgeDirection.getOrientation(this.ordinal()));
        }
        
        public static SignSide getSide(int side) {
            return values()[side % values().length];
        }
    }
}
