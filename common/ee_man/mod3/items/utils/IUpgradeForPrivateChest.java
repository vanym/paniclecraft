package ee_man.mod3.items.utils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import ee_man.mod3.client.gui.container.GuiPrivateChest;
import ee_man.mod3.container.ContainerPrivateChest;
import ee_man.mod3.tileEntity.TileEntityPrivateChest;

public interface IUpgradeForPrivateChest{
	
	void onChestUpdate(TileEntityPrivateChest chest, ItemStack itemstack);
	
	int getType(ItemStack itemstack);
	
	boolean canPlayerOpenChest(TileEntityPrivateChest chest, ItemStack itemstack, EntityPlayer player, int par6, float par7, float par8, float par9);
	
	boolean openInventoryForWorld(TileEntityPrivateChest chest, ItemStack itemstack);
	
	boolean canChestBeBroken(TileEntityPrivateChest chest, ItemStack itemstack);
	
	void onPlayerTryOpenChestAndHeCan(TileEntityPrivateChest chest, ItemStack itemstack, EntityPlayer player, int par6, float par7, float par8, float par9);
	
	void onPlayerTryOpenChestAndHeCannot(TileEntityPrivateChest chest, ItemStack itemstack, EntityPlayer player, int par6, float par7, float par8, float par9);
	
	void onChestBreak(TileEntityPrivateChest chest, ItemStack itemstack);
	
	void onPut(TileEntityPrivateChest chest, ItemStack itemstack, EntityPlayer player);
	
	void onPull(TileEntityPrivateChest chest, ItemStack itemstack, EntityPlayer player);
	
	boolean canBePuted(TileEntityPrivateChest chest, ItemStack itemstack, EntityPlayer player);
	
	boolean canBePulled(TileEntityPrivateChest chest, ItemStack itemstack, EntityPlayer player);
	
	ContainerPrivateChest getContainer(ContainerPrivateChest container, TileEntityPrivateChest chest, ItemStack itemstack);
	
	@SideOnly(Side.CLIENT)
	GuiPrivateChest getGui(GuiPrivateChest gui, TileEntityPrivateChest chest, ItemStack itemstack);
	
	@SideOnly(Side.CLIENT)
	boolean specialRender(TileEntityPrivateChest chest, ItemStack itemstack);
}
