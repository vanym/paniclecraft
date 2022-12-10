package ee_man.mod3.items;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import ee_man.mod3.DefaultProperties;
import ee_man.mod3.client.gui.container.GuiPrivateChest;
import ee_man.mod3.client.gui.container.GuiPrivateChestUpgradeCraft;
import ee_man.mod3.client.gui.container.GuiPrivateChestUpgradePrivateTool;
import ee_man.mod3.container.ContainerPrivateChest;
import ee_man.mod3.container.ContainerPrivateChestUpgradeCraft;
import ee_man.mod3.items.utils.IUpgradeForPrivateChest;
import ee_man.mod3.tileEntity.TileEntityPrivateChest;

public class ItemPrivateChestUpgrade extends ItemMod3 implements IUpgradeForPrivateChest{
	
	@SideOnly(Side.CLIENT)
	public Icon[] icons;
	
	public ItemPrivateChestUpgrade(int par1){
		super(par1);
		this.setMaxStackSize(1);
		this.setHasSubtypes(true);
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	@SideOnly(Side.CLIENT)
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List){
		for(int var4 = 0; var4 < 4; ++var4){
			par3List.add(new ItemStack(par1, 1, var4));
		}
	}
	
	@Override
	public void onChestUpdate(TileEntityPrivateChest chest, ItemStack itemstack){
		switch(itemstack.getItemDamage()){
			case 2:
				@SuppressWarnings("unchecked")
				List<EntityItem> list = chest.worldObj.getEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getBoundingBox(chest.xCoord - 1, chest.yCoord - 1, chest.zCoord - 1, chest.xCoord + 2, chest.yCoord + 2, chest.zCoord + 2));
				for(int i = 0; i < list.size(); i++){
					EntityItem itemEntity = list.get(i);
					if(itemEntity.getDistance(chest.xCoord + 0.5, chest.yCoord + 0.5, chest.zCoord + 0.5) <= 1.5){
						ItemStack itemStack = itemEntity.getEntityItem();
						int itemStackSize = itemStack.stackSize;
						if(itemEntity.delayBeforeCanPickup <= 0 && (itemStackSize <= 0 || chest.inventoryItems.addItemStackToInventory(itemStack))){
							itemEntity.playSound("random.pop", 0.2F, (1.0F * 0.7F + 1.0F) * 2.0F);
							if(itemStack.stackSize <= 0){
								itemEntity.setDead();
							}
						}
					}
				}
			default:
				return;
		}
	}
	
	@Override
	public int getType(ItemStack itemstack){
		switch(itemstack.getItemDamage()){
			case 0:
				return 0;
			case 1:
				return 1;
			case 2:
				return 2;
			case 3:
				return 3;
			default:
				return -1;
		}
	}
	
	@Override
	public boolean canPlayerOpenChest(TileEntityPrivateChest chest, ItemStack itemstack, EntityPlayer player, int par6, float par7, float par8, float par9){
		switch(itemstack.getItemDamage()){
			case 0:
				ArrayList<String> list = getPlayerNamesFromNBT(chest.upgradesDataNotSendable);
				return list.contains(player.username) || list.isEmpty();
			default:
				return true;
		}
	}
	
	@Override
	public void onPlayerTryOpenChestAndHeCan(TileEntityPrivateChest chest, ItemStack itemstack, EntityPlayer player, int par6, float par7, float par8, float par9){
		switch(itemstack.getItemDamage()){
			default:
				return;
		}
	}
	
	@Override
	public void onPlayerTryOpenChestAndHeCannot(TileEntityPrivateChest chest, ItemStack itemstack, EntityPlayer player, int par6, float par7, float par8, float par9){
		switch(itemstack.getItemDamage()){
			default:
				return;
		}
	}
	
	public static ArrayList<String> getPlayerNamesFromNBT(NBTTagCompound tag){
		ArrayList<String> list = new ArrayList<String>();
		if(tag.hasKey("privateUpgrade0names")){
			NBTTagList tagList = tag.getTagList("privateUpgrade0names");
			for(int i = 0; i < tagList.tagCount(); i++){
				NBTTagCompound tagAt = (NBTTagCompound)tagList.tagAt(i);
				if(tagAt.hasKey("name"))
					list.add(tagAt.getString("name"));
			}
		}
		return list;
	}
	
	public static void removePlayerNameFromNBT(NBTTagCompound tag, String str){
		if(tag.hasKey("privateUpgrade0names")){
			NBTTagList tagList = tag.getTagList("privateUpgrade0names");
			for(int i = 0; i < tagList.tagCount(); i++){
				NBTTagCompound tagAt = (NBTTagCompound)tagList.tagAt(i);
				if(tagAt.hasKey("name"))
					if(str.equals(tagAt.getString("name")))
						tagList.removeTag(i);
			}
		}
	}
	
	public static void addPlayerNameToNBT(NBTTagCompound tag, String str){
		if(!tag.hasKey("privateUpgrade0names")){
			tag.setTag("privateUpgrade0names", new NBTTagList());
		}
		NBTTagList tagList = tag.getTagList("privateUpgrade0names");
		NBTTagCompound tagAt = new NBTTagCompound();
		tagAt.setString("name", str);
		tagList.appendTag(tagAt);
	}
	
	@Override
	public boolean canChestBeBroken(TileEntityPrivateChest chest, ItemStack itemstack){
		switch(itemstack.getItemDamage()){
			case 1:
				return false;
			default:
				return true;
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister){
		this.icons = new Icon[4];
		for(int i = 0; i < icons.length; i++)
			icons[i] = iconRegister.registerIcon(DefaultProperties.TEXTURE_ID + ":" + this.getUnlocalizedName() + i);
	}
	
	@SideOnly(Side.CLIENT)
	public Icon getIconFromDamage(int par1){
		if(icons == null || par1 >= icons.length)
			return this.itemIcon;
		return icons[par1];
	}
	
	@Override
	public ContainerPrivateChest getContainer(ContainerPrivateChest container, TileEntityPrivateChest chest, ItemStack itemstack){
		switch(itemstack.getItemDamage()){
			case 3:
				return new ContainerPrivateChestUpgradeCraft(container.playerInventory, container.tile);
			default:
				return container;
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiPrivateChest getGui(GuiPrivateChest gui, TileEntityPrivateChest chest, ItemStack itemstack){
		switch(itemstack.getItemDamage()){
			case 0:
				return new GuiPrivateChestUpgradePrivateTool(gui.container);
			case 3:
				return new GuiPrivateChestUpgradeCraft(new ContainerPrivateChestUpgradeCraft(gui.container.playerInventory, gui.container.tile));
			default:
				return gui;
		}
	}
	
	@Override
	public boolean openInventoryForWorld(TileEntityPrivateChest chest, ItemStack itemstack){
		switch(itemstack.getItemDamage()){
			case 0:
				return false;
			default:
				return true;
		}
	}
	
	@Override
	public void onChestBreak(TileEntityPrivateChest chest, ItemStack itemstack){
		switch(itemstack.getItemDamage()){
			default:
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean specialRender(TileEntityPrivateChest chest, ItemStack itemstack){
		switch(itemstack.getItemDamage()){
			default:
				return false;
		}
	}
	
	@Override
	public void onPut(TileEntityPrivateChest chest, ItemStack itemstack, EntityPlayer player){
		switch(itemstack.getItemDamage()){
			case 0:
				if(!ItemPrivateChestUpgrade.getPlayerNamesFromNBT(chest.upgradesDataNotSendable).contains(player.username))
					addPlayerNameToNBT(chest.upgradesDataNotSendable, player.username);
			break;
			default:
			break;
		}
	}
	
	@Override
	public void onPull(TileEntityPrivateChest chest, ItemStack itemstack, EntityPlayer player){
		switch(itemstack.getItemDamage()){
			default:
			break;
		}
	}
	
	@Override
	public boolean canBePuted(TileEntityPrivateChest chest, ItemStack itemstack, EntityPlayer player){
		switch(itemstack.getItemDamage()){
			default:
				return true;
		}
	}
	
	@Override
	public boolean canBePulled(TileEntityPrivateChest chest, ItemStack itemstack, EntityPlayer player){
		switch(itemstack.getItemDamage()){
			default:
				return true;
		}
	}
}
