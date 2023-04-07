package com.vanym.paniclecraft.client.renderer.item;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.item.ItemAdvSign;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;

import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemRendererAdvSign extends TileEntityItemStackRenderer {
    
    @Override
    public void renderByItem(ItemStack item, float partialTicks) {
        TileEntityAdvSign tileAS = new TileEntityAdvSign();
        tileAS.setStick(true);
        if (item.hasTagCompound()) {
            NBTTagCompound tag = item.getTagCompound();
            if (tag.hasKey(ItemAdvSign.TAG_SIGN, 10)) {
                NBTTagCompound signTag = tag.getCompoundTag(ItemAdvSign.TAG_SIGN);
                tileAS.readFromNBT(signTag, true);
            }
        }
        Core.instance.advSign.tileAdvSignRenderer.render(tileAS, 0.0D, 0.0D, 0.0D,
                                                         partialTicks, false, false, -1);
    }
}
