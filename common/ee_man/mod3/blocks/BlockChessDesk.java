package ee_man.mod3.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import ee_man.mod3.Core;
import ee_man.mod3.DefaultProperties;
import ee_man.mod3.tileEntity.TileEntityChessDesk;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class BlockChessDesk extends BlockContainer{
	
	public BlockChessDesk(int par1){
		super(par1, Material.wood);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.1875F, 1.0F);
	}
	
	@Override
	public TileEntity createNewTileEntity(World var1){
		return new TileEntityChessDesk();
	}
	
	public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6){
		this.dropBlockAsItem_do(par1World, par2, par3, par4, this.getPickBlock(null, par1World, par2, par3, par4));
		super.breakBlock(par1World, par2, par3, par4, par5, par6);
	}
	
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9){
		if(par1World.getBlockTileEntity(par2, par3, par4) instanceof TileEntityChessDesk){
			par5EntityPlayer.openGui(Core.instance, 1, par1World, par2, par3, par4);
			return true;
		}
		return false;
	}
	
	public boolean renderAsNormalBlock(){
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister){
		this.blockIcon = iconRegister.registerIcon(DefaultProperties.TEXTURE_ID + ":" + this.getUnlocalizedName());
	}
	
	public boolean isOpaqueCube(){
		return false;
	}
	
	public int getRenderType(){
		return -1;
	}
	
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z){
		TileEntityChessDesk tile = (TileEntityChessDesk)world.getBlockTileEntity(x, y, z);
		ItemStack itemS = new ItemStack(Core.itemChessDesk);
		if(tile == null)
			return itemS;
		NBTTagCompound var1 = new NBTTagCompound();
		itemS.setTagCompound(var1);
		var1.setByteArray("desk", tile.desk.desk);
		var1.setByte("lastFrom", tile.desk.lastFrom);
		var1.setByte("lastTo", tile.desk.lastTo);
		var1.setBoolean("isWhiteTurn", tile.desk.isWhiteTurn);
		var1.setString("whitePlayer", tile.whitePlayer);
		var1.setString("blackPlayer", tile.blackPlayer);
		if(tile.name != null){
			NBTTagCompound var2 = var1.getCompoundTag("display");
			if(!var1.hasKey("display"))
				var1.setCompoundTag("display", var2);
			var2.setString("Name", tile.name);
		}
		return itemS;
	}
	
	public int idDropped(int par1, Random par2Random, int par3){
		return 0;
	}
}
