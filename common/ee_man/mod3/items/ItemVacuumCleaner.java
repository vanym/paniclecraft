package ee_man.mod3.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ee_man.mod3.DefaultProperties;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class ItemVacuumCleaner extends ItemBroom{
	
	@SideOnly(Side.CLIENT)
	public Icon iconOn;
	@SideOnly(Side.CLIENT)
	public Icon iconOff;
	
	public static final String isOn = "isOn";
	
	public ItemVacuumCleaner(int par1, int par2, double par3){
		super(par1, par2, par3);
	}
	
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5){
		if(!par2World.isRemote && par3Entity instanceof EntityPlayer && this.isOn(par1ItemStack)){
			EntityPlayer par3 = (EntityPlayer)par3Entity;
			this.collectItems(par1ItemStack, par2World, par3);
			if(par1ItemStack.stackSize < 1){
				for(int i = 0; i < par3.inventory.getSizeInventory(); i++){
					ItemStack itemInSlot = par3.inventory.getStackInSlot(i);
					if(itemInSlot != null && itemInSlot == par1ItemStack)
						par3.inventory.setInventorySlotContents(i, null);
				}
			}
		}
	}
	
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3){
		NBTTagCompound nt;
		if(par1ItemStack.hasTagCompound()){
			nt = par1ItemStack.getTagCompound();
		}
		else{
			nt = new NBTTagCompound();
			par1ItemStack.setTagCompound(nt);
		}
		nt.setBoolean(isOn, !this.isOn(par1ItemStack));
		return par1ItemStack;
	}
	
	public boolean isOn(ItemStack par1){
		if(par1.hasTagCompound()){
			NBTTagCompound nt = par1.getTagCompound();
			return(nt.hasKey(isOn) ? nt.getBoolean(isOn) : false);
		}
		else
			return false;
	}
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister){
		itemIcon = iconRegister.registerIcon(DefaultProperties.TEXTURE_ID + ":" + this.getUnlocalizedName());
		iconOn = iconRegister.registerIcon(DefaultProperties.TEXTURE_ID + ":" + this.getUnlocalizedName() + "_on");
		iconOff = iconRegister.registerIcon(DefaultProperties.TEXTURE_ID + ":" + this.getUnlocalizedName() + "_off");
	}
	
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses(){
		return true;
	}
	
	public int getRenderPasses(int metadata){
		return 1;
	}
	
	@SideOnly(Side.CLIENT)
	public Icon getIcon(ItemStack stack, int pass){
		return this.isOn(stack) ? this.iconOn : this.iconOff;
	}
}
