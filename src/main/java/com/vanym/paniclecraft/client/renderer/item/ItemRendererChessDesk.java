package com.vanym.paniclecraft.client.renderer.item;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.item.ItemChessDesk;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;

import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemRendererChessDesk extends TileEntityItemStackRenderer {
    
    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        TileEntityChessDesk tileChessDesk = new TileEntityChessDesk();
        if (stack.hasTagCompound()) {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag.hasKey(ItemChessDesk.TAG_MOVES, 9)) {
                NBTTagList list = tag.getTagList(ItemChessDesk.TAG_MOVES, 10);
                tileChessDesk.readMovesFromNBT(list);
            }
        }
        Core.instance.deskgame.tileChessDeskRenderer.render(tileChessDesk, 0.0F, 0.0F, 0.0F,
                                                            partialTicks, -1, 0.0F);
    }
}
