package com.vanym.paniclecraft.core;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public final class GuiHandler implements IGuiHandler {
    
    @Override
    public Object getServerGuiElement(
            int ID,
            EntityPlayer player,
            World world,
            int x,
            int y,
            int z) {
        return GUIs.values()[ID].getServerGuiElement(ID, player, world, x, y, z);
    }
    
    @Override
    public Object getClientGuiElement(
            int ID,
            EntityPlayer player,
            World world,
            int x,
            int y,
            int z) {
        return GUIs.values()[ID].getClientGuiElement(ID, player, world, x, y, z);
    }
}
