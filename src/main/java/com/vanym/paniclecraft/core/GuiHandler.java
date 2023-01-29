package com.vanym.paniclecraft.core;

import com.vanym.paniclecraft.client.gui.GuiChess;
import com.vanym.paniclecraft.client.gui.GuiEditAdvSign;
import com.vanym.paniclecraft.client.gui.container.GuiCannon;
import com.vanym.paniclecraft.client.gui.container.GuiPalette;
import com.vanym.paniclecraft.client.gui.container.GuiPortableCrafting;
import com.vanym.paniclecraft.container.ContainerCannon;
import com.vanym.paniclecraft.container.ContainerPalette;
import com.vanym.paniclecraft.container.ContainerPortableWorkbench;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;
import com.vanym.paniclecraft.tileentity.TileEntityCannon;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {
    
    @Override
    public Object getServerGuiElement(
            int ID,
            EntityPlayer player,
            World world,
            int x,
            int y,
            int z) {
        if (ID == GUIs.PALETTE.ordinal()) {
            return new ContainerPalette(player.inventory);
        }
        if (ID == GUIs.PORTABLEWORKBENCH.ordinal()) {
            return new ContainerPortableWorkbench(player, world);
        }
        if (ID == GUIs.CANNON.ordinal()) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileEntityCannon) {
                return new ContainerCannon(player.inventory, (TileEntityCannon)tile);
            } else {
                return null;
            }
        }
        return null;
    }
    
    @Override
    public Object getClientGuiElement(
            int ID,
            EntityPlayer player,
            World world,
            int x,
            int y,
            int z) {
        if (ID == GUIs.ADVSIGN.ordinal()) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileEntityAdvSign) {
                return new GuiEditAdvSign((TileEntityAdvSign)tile);
            } else {
                return null;
            }
        }
        if (ID == GUIs.PALETTE.ordinal()) {
            return new GuiPalette(new ContainerPalette(player.inventory));
        }
        if (ID == GUIs.PORTABLEWORKBENCH.ordinal()) {
            return new GuiPortableCrafting(player, world);
        }
        if (ID == GUIs.CANNON.ordinal()) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileEntityCannon) {
                return new GuiCannon(player.inventory, (TileEntityCannon)tile);
            } else {
                return null;
            }
        }
        if (ID == GUIs.CHESS.ordinal()) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileEntityChessDesk) {
                return new GuiChess((TileEntityChessDesk)tile);
            } else {
                return null;
            }
        }
        return null;
    }
    
}
