package com.vanym.paniclecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.vanym.paniclecraft.init.ModItems;
import com.vanym.paniclecraft.item.ItemPaintBrush;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;
import com.vanym.paniclecraft.utils.Painting;

public class BlockPaintingFrame extends BlockContainerMod3{
	
	public BlockPaintingFrame(){
		super(Material.wood);
		this.setBlockName("paintingFrame");
		this.setHardness(0.6F);
	}
	
	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_){
		return new TileEntityPaintingFrame();
	}
	
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float p_149727_7_, float p_149727_8_, float p_149727_9_){
		if(!entityPlayer.isSneaking())
			return false;
		TileEntityPaintingFrame tileP = (TileEntityPaintingFrame)world.getTileEntity(x, y, z);
		if(tileP != null){
			if(tileP.getPainting(side) == null)
				return false;
			if(world.isRemote)
				return true;
			if(side == 0){
				int rot = (int)((entityPlayer.rotationYaw + 45.0F) / 90.0F);
				while(rot >= 4)
					rot -= 4;
				switch(rot){
					case 1:
						tileP.getPainting(side).rotatePicRight();
					break;
					case 2:
						tileP.getPainting(side).rotatePic180();
					break;
					case 3:
						tileP.getPainting(side).rotatePicLeft();
					break;
				}
			}
			if(side == 1){
				int rot = (int)((entityPlayer.rotationYaw + 45.0F) / 90.0F);
				while(rot >= 4)
					rot -= 4;
				switch(rot){
					case 1:
						tileP.getPainting(side).rotatePicLeft();
					break;
					case 2:
						tileP.getPainting(side).rotatePic180();
					break;
					case 3:
						tileP.getPainting(side).rotatePicRight();
					break;
				}
			}
			ForgeDirection dir = ForgeDirection.getOrientation(side);
			EntityItem entityItem = new EntityItem(world, x + 0.5D + (dir.offsetX * 0.6), y + 0.5D + (dir.offsetY * 0.6), z + 0.5D + (dir.offsetZ * 0.6), BlockPainting.getSavedPic(tileP.getPainting(side)));
			world.spawnEntityInWorld(entityItem);
			tileP.setPainting(side, null);
		}
		return false;
	}
	
	public int getRenderType(){
		return -1;
	}
	
	public boolean isOpaqueCube(){
		return false;
	}
	
	public boolean renderAsNormalBlock(){
		return false;
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
							if(tile instanceof TileEntityPaintingFrame && ((TileEntityPaintingFrame)tile).getPainting(mc.objectMouseOver.sideHit) != null){
								Vec3 vec = mc.objectMouseOver.hitVec;
								float f = (float)vec.xCoord - (float)par2;
								float f1 = (float)vec.yCoord - (float)par3;
								float f2 = (float)vec.zCoord - (float)par4;
								int side = mc.objectMouseOver.sideHit;
								TileEntityPaintingFrame tileP = (TileEntityPaintingFrame)tile;
								int px = ItemPaintBrush.getXuse(tileP.getPainting(side).getRow(), side, f, f1, f2);
								int py = ItemPaintBrush.getYuse(tileP.getPainting(side).getRow(), side, f, f1, f2);
								double mxdx = (1.0D / tileP.getPainting(side).getRow()) * px;
								double mxdy = (1.0D / tileP.getPainting(side).getRow()) * py;
								double mndx = (1.0D / tileP.getPainting(side).getRow()) * (tileP.getPainting(side).getRow() - px) - (1.0D / tileP.getPainting(side).getRow());
								double mndy = (1.0D / tileP.getPainting(side).getRow()) * (tileP.getPainting(side).getRow() - py) - (1.0D / tileP.getPainting(side).getRow());
								switch(side){
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
	
	public void breakBlock(World world, int x, int y, int z, Block p_149749_5_, int p_149749_6_){
		TileEntityPaintingFrame trpf = (TileEntityPaintingFrame)world.getTileEntity(x, y, z);
		Painting[] pics = trpf.getPaintings();
		for(int i = 0; i < pics.length; i++){
			if(pics[i] != null){
				this.dropBlockAsItem(world, x, y, z, new ItemStack(ModItems.itemPainting));
			}
		}
		super.breakBlock(world, x, y, z, p_149749_5_, p_149749_6_);
	}
	
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int par1, int md){
		return ModItems.blockPainting.getIcon(par1, md);
	}
}
