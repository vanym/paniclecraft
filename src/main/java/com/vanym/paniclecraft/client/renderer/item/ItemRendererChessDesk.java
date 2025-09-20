package com.vanym.paniclecraft.client.renderer.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.vanym.paniclecraft.item.ItemChessDesk;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemRendererChessDesk extends ItemStackTileEntityRenderer {
    
    @Override
    public void renderByItem(
            ItemStack stack,
            ItemCameraTransforms.TransformType type,
            MatrixStack ms,
            IRenderTypeBuffer buffers,
            int light,
            int overlay) {
        TileEntityChessDesk tileChessDesk = new TileEntityChessDesk();
        ItemChessDesk.getMoves(stack).ifPresent(list->tileChessDesk.readMoves(list));
        TileEntityRendererDispatcher.instance.renderItem(tileChessDesk, ms, buffers,
                                                         light, overlay);
    }
}
