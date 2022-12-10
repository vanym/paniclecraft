package ee_man.mod3.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;

import ee_man.mod3.Core;
import ee_man.mod3.tileEntity.TileEntityAdvSign;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

public class BlockAdvSign extends BlockContainer{
	
	public boolean isFreestanding;
	
	public boolean rightClickLink;
	
	public int itemSignId;
	
	public BlockAdvSign(int par1, boolean par2, boolean par3, int par4Id){
		super(par1, Material.wood);
		this.isFreestanding = par2;
		this.rightClickLink = par3;
		this.itemSignId = par4Id;
		float var4 = 0.25F;
		float var5 = 1.0F;
		this.setBlockBounds(0.5F - var4, 0.0F, 0.5F - var4, 0.5F + var4, var5, 0.5F + var4);
	}
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister){
	}
	
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int par1, int par2){
		return Block.planks.getBlockTextureFromSide(par1);
	}
	
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4){
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int par2, int par3, int par4){
		this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
		return super.getSelectedBoundingBoxFromPool(par1World, par2, par3, par4);
	}
	
	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4){
		if(!this.isFreestanding){
			int var5 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
			float var6 = 0.28125F;
			float var7 = 0.78125F;
			float var8 = 0.0F;
			float var9 = 1.0F;
			float var10 = 0.125F;
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			
			if(var5 == 2){
				this.setBlockBounds(var8, var6, 1.0F - var10, var9, var7, 1.0F);
			}
			
			if(var5 == 3){
				this.setBlockBounds(var8, var6, 0.0F, var9, var7, var10);
			}
			
			if(var5 == 4){
				this.setBlockBounds(1.0F - var10, var6, var8, 1.0F, var7, var9);
			}
			
			if(var5 == 5){
				this.setBlockBounds(0.0F, var6, var8, var10, var7, var9);
			}
		}
	}
	
	public int getRenderType(){
		return -1;
	}
	
	public boolean renderAsNormalBlock(){
		return false;
	}
	
	public boolean getBlocksMovement(IBlockAccess par1IBlockAccess, int par2, int par3, int par4){
		return true;
	}
	
	public boolean isOpaqueCube(){
		return false;
	}
	
	public TileEntity createNewTileEntity(World par1World){
		return new TileEntityAdvSign();
	}
	
	public int idDropped(int par1, Random par2Random, int par3){
		return Core.itemAdvSign.itemID;
	}
	
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9){
		if(this.rightClickLink){
			int x = par2;
			int y = par3;
			int z = par4;
			if(!this.isFreestanding){
				switch(par1World.getBlockMetadata(par2, par3, par4)){
					case 2:
						z++;
					break;
					case 3:
						z--;
					break;
					case 4:
						x++;
					break;
					case 5:
						x--;
					break;
				}
			}
			else
				y--;
			int backId = par1World.getBlockId(x, y, z);
			if(backId > 0 && !(Block.blocksList[backId] instanceof BlockAdvSign)){
				if(!ForgeEventFactory.onPlayerInteract(par5EntityPlayer, Action.RIGHT_CLICK_BLOCK, x, y, z, par6).isCanceled())
					return Block.blocksList[backId].onBlockActivated(par1World, x, y, z, par5EntityPlayer, par6, par7, par8, par9);
			}
		}
		return false;
	}
	
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5){
		boolean var6 = false;
		
		if(this.isFreestanding){
			if(!par1World.getBlockMaterial(par2, par3 - 1, par4).isSolid()){
				var6 = true;
			}
		}
		else{
			int var7 = par1World.getBlockMetadata(par2, par3, par4);
			var6 = true;
			
			if(var7 == 2 && par1World.getBlockMaterial(par2, par3, par4 + 1).isSolid()){
				var6 = false;
			}
			
			if(var7 == 3 && par1World.getBlockMaterial(par2, par3, par4 - 1).isSolid()){
				var6 = false;
			}
			
			if(var7 == 4 && par1World.getBlockMaterial(par2 + 1, par3, par4).isSolid()){
				var6 = false;
			}
			
			if(var7 == 5 && par1World.getBlockMaterial(par2 - 1, par3, par4).isSolid()){
				var6 = false;
			}
		}
		
		if(var6){
			this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
			par1World.setBlockToAir(par2, par3, par4);
		}
		
		super.onNeighborBlockChange(par1World, par2, par3, par4, par5);
	}
	
	@SideOnly(Side.CLIENT)
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z){
		TileEntityAdvSign tile = (TileEntityAdvSign)world.getBlockTileEntity(x, y, z);
		ItemStack itemS = new ItemStack(Core.itemAdvSign);
		if(tile == null)
			return itemS;
		NBTTagCompound var1 = new NBTTagCompound();
		itemS.setTagCompound(var1);
		var1.setString("SignText", tile.signText);
		return itemS;
	}
}
