package ee_man.mod3.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ee_man.mod3.Core;
import ee_man.mod3.tileEntity.TileEntityCannon;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class BlockCannon extends BlockContainer{
	
	private Class<? extends TileEntity> tileClass;
	
	public BlockCannon(int par1, Class<? extends TileEntity> par2Class){
		super(par1, Material.iron);
		tileClass = par2Class;
	}
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister){
	}
	
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int par1, int par2){
		return Block.anvil.getBlockTextureFromSide(par1);
	}
	
	public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6){
		TileEntityCannon tileCannon = (TileEntityCannon)par1World.getBlockTileEntity(par2, par3, par4);
		if(tileCannon != null){
			if(tileCannon.item != null){
				this.dropBlockAsItem_do(par1World, par2, par3, par4, tileCannon.item);
				tileCannon.item = null;
			}
		}
		super.breakBlock(par1World, par2, par3, par4, par5, par6);
	}
	
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9){
		if(par1World.getBlockTileEntity(par2, par3, par4) instanceof TileEntityCannon){
			par5EntityPlayer.openGui(Core.instance, 2, par1World, par2, par3, par4);
			return true;
		}
		return false;
	}
	
	public TileEntity createNewTileEntity(World par1World){
		try{
			return tileClass.newInstance();
		} catch(Exception var3){
			throw new RuntimeException(var3);
		}
	}
	
	public boolean renderAsNormalBlock(){
		return false;
	}
	
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4){
		return AxisAlignedBB.getAABBPool().getAABB((double)par2 + 0.0F, (double)par3 + 0.0F, (double)par4 + 0.0F, (double)par2 + 1.0F, (double)par3 + 0.0625F, (double)par4 + 1.0F);
	}
	
	@SideOnly(Side.CLIENT)
	public int idPicked(World par1World, int par2, int par3, int par4){
		return Core.itemCannonBlock.itemID;
	}
	
	public int idDropped(int par1, Random par2Random, int par3){
		return Core.itemCannonBlock.itemID;
	}
	
	public boolean isOpaqueCube(){
		return false;
	}
	
	public int getRenderType(){
		return -1;
	}
}
