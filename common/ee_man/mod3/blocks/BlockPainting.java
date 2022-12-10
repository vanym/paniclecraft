package ee_man.mod3.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ee_man.mod3.Core;
import ee_man.mod3.DefaultProperties;
import ee_man.mod3.items.ItemPaintBrush;
import ee_man.mod3.tileEntity.TileEntityPainting;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPainting extends BlockContainer{
	
	public static boolean specialBoundingBox = true;
	
	public BlockPainting(int par1){
		super(par1, Material.wood);
	}
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister){
		this.blockIcon = iconRegister.registerIcon(DefaultProperties.TEXTURE_ID + ":" + this.getUnlocalizedName());
	}
	
	public boolean renderAsNormalBlock(){
		return false;
	}
	
	public boolean getBlocksMovement(IBlockAccess par1IBlockAccess, int par2, int par3, int par4){
		return true;
	}
	
	public int getRenderType(){
		return -1;
	}
	
	public boolean isOpaqueCube(){
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox(World par1World, int par2, int par3, int par4){
		this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
		return super.getSelectedBoundingBoxFromPool(par1World, par2, par3, par4);
	}
	
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int par2, int par3, int par4){
		this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
		if(specialBoundingBox){
			Minecraft mc = Minecraft.getMinecraft();
			ItemStack is = mc.thePlayer.inventory.getCurrentItem();
			if(is != null)
				if(is.getItem() instanceof ItemPaintBrush){
					if(mc.objectMouseOver != null)
						if(mc.objectMouseOver.typeOfHit == EnumMovingObjectType.TILE){
							TileEntity tile = par1World.getBlockTileEntity(par2, par3, par4);
							if(tile instanceof TileEntityPainting && mc.objectMouseOver.sideHit == tile.getBlockMetadata()){
								Vec3 vec = mc.objectMouseOver.hitVec;
								float f = (float)vec.xCoord - (float)par2;
								float f1 = (float)vec.yCoord - (float)par3;
								float f2 = (float)vec.zCoord - (float)par4;
								TileEntityPainting tileP = (TileEntityPainting)tile;
								int px = ItemPaintBrush.getXuse(tileP.Row, mc.objectMouseOver.sideHit, f, f1, f2);
								int py = ItemPaintBrush.getYuse(tileP.Row, mc.objectMouseOver.sideHit, f, f1, f2);
								double mxdx = (1.0D / tileP.Row) * px;
								double mxdy = (1.0D / tileP.Row) * py;
								double mndx = (1.0D / tileP.Row) * (tileP.Row - px) - (1.0D / tileP.Row);
								double mndy = (1.0D / tileP.Row) * (tileP.Row - py) - (1.0D / tileP.Row);
								switch(tileP.getBlockMetadata()){
									case 2:
										return AxisAlignedBB.getAABBPool().getAABB((double)par2 + this.minX + mndx, (double)par3 + this.minY + mndy, (double)par4 + this.minZ, (double)par2 + this.maxX - mxdx, (double)par3 + this.maxY - mxdy, (double)par4 + this.maxZ);
									case 3:
										return AxisAlignedBB.getAABBPool().getAABB((double)par2 + this.minX + mxdx, (double)par3 + this.minY + mndy, (double)par4 + this.minZ, (double)par2 + this.maxX - mndx, (double)par3 + this.maxY - mxdy, (double)par4 + this.maxZ);
									case 4:
										return AxisAlignedBB.getAABBPool().getAABB((double)par2 + this.minX, (double)par3 + this.minY + mndy, (double)par4 + this.minZ + mxdx, (double)par2 + this.maxX, (double)par3 + this.maxY - mxdy, (double)par4 + this.maxZ - mndx);
									case 5:
										return AxisAlignedBB.getAABBPool().getAABB((double)par2 + this.minX, (double)par3 + this.minY + mndy, (double)par4 + this.minZ + mndx, (double)par2 + this.maxX, (double)par3 + this.maxY - mxdy, (double)par4 + this.maxZ - mxdx);
								}
							}
						}
				}
		}
		return super.getSelectedBoundingBoxFromPool(par1World, par2, par3, par4);
	}
	
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4){
		return null;
	}
	
	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4){
		int var5 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
		float var6 = 0.0F;
		float var7 = 1.0F;
		float var8 = 0.0F;
		float var9 = 1.0F;
		float var10 = 0.0625F;
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
	
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5){
		boolean var6 = false;
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
		if(var6){
			this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
			par1World.setBlockToAir(par2, par3, par4);
		}
		
		super.onNeighborBlockChange(par1World, par2, par3, par4, par5);
	}
	
	@Override
	public TileEntity createNewTileEntity(World var1){
		return new TileEntityPainting();
	}
	
	@SideOnly(Side.CLIENT)
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z){
		TileEntityPainting tile = (TileEntityPainting)world.getBlockTileEntity(x, y, z);
		ItemStack itemS = new ItemStack(Core.itemPaintingBlock);
		if(tile == null)
			return itemS;
		NBTTagCompound var1 = new NBTTagCompound();
		itemS.setTagCompound(var1);
		NBTTagCompound var2 = new NBTTagCompound();
		var1.setTag("PaintingData", var2);
		tile.writeToNBT(var2);
		var2.removeTag("x");
		var2.removeTag("y");
		var2.removeTag("z");
		return itemS;
	}
	
	public int idDropped(int par1, Random par2Random, int par3){
		return Core.itemPaintingBlock.itemID;
	}
}
