package com.vanym.paniclecraft.block;

import java.util.Random;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.GUIs;
import com.vanym.paniclecraft.init.ModItems;
import com.vanym.paniclecraft.tileentity.TileEntityCannon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
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
            World par1World,
            int par2,
            int par3,
            int par4,
            EntityPlayer par5EntityPlayer,
            int par6,
            float par7,
            float par8,
            float par9) {
        par5EntityPlayer.openGui(Core.instance, GUIs.CANNON.ordinal(), par1World, par2, par3, par4);
        return true;
    }
    
    @Override
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
        return ModItems.itemCannon;
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int md) {
        return new TileEntityCannon();
    }
    
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(
            World par1World,
            int par2,
            int par3,
            int par4) {
        return null;// TODO
        // return AxisAlignedBB.getBoundingBox((double)par2 + 0.0F, (double)par3
        // + 0.0F, (double)par4 + 0.0F, (double)par2 + 1.0F, (double)par3 +
        // 0.0625F, (double)par4 + 1.0F);
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
        return ForgeDirection.getOrientation(world.getBlockMetadata(x, y, z)) == side.getOpposite();
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int par1, int par2) {
        return Blocks.anvil.getBlockTextureFromSide(par1);
    }
}
