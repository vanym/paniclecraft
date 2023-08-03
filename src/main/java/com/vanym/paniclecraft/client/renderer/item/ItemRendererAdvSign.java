package com.vanym.paniclecraft.client.renderer.item;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.core.component.advsign.AdvSignForm;
import com.vanym.paniclecraft.item.ItemAdvSign;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;

import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemRendererAdvSign extends ItemStackTileEntityRenderer {
    
    @Override
    public void renderByItem(ItemStack item) {
        TileEntityAdvSign tileAS = new TileEntityAdvSign();
        tileAS.setForm(AdvSignForm.STICK_DOWN);
        ItemAdvSign.getSign(item).ifPresent(signTag->tileAS.read(signTag, true));
        Core.instance.advSign.tileAdvSignRenderer.render(tileAS, 0.0D, 0.0D, 0.0D,
                                                         1.0F, -1, true, false, null);
    }
}
