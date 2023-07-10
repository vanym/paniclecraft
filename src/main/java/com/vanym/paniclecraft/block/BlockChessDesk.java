package com.vanym.paniclecraft.block;

import java.util.Random;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.GUIs;
import com.vanym.paniclecraft.item.ItemChessDesk;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;
import com.vanym.paniclecraft.utils.WorldUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class BlockChessDesk extends BlockContainerMod3 {
    
    public BlockChessDesk() {
        super(Material.wood);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 3.0F / 16.0F, 1.0F);
        this.setRegistryName("chess_desk");
        this.setHardness(0.5F);
    }
    
    @Override
    public Class<? extends ItemBlock> getItemClass() {
        return ItemChessDesk.class;
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityChessDesk();
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
    public int quantityDropped(Random rand) {
        return 0;
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
        player.openGui(Core.instance, GUIs.CHESS.ordinal(), world, x, y, z);
        return true;
    }
    
    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntityChessDesk tileCD = (TileEntityChessDesk)world.getTileEntity(x, y, z);
        this.dropBlockAsItem(world, x, y, z, ItemChessDesk.getSavedDesk(tileCD));
        super.breakBlock(world, x, y, z, block, meta);
    }
    
    @Override
    public void onBlockPlacedBy(
            World world,
            int x,
            int y,
            int z,
            EntityLivingBase entity,
            ItemStack stack) {
        ItemChessDesk.getMoves(stack).ifPresent(list-> {
            WorldUtils.getTileEntity(world, x, y, z, TileEntityChessDesk.class)
                      .ifPresent(tileCD->tileCD.readMovesFromNBT(list));
        });
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        TileEntityChessDesk tile = (TileEntityChessDesk)world.getTileEntity(x, y, z);
        return ItemChessDesk.getSavedDesk(tile);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public String getItemIconName() {
        return this.getTextureName();
    }
}
