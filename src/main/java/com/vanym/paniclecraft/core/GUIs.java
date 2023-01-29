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

public enum GUIs implements IGuiHandler {
    PALETTE {
        @Override
        public Object getServerGuiElement(
                int ID,
                EntityPlayer player,
                World world,
                int x,
                int y,
                int z) {
            return new ContainerPalette(player.inventory);
        }
        
        @Override
        public Object getClientGuiElement(
                int ID,
                EntityPlayer player,
                World world,
                int x,
                int y,
                int z) {
            return new GuiPalette(new ContainerPalette(player.inventory));
        }
    },
    PORTABLEWORKBENCH {
        @Override
        public Object getServerGuiElement(
                int ID,
                EntityPlayer player,
                World world,
                int x,
                int y,
                int z) {
            return new ContainerPortableWorkbench(player, world);
        }
        
        @Override
        public Object getClientGuiElement(
                int ID,
                EntityPlayer player,
                World world,
                int x,
                int y,
                int z) {
            return new GuiPortableCrafting(player, world);
        }
    },
    ADVSIGN {
        @Override
        public Object getServerGuiElement(
                int ID,
                EntityPlayer player,
                World world,
                int x,
                int y,
                int z) {
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
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileEntityAdvSign) {
                return new GuiEditAdvSign((TileEntityAdvSign)tile);
            } else {
                return null;
            }
        }
    },
    CANNON {
        @Override
        public Object getServerGuiElement(
                int ID,
                EntityPlayer player,
                World world,
                int x,
                int y,
                int z) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileEntityCannon) {
                return new ContainerCannon(player.inventory, (TileEntityCannon)tile);
            } else {
                return null;
            }
        }
        
        @Override
        public Object getClientGuiElement(
                int ID,
                EntityPlayer player,
                World world,
                int x,
                int y,
                int z) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileEntityCannon) {
                return new GuiCannon(player.inventory, (TileEntityCannon)tile);
            } else {
                return null;
            }
        }
    },
    CHESS {
        @Override
        public Object getServerGuiElement(
                int ID,
                EntityPlayer player,
                World world,
                int x,
                int y,
                int z) {
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
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileEntityChessDesk) {
                return new GuiChess((TileEntityChessDesk)tile);
            } else {
                return null;
            }
        }
    };
}
