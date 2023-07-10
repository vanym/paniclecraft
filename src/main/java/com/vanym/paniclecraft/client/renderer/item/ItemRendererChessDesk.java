package com.vanym.paniclecraft.client.renderer.item;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.item.ItemChessDesk;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;

import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemRendererChessDesk extends ItemStackTileEntityRenderer {
    
    @Override
    public void renderByItem(ItemStack stack) {
        TileEntityChessDesk tileChessDesk = new TileEntityChessDesk();
        ItemChessDesk.getMoves(stack).ifPresent(list->tileChessDesk.readMoves(list));
        Core.instance.deskgame.tileChessDeskRenderer.render(tileChessDesk, 0.0F, 0.0F, 0.0F,
                                                            1.0F, -1);
    }
}
