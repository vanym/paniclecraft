package ee_man.mod3.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import ee_man.mod3.client.gui.GuiChess;
import ee_man.mod3.client.gui.GuiEditAdvSign;
import ee_man.mod3.client.gui.container.GuiCannon;
import ee_man.mod3.client.gui.container.GuiPalette;
import ee_man.mod3.client.gui.container.GuiPortableCrafting;
import ee_man.mod3.container.ContainerCannon;
import ee_man.mod3.container.ContainerPalette;
import ee_man.mod3.container.ContainerPortableWorkbench;
import ee_man.mod3.tileentity.TileEntityAdvSign;
import ee_man.mod3.tileentity.TileEntityCannon;
import ee_man.mod3.tileentity.TileEntityChessDesk;

public class GuiHandler implements IGuiHandler{
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z){
		if(ID == GUIs.PALETTE.ordinal()){
			return new ContainerPalette(player.inventory);
		}
		if(ID == GUIs.PORTABLEWORKBENCH.ordinal()){
			return new ContainerPortableWorkbench(player.inventory, world, x, y, z);
		}
		if(ID == GUIs.CANNON.ordinal()){
			TileEntity tile = world.getTileEntity(x, y, z);
			if(tile instanceof TileEntityCannon){
				return new ContainerCannon(player.inventory, (TileEntityCannon)tile);
			}
			else
				return null;
		}
		return null;
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z){
		if(ID == GUIs.ADVSIGN.ordinal()){
			TileEntity tile = world.getTileEntity(x, y, z);
			if(tile instanceof TileEntityAdvSign){
				return new GuiEditAdvSign((TileEntityAdvSign)tile);
			}
			else
				return null;
		}
		if(ID == GUIs.PALETTE.ordinal()){
			return new GuiPalette(new ContainerPalette(player.inventory));
		}
		if(ID == GUIs.PORTABLEWORKBENCH.ordinal()){
			return new GuiPortableCrafting(new ContainerPortableWorkbench(player.inventory, world, x, y, z));
		}
		if(ID == GUIs.CANNON.ordinal()){
			TileEntity tile = world.getTileEntity(x, y, z);
			if(tile instanceof TileEntityCannon){
				return new GuiCannon(player.inventory, (TileEntityCannon)tile);
			}
			else
				return null;
		}
		if(ID == GUIs.CHESS.ordinal()){
			TileEntity tile = world.getTileEntity(x, y, z);
			if(tile instanceof TileEntityChessDesk){
				return new GuiChess((TileEntityChessDesk)tile);
			}
			else
				return null;
		}
		return null;
	}
	
}
