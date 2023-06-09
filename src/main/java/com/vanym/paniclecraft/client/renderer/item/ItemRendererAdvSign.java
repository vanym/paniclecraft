package com.vanym.paniclecraft.client.renderer.item;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.item.ItemAdvSign;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;

import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemRendererAdvSign extends TileEntityItemStackRenderer {
    
    @Override
    public void renderByItem(ItemStack item, float partialTicks) {
        TileEntityAdvSign tileAS = new TileEntityAdvSign();
        tileAS.setStick(true);
        ItemAdvSign.getSign(item).ifPresent(signTag->tileAS.readFromNBT(signTag, true));
        Core.instance.advSign.tileAdvSignRenderer.render(tileAS, 0.0D, 0.0D, 0.0D,
                                                         partialTicks, -1, true, false, -1);
    }
}
