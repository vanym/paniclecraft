package ee_man.mod3.items;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ee_man.mod3.Core;
import ee_man.mod3.tileEntity.TileEntityPainting;
import ee_man.mod3.utils.Localization;
import ee_man.mod3.utils.MainUtils.PicData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;

public class ItemPainting extends ItemMod3{
	
	public static ArrayList<ItemStack> pictures;
	
	public ItemPainting(int par1){
		super(par1);
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4){
		if(par1ItemStack.hasTagCompound()){
			NBTTagCompound tag = par1ItemStack.getTagCompound();
			if(tag.hasKey("PaintingData")){
				par3List.add(Localization.get("text.paintingHaveSave"));
			}
		}
	}
	
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10){
		if(par7 == 0 || par7 == 1){
			return false;
		}
		else
			if(!par3World.getBlockMaterial(par4, par5, par6).isSolid()){
				return false;
			}
			else{
				if(par7 == 2){
					--par6;
				}
				
				if(par7 == 3){
					++par6;
				}
				
				if(par7 == 4){
					--par4;
				}
				
				if(par7 == 5){
					++par4;
				}
				
				if(!par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack)){
					return false;
				}
				else
					if(!Core.blockAdvSignPost.canPlaceBlockAt(par3World, par4, par5, par6)){
						return false;
					}
					else{
						par3World.setBlock(par4, par5, par6, Core.blockPainting.blockID, par7, 3);
						
						TileEntityPainting tileP = (TileEntityPainting)par3World.getBlockTileEntity(par4, par5, par6);
						
						for(int i = 0; i < tileP.pic.length; i++){
							tileP.pic[i] = (byte)(ItemPaintBrush.DEFAULT_COLOR_RGB);
						}
						
						if(par1ItemStack.hasTagCompound()){
							NBTTagCompound tag = par1ItemStack.getTagCompound();
							if(tag.hasKey("PaintingData")){
								NBTTagCompound tagData = tag.getCompoundTag("PaintingData");
								if(!tagData.hasNoTags()){
									tagData.setInteger("x", par4);
									tagData.setInteger("y", par5);
									tagData.setInteger("z", par6);
									tileP.readFromNBT(tagData);
								}
							}
						}
						
						--par1ItemStack.stackSize;
						
						return true;
					}
			}
	}
	
	public static ArrayList<ItemStack> getItemsFromPics(ArrayList<PicData> pics){
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		Iterator<PicData> ir = pics.iterator();
		
		while(ir.hasNext()){
			PicData pic = ir.next();
			ItemStack is = new ItemStack(Core.itemPaintingBlock);
			NBTTagCompound nbt1 = new NBTTagCompound();
			NBTTagCompound nbt2 = new NBTTagCompound();
			is.setTagCompound(nbt1);
			nbt1.setTag("PaintingData", nbt2);
			nbt2.setInteger("Row", pic.row);
			nbt2.setByteArray("pic[" + pic.row + "]", pic.byteArray);
			nbt2.setInteger("BrushRadius", 0);
			nbt2.setIntArray("NoDrawPixels[0]", new int[]{0});
			nbt2.setIntArray("NoDrawPixels[1]", new int[]{0});
			if(pic.name != null)
				is.setItemName("\u00a7r" + pic.name);
			items.add(is);
		}
		return items;
	}
	
	public WeightedRandomChestContent getChestGenBase(ChestGenHooks chest, Random rnd, WeightedRandomChestContent original){
		if(!pictures.isEmpty())
			original.theItemId = pictures.get(Math.abs(rnd.nextInt() % pictures.size()));
		return original;
	}
}
