package ee_man.mod3.inventory;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ReportedException;

public class InventoryPrivateChest implements IInventory{
	
	public ItemStack[] items;
	
	private final int stackLimit;
	
	public InventoryPrivateChest(int par1StackLimit, int par2ItemsLength){
		stackLimit = par1StackLimit;
		items = new ItemStack[par2ItemsLength];
	}
	
	@Override
	public int getSizeInventory(){
		return items.length;
	}
	
	@Override
	public ItemStack getStackInSlot(int i){
		return items[i];
	}
	
	@Override
	public ItemStack decrStackSize(int i, int j){
		
		if(this.items[i] != null){
			ItemStack itemstack;
			
			if(this.items[i].stackSize <= j){
				itemstack = this.items[i];
				this.items[i] = null;
				this.onInventoryChanged();
				return itemstack;
			}
			else{
				itemstack = this.items[i].splitStack(j);
				
				if(this.items[i].stackSize == 0){
					this.items[i] = null;
				}
				
				this.onInventoryChanged();
				return itemstack;
			}
		}
		else{
			return null;
		}
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int i){
		if(this.items[i] != null){
			ItemStack itemstack = this.items[i];
			this.items[i] = null;
			return itemstack;
		}
		else{
			return null;
		}
	}
	
	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack){
		this.items[i] = itemstack;
		
		if(itemstack != null && itemstack.stackSize > this.getInventoryStackLimit()){
			itemstack.stackSize = this.getInventoryStackLimit();
		}
		
		this.onInventoryChanged();
	}
	
	@Override
	public String getInvName(){
		return null;
	}
	
	@Override
	public boolean isInvNameLocalized(){
		return false;
	}
	
	@Override
	public int getInventoryStackLimit(){
		return this.stackLimit;
	}
	
	@Override
	public void onInventoryChanged(){
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer){
		return true;
	}
	
	@Override
	public void openChest(){
	}
	
	@Override
	public void closeChest(){
	}
	
	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack){
		return true;
	}
	
	public boolean addItemStackToInventory(ItemStack par1ItemStack){
		if(par1ItemStack == null){
			return false;
		}
		else{
			try{
				int i;
				
				if(par1ItemStack.isItemDamaged()){
					i = this.getFirstEmptyStack();
					
					if(i >= 0){
						this.items[i] = ItemStack.copyItemStack(par1ItemStack);
						this.items[i].animationsToGo = 5;
						par1ItemStack.stackSize = 0;
						return true;
					}
					else
						return false;
					
				}
				else{
					do{
						i = par1ItemStack.stackSize;
						par1ItemStack.stackSize = this.storePartialItemStack(par1ItemStack);
					} while(par1ItemStack.stackSize > 0 && par1ItemStack.stackSize < i);
					
					return par1ItemStack.stackSize < i;
					
				}
			} catch(Throwable throwable){
				CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Adding item to inventory");
				CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being added");
				crashreportcategory.addCrashSection("Item ID", Integer.valueOf(par1ItemStack.itemID));
				crashreportcategory.addCrashSection("Item data", Integer.valueOf(par1ItemStack.getItemDamage()));
				throw new ReportedException(crashreport);
			}
		}
	}
	
	public int getFirstEmptyStack(){
		for(int i = 0; i < this.items.length; ++i){
			if(this.items[i] == null){
				return i;
			}
		}
		
		return -1;
	}
	
	private int storePartialItemStack(ItemStack par1ItemStack){
		int i = par1ItemStack.itemID;
		int j = par1ItemStack.stackSize;
		int k;
		
		if(par1ItemStack.getMaxStackSize() == 1){
			k = this.getFirstEmptyStack();
			
			if(k < 0){
				return j;
			}
			else{
				if(this.items[k] == null){
					this.items[k] = ItemStack.copyItemStack(par1ItemStack);
				}
				
				return 0;
			}
		}
		else{
			k = this.storeItemStack(par1ItemStack);
			
			if(k < 0){
				k = this.getFirstEmptyStack();
			}
			
			if(k < 0){
				return j;
			}
			else{
				if(this.items[k] == null){
					this.items[k] = new ItemStack(i, 0, par1ItemStack.getItemDamage());
					
					if(par1ItemStack.hasTagCompound()){
						this.items[k].setTagCompound((NBTTagCompound)par1ItemStack.getTagCompound().copy());
					}
				}
				
				int l = j;
				
				if(j > this.items[k].getMaxStackSize() - this.items[k].stackSize){
					l = this.items[k].getMaxStackSize() - this.items[k].stackSize;
				}
				
				if(l > this.getInventoryStackLimit() - this.items[k].stackSize){
					l = this.getInventoryStackLimit() - this.items[k].stackSize;
				}
				
				if(l == 0){
					return j;
				}
				else{
					j -= l;
					this.items[k].stackSize += l;
					this.items[k].animationsToGo = 5;
					return j;
				}
			}
		}
	}
	
	private int storeItemStack(ItemStack par1ItemStack){
		for(int i = 0; i < this.items.length; ++i){
			if(this.items[i] != null && this.items[i].itemID == par1ItemStack.itemID && this.items[i].isStackable() && this.items[i].stackSize < this.items[i].getMaxStackSize() && this.items[i].stackSize < this.getInventoryStackLimit() && (!this.items[i].getHasSubtypes() || this.items[i].getItemDamage() == par1ItemStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(this.items[i], par1ItemStack)){
				return i;
			}
		}
		
		return -1;
	}
}
