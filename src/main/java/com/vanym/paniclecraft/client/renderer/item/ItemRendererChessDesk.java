package com.vanym.paniclecraft.client.renderer.item;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.item.ItemChessDesk;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;

import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemRendererChessDesk extends TileEntityItemStackRenderer {
    
    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        TileEntityChessDesk tileChessDesk = new TileEntityChessDesk();
        ItemChessDesk.getMoves(stack).ifPresent(list->tileChessDesk.readMoves(list));
        Core.instance.deskgame.tileChessDeskRenderer.render(tileChessDesk, 0.0F, 0.0F, 0.0F,
                                                            partialTicks, -1, 0.0F);
    }
}
