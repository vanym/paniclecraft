package com.vanym.paniclecraft.client.renderer.item;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.tileentity.TileEntityCannon;

import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemRendererCannon extends TileEntityItemStackRenderer {
    
    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        TileEntityCannon tileCannon = new TileEntityCannon();
        Core.instance.cannon.tileCannonRenderer.render(tileCannon, 0.0F, 0.0F, 0.0F,
                                                       partialTicks, -1, 0.0F);
    }
}
