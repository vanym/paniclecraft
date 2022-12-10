package ee_man.mod3.items;

import ee_man.mod3.Core;
import ee_man.mod3.tileEntity.TileEntityPainting;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ItemPaintingMover extends ItemMod3{
	
	public ItemPaintingMover(int par1, int par2MaxDem){
		super(par1);
		this.setMaxDamage(par2MaxDem);
		this.setMaxStackSize(1);
	}
	
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10){
		if(par3World.getBlockId(par4, par5, par6) == Core.blockPainting.blockID){
			if(!par3World.isRemote){
				TileEntity tile = par3World.getBlockTileEntity(par4, par5, par6);
				if(tile != null && tile instanceof TileEntityPainting){
					TileEntityPainting tileP = (TileEntityPainting)tile;
					ItemStack item = new ItemStack(Core.itemPaintingBlock);
					item.setTagCompound(new NBTTagCompound());
					NBTTagCompound nbt = item.getTagCompound();
					NBTTagCompound pdnbt = new NBTTagCompound();
					nbt.setCompoundTag("PaintingData", pdnbt);
					tileP.writeToNBT(pdnbt);
					pdnbt.removeTag("x");
					pdnbt.removeTag("y");
					pdnbt.removeTag("z");
					EntityItem entityItem = new EntityItem(par3World, par4 + 0.5, par5 + 0.5, par6 + 0.5, item);
					entityItem.delayBeforeCanPickup = 15;
					par1ItemStack.damageItem(1, par2EntityPlayer);
					par3World.setBlockToAir(par4, par5, par6);
					par3World.spawnEntityInWorld(entityItem);
				}
			}
			return true;
		}
		return false;
	}
}
