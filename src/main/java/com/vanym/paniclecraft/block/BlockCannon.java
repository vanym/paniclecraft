package com.vanym.paniclecraft.block;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.core.GUIs;
import com.vanym.paniclecraft.inventory.InventoryUtils;
import com.vanym.paniclecraft.tileentity.TileEntityCannon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockCannon extends BlockContainerMod3 {
    
    public BlockCannon() {
        super(Material.anvil);
        this.setBlockName("cannon");
        this.setHardness(1.5F);
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
        if (!world.isRemote) {
            player.openGui(Core.instance, GUIs.CANNON.ordinal(), world, x, y, z);
        }
        return true;
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityCannon();
    }
    
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }
    
    @Override
    public boolean isOpaqueCube() {
        return false;
    }
    
    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    @Override
    public int getRenderType() {
        return -1;
    }
    
    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return side == ForgeDirection.DOWN;
    }
    
    @Override
    public void onBlockPlacedBy(
            World world,
            int x,
            int y,
            int z,
            EntityLivingBase entity,
            ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, entity, stack);
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile != null && tile instanceof TileEntityCannon) {
            TileEntityCannon tileCannon = (TileEntityCannon)tile;
            double direction = Math.round(180.0D + entity.rotationYaw);
            tileCannon.setDirection(direction);
            double height = Math.round(entity.rotationPitch);
            tileCannon.setHeight(Math.max(0.0D, Math.min(90.0D, height)));
        }
    }
    
    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityCannon) {
            TileEntityCannon tileCannon = (TileEntityCannon)tile;
            InventoryUtils.inventoryToStream(tileCannon)
                          .map(sk->new EntityItem(world, x + 0.5F, y + 0.3F, z + 0.5F, sk.copy()))
                          .forEach(world::spawnEntityInWorld);
        }
        super.breakBlock(world, x, y, z, block, meta);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {}
    
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return Blocks.anvil.getBlockTextureFromSide(side);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public String getItemIconName() {
        return DEF.MOD_ID + ":" + this.getName();
    }
}
