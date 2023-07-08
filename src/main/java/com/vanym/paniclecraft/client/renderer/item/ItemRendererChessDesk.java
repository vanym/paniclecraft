package com.vanym.paniclecraft.client.renderer.item;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.item.ItemChessDesk;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;

import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemRendererChessDesk extends ItemStackTileEntityRenderer {
    
    @Override
    public void renderByItem(ItemStack stack) {
        TileEntityChessDesk tileChessDesk = new TileEntityChessDesk();
        if (stack.hasTag()) {
            CompoundNBT tag = stack.getTag();
            if (tag.contains(ItemChessDesk.TAG_MOVES, 9)) {
                ListNBT list = tag.getList(ItemChessDesk.TAG_MOVES, 10);
                tileChessDesk.readMovesFromNBT(list);
            }
        }
        Core.instance.deskgame.tileChessDeskRenderer.render(tileChessDesk, 0.0F, 0.0F, 0.0F,
                                                            1.0F, -1);
    }
}
