package ee_man.mod3.block;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ee_man.mod3.init.ModItems;
import ee_man.mod3.item.ItemPaintBrush;
import ee_man.mod3.tileentity.TileEntityPainting;
import ee_man.mod3.utils.Painting;

public class BlockPainting extends BlockContainerMod3{
	
	public BlockPainting(){
		super(Material.wood);
		this.setBlockName("painting");
		this.setHardness(0.4F);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int md){
		return new TileEntityPainting();
	}
	
	public int getRenderType(){
		return -1;
	}
	
	public boolean isOpaqueCube(){
		return false;
	}
	
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side){
		return ForgeDirection.getOrientation(world.getBlockMetadata(x, y, z)) == side.getOpposite();
	}
	
	public boolean renderAsNormalBlock(){
		return false;
	}
	
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4){
		this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
		return super.getCollisionBoundingBoxFromPool(par1World, par2, par3, par4);
		// return null;
	}
	
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_){
		return ModItems.itemPainting;
	}
	
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_){
		if(!entityPlayer.isSneaking())
			return false;
		if(!world.isRemote){
			TileEntityPainting tileP = (TileEntityPainting)world.getTileEntity(x, y, z);
			int md = world.getBlockMetadata(x, y, z);
			if(md == 0){
				int rot = (int)((entityPlayer.rotationYaw + 45.0F) / 90.0F);
				while(rot >= 4)
					rot -= 4;
				switch(rot){
					case 1:
						tileP.getPainting(md).rotatePicRight();
					break;
					case 2:
						tileP.getPainting(md).rotatePic180();
					break;
					case 3:
						tileP.getPainting(md).rotatePicLeft();
					break;
				}
			}
			if(md == 1){
				int rot = (int)((entityPlayer.rotationYaw + 45.0F) / 90.0F);
				while(rot >= 4)
					rot -= 4;
				switch(rot){
					case 1:
						tileP.getPainting(md).rotatePicLeft();
					break;
					case 2:
						tileP.getPainting(md).rotatePic180();
					break;
					case 3:
						tileP.getPainting(md).rotatePicRight();
					break;
				}
			}
			EntityItem entityItem = new EntityItem(world, x + 0.5D, y + 0.5D, z + 0.5D, getSavedPic(tileP.getPainting(md)));
			world.spawnEntityInWorld(entityItem);
			world.setBlockToAir(x, y, z);
		}
		return true;
	}
	
	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4){
		int var5 = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
		float var6 = 0.0F;
		float var7 = 1.0F;
		float var8 = 0.0F;
		float var9 = 1.0F;
		float var10 = 0.0625F;
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		
		switch(var5){
			case 0:
				this.setBlockBounds(var8, 1.0F - var10, var6, var9, var7, 1.0F);
				return;
			case 1:
				this.setBlockBounds(var8, var6, 0.0F, var9, var10, 1.0F);
				return;
			case 2:
				this.setBlockBounds(var8, var6, 1.0F - var10, var9, var7, 1.0F);
				return;
			case 3:
				this.setBlockBounds(var8, var6, 0.0F, var9, var7, var10);
				return;
			case 4:
				this.setBlockBounds(1.0F - var10, var6, var8, 1.0F, var7, var9);
				return;
			case 5:
				this.setBlockBounds(0.0F, var6, var8, var10, var7, var9);
				return;
		}
	}
	
	public static ItemStack getSavedPic(Painting pic){
		ItemStack itemS = new ItemStack(ModItems.itemPainting);
		if(pic == null)
			return itemS;
		NBTTagCompound var1 = new NBTTagCompound();
		NBTTagCompound var2 = new NBTTagCompound();
		pic.writeToNBT(var2);
		itemS.setTagCompound(var1);
		var1.setTag("PaintingData", var2);
		return itemS;
	}
	
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox(World par1World, int par2, int par3, int par4){
		this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
		return super.getSelectedBoundingBoxFromPool(par1World, par2, par3, par4);
	}
	
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int par2, int par3, int par4){
		this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
		if(Painting.specialBoundingBox){
			Minecraft mc = Minecraft.getMinecraft();
			ItemStack is = mc.thePlayer.inventory.getCurrentItem();
			if(is != null)
				if(is.getItem() instanceof ItemPaintBrush && (is.getItemDamage() == 0 || is.getItemDamage() == 1)){
					if(mc.objectMouseOver != null)
						if(mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK){
							TileEntity tile = par1World.getTileEntity(par2, par3, par4);
							if(tile instanceof TileEntityPainting && mc.objectMouseOver.sideHit == tile.getBlockMetadata()){
								Vec3 vec = mc.objectMouseOver.hitVec;
								float f = (float)vec.xCoord - (float)par2;
								float f1 = (float)vec.yCoord - (float)par3;
								float f2 = (float)vec.zCoord - (float)par4;
								int side = mc.objectMouseOver.sideHit;
								TileEntityPainting tileP = (TileEntityPainting)tile;
								int px = ItemPaintBrush.getXuse(tileP.getPainting(side).getRow(), mc.objectMouseOver.sideHit, f, f1, f2);
								int py = ItemPaintBrush.getYuse(tileP.getPainting(side).getRow(), mc.objectMouseOver.sideHit, f, f1, f2);
								double mxdx = (1.0D / tileP.getPainting(side).getRow()) * px;
								double mxdy = (1.0D / tileP.getPainting(side).getRow()) * py;
								double mndx = (1.0D / tileP.getPainting(side).getRow()) * (tileP.getPainting(side).getRow() - px) - (1.0D / tileP.getPainting(side).getRow());
								double mndy = (1.0D / tileP.getPainting(side).getRow()) * (tileP.getPainting(side).getRow() - py) - (1.0D / tileP.getPainting(side).getRow());
								switch(tileP.getBlockMetadata()){
									case 0:
										return AxisAlignedBB.getBoundingBox((double)par2 + this.minX + mndx, (double)par3 + this.minY, (double)par4 + this.minZ + mxdy, (double)par2 + this.maxX - mxdx, (double)par3 + this.maxY, (double)par4 + this.maxZ - mndy);
									case 1:
										return AxisAlignedBB.getBoundingBox((double)par2 + this.minX + mndx, (double)par3 + this.minY, (double)par4 + this.minZ + mndy, (double)par2 + this.maxX - mxdx, (double)par3 + this.maxY, (double)par4 + this.maxZ - mxdy);
									case 2:
										return AxisAlignedBB.getBoundingBox((double)par2 + this.minX + mndx, (double)par3 + this.minY + mndy, (double)par4 + this.minZ, (double)par2 + this.maxX - mxdx, (double)par3 + this.maxY - mxdy, (double)par4 + this.maxZ);
									case 3:
										return AxisAlignedBB.getBoundingBox((double)par2 + this.minX + mxdx, (double)par3 + this.minY + mndy, (double)par4 + this.minZ, (double)par2 + this.maxX - mndx, (double)par3 + this.maxY - mxdy, (double)par4 + this.maxZ);
									case 4:
										return AxisAlignedBB.getBoundingBox((double)par2 + this.minX, (double)par3 + this.minY + mndy, (double)par4 + this.minZ + mxdx, (double)par2 + this.maxX, (double)par3 + this.maxY - mxdy, (double)par4 + this.maxZ - mndx);
									case 5:
										return AxisAlignedBB.getBoundingBox((double)par2 + this.minX, (double)par3 + this.minY + mndy, (double)par4 + this.minZ + mndx, (double)par2 + this.maxX, (double)par3 + this.maxY - mxdy, (double)par4 + this.maxZ - mxdx);
								}
							}
						}
				}
		}
		return super.getSelectedBoundingBoxFromPool(par1World, par2, par3, par4);
	}
	
	@SideOnly(Side.CLIENT)
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z){
		TileEntityPainting tile = (TileEntityPainting)world.getTileEntity(x, y, z);
		return getSavedPic(tile.getPainting(tile.getBlockMetadata()));
	}
}
