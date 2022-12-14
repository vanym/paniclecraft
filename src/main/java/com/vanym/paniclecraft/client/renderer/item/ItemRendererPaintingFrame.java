package com.vanym.paniclecraft.client.renderer.item;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.tileentity.TileEntityPaintingFrame;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

@SideOnly(Side.CLIENT)
public class ItemRendererPaintingFrame implements IItemRenderer {
    
    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }
    
    @Override
    public boolean shouldUseRenderHelper(
            ItemRenderType type,
            ItemStack item,
            ItemRendererHelper helper) {
        return true;
    }
    
    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        TileEntityPaintingFrame tile = new TileEntityPaintingFrame();
        tile.blockType = Core.instance.painting.blockPaintingFrame;
        switch (type) {
            case ENTITY:
                GL11.glTranslatef(-0.5F, -0.2F, -0.5F);
            break;
            case EQUIPPED:
            break;
            case EQUIPPED_FIRST_PERSON:
            break;
            case FIRST_PERSON_MAP:
            break;
            case INVENTORY:
                GL11.glTranslatef(0.0F, -0.1F, 0.0F);
            break;
            default:
            break;
        }
        Core.instance.painting.tilePaintingFrameRenderer.renderTileEntityAtItem(tile);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
    }
    
}
