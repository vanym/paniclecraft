package com.vanym.paniclecraft.client.gui.container;

import com.vanym.paniclecraft.container.ContainerPortableWorkbench;

import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPortableCrafting extends GuiCrafting {
    
    public GuiPortableCrafting(EntityPlayer player, World world) {
        super(player.inventory, world, player.getPosition());
        this.inventorySlots = new ContainerPortableWorkbench(player, world);
    }
}
