package com.vanym.paniclecraft.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.GUIs;
import com.vanym.paniclecraft.init.ModItems;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;

public class BlockChessDesk extends BlockContainerMod3{
	
	public BlockChessDesk(){
		super(Material.wood);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.1875F, 1.0F);
		this.setBlockName("chessDesk");
		this.setHardness(0.5F);
	}
	
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9){
		par5EntityPlayer.openGui(Core.instance, GUIs.CHESS.ordinal(), par1World, par2, par3, par4);
		return true;
	}
	
	public void breakBlock(World world, int x, int y, int z, Block p_149749_5_, int p_149749_6_){
		this.dropBlockAsItem(world, x, y, z, getSavedDesk((TileEntityChessDesk)world.getTileEntity(x, y, z)));
		super.breakBlock(world, x, y, z, p_149749_5_, p_149749_6_);
	}
	
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_){
		return null;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int md){
		return new TileEntityChessDesk();
	}
	
	public boolean renderAsNormalBlock(){
		return false;
	}
	
	public boolean isOpaqueCube(){
		return false;
	}
	
	public int getRenderType(){
		return -1;
	}
	
	public static ItemStack getSavedDesk(TileEntityChessDesk tileCD){
		ItemStack itemS = new ItemStack(ModItems.itemChessDesk);
		if(tileCD == null)
			return itemS;
		NBTTagCompound var1 = new NBTTagCompound();
		itemS.setTagCompound(var1);
		NBTTagCompound var2 = new NBTTagCompound();
		tileCD.writeToNBT(var2);
		var2.removeTag("x");
		var2.removeTag("y");
		var2.removeTag("z");
		var1.setTag("ChessData", var2);
		return itemS;
	}
	
	@SideOnly(Side.CLIENT)
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z){
		TileEntityChessDesk tile = (TileEntityChessDesk)world.getTileEntity(x, y, z);
		return getSavedDesk(tile);
	}
}
