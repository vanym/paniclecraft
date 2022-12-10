package ee_man.mod3.container.slot;

import ee_man.mod3.tileEntity.TileEntityPrivateChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotUpgrade extends Slot{
	
	public TileEntityPrivateChest tile;
	
	public SlotUpgrade(TileEntityPrivateChest par1Tile, int par2, int par3, int par4){
		super(par1Tile.inventoryUpgrades, par2, par3, par4);
		tile = par1Tile;
	}
	
	public boolean isItemValid(ItemStack par1ItemStack){
		return this.inventory.isItemValidForSlot(this.slotNumber, par1ItemStack);
	}
	
	public boolean canTakeStack(EntityPlayer par1EntityPlayer){
		if(tile.select == this.getSlotIndex()){
			return false;
		}
		else
			return true;
	}
}
