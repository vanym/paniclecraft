package ee_man.mod3.inventory;

import java.util.ArrayList;

import ee_man.mod3.items.utils.IUpgradeForPrivateChest;
import ee_man.mod3.tileEntity.TileEntityPrivateChest;
import net.minecraft.item.ItemStack;

public class InventoryUpgradesPrivateChest extends InventoryPrivateChest{
	
	public TileEntityPrivateChest tile;
	
	public InventoryUpgradesPrivateChest(TileEntityPrivateChest par1Tile){
		super(64, 16);
		tile = par1Tile;
	}
	
	public boolean hasType(int i){
		for(int j = 0; j < this.getSizeInventory(); j++){
			ItemStack is = this.getStackInSlot(j);
			if(is != null)
				if(is.getItem() instanceof IUpgradeForPrivateChest){
					IUpgradeForPrivateChest cuis = (IUpgradeForPrivateChest)is.getItem();
					if(cuis.getType(is) == i)
						return true;
				}
		}
		return false;
	}
	
	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack){
		if(itemstack.getItem() instanceof IUpgradeForPrivateChest){
			IUpgradeForPrivateChest cu = (IUpgradeForPrivateChest)itemstack.getItem();
			return !this.hasType(cu.getType(itemstack));
		}
		else
			return false;
	}
	
	public ArrayList<ItemStack> getNotNullUpgrades(){
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		for(int i = 0; i < this.getSizeInventory(); i++){
			ItemStack is = this.getStackInSlot(i);
			if(is != null)
				list.add(is);
		}
		return list;
	}
	
	public void onInventoryChanged(){
		super.onInventoryChanged();
		if(tile.worldObj != null){
			tile.worldObj.notifyBlockChange(tile.xCoord, tile.yCoord, tile.zCoord, tile.worldObj.getBlockId(tile.xCoord, tile.yCoord, tile.zCoord));
			tile.worldObj.markBlockForUpdate(tile.xCoord, tile.yCoord, tile.zCoord);
		}
	}
}
