package com.vanym.paniclecraft.client.renderer.item;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.item.ItemAdvSign;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;

import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemRendererAdvSign extends ItemStackTileEntityRenderer {
    
    @Override
    public void renderByItem(ItemStack item) {
        TileEntityAdvSign tileAS = new TileEntityAdvSign();
        tileAS.setStick(true);
        if (item.hasTag()) {
            CompoundNBT tag = item.getTag();
            if (tag.contains(ItemAdvSign.TAG_SIGN, 10)) {
                CompoundNBT signTag = tag.getCompound(ItemAdvSign.TAG_SIGN);
                tileAS.read(signTag, true);
            }
        }
        Core.instance.advSign.tileAdvSignRenderer.render(tileAS, 0.0D, 0.0D, 0.0D,
                                                         1.0F, -1, true, false, -1);
    }
}
