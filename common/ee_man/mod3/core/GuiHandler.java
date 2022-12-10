package ee_man.mod3.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import ee_man.mod3.client.gui.GuiChess;
import ee_man.mod3.client.gui.GuiEditAdvSign;
import ee_man.mod3.client.gui.container.GuiCannon;
import ee_man.mod3.client.gui.container.GuiPalette;
import ee_man.mod3.client.gui.container.GuiPortableCrafting;
import ee_man.mod3.client.gui.container.GuiPrivateChest;
import ee_man.mod3.container.ContainerCannon;
import ee_man.mod3.container.ContainerPalette;
import ee_man.mod3.container.ContainerPortableWorkbench;
import ee_man.mod3.container.ContainerPrivateChest;
import ee_man.mod3.items.utils.IUpgradeForPrivateChest;
import ee_man.mod3.tileEntity.TileEntityAdvSign;
import ee_man.mod3.tileEntity.TileEntityCannon;
import ee_man.mod3.tileEntity.TileEntityChessDesk;
import ee_man.mod3.tileEntity.TileEntityPrivateChest;

public class GuiHandler implements IGuiHandler{
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z){
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		switch(ID){
			case 2:
				if(tile instanceof TileEntityCannon){
					return new ContainerCannon(player.inventory, (TileEntityCannon)tile);
				}
				else
					return null;
			case 3:
				if(tile instanceof TileEntityPrivateChest){
					TileEntityPrivateChest chest = (TileEntityPrivateChest)tile;
					ContainerPrivateChest container = new ContainerPrivateChest(player.inventory, chest);
					ItemStack item = chest.getSelectedUpgrade();
					if(item != null)
						return ((IUpgradeForPrivateChest)item.getItem()).getContainer(container, chest, item);
					else
						return container;
				}
				else
					return null;
			case 4:
				if(ContainerPortableWorkbench.canBeWorkbench(player.getHeldItem())){
					return new ContainerPortableWorkbench(player.inventory, world);
				}
				else
					return null;
			case 5:
				return new ContainerPalette(player.inventory);
			case 6:
				return null;
			case 7:
				return null;
			default:
				return null;
		}
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z){
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		switch(ID){
			case 0:
				if(tile instanceof TileEntityAdvSign){
					return new GuiEditAdvSign((TileEntityAdvSign)tile);
				}
				else
					return null;
			case 1:
				if(tile instanceof TileEntityChessDesk){
					return new GuiChess((TileEntityChessDesk)tile);
				}
				else
					return null;
			case 2:
				if(tile instanceof TileEntityCannon){
					return new GuiCannon(player.inventory, (TileEntityCannon)tile);
				}
				else
					return null;
			case 3:
				if(tile instanceof TileEntityPrivateChest){
					TileEntityPrivateChest chest = (TileEntityPrivateChest)tile;
					GuiPrivateChest gui = new GuiPrivateChest(new ContainerPrivateChest(player.inventory, chest));
					ItemStack item = chest.getSelectedUpgrade();
					if(item != null)
						return ((IUpgradeForPrivateChest)item.getItem()).getGui(gui, chest, item);
					else
						return gui;
				}
				else
					return null;
			case 4:
				return new GuiPortableCrafting(new ContainerPortableWorkbench(player.inventory, world));
			case 5:
				return new GuiPalette(new ContainerPalette(player.inventory));
			case 6:
				return null;
			case 7:
				return null;
			default:
				return null;
		}
	}
}
