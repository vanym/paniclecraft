package com.vanym.paniclecraft.client.gui.container;

import com.vanym.paniclecraft.container.ContainerPortableWorkbench;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class GuiPortableCrafting extends GuiCrafting {
    
    public GuiPortableCrafting(EntityPlayer player, World world) {
        super(player.inventory, world, (int)player.posX, (int)player.posY, (int)player.posZ);
        this.inventorySlots = new ContainerPortableWorkbench(player, world);
    }
}
