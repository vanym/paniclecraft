package com.vanym.paniclecraft.client.renderer.item;

import org.lwjgl.opengl.GL11;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.item.ItemChessDesk;
import com.vanym.paniclecraft.tileentity.TileEntityChessDesk;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

@SideOnly(Side.CLIENT)
public class ItemRendererChessDesk implements IItemRenderer {
    
    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        if (!Core.instance.deskgame.renderChessDeskItem) {
            return false;
        }
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
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        TileEntityChessDesk tileChessDesk = new TileEntityChessDesk();
        tileChessDesk.blockMetadata = 0;
        switch (type) {
            case ENTITY:
                GL11.glTranslatef(-0.5F, -0.4F, -0.5F);
            break;
            case EQUIPPED:
                try {
                    EntityLivingBase entity = (EntityLivingBase)data[1];
                    if (stack != entity.getEquipmentInSlot(4)) {
                        GL11.glRotatef(125.0F, 0.0F, 1.0F, 0.0F);
                        GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
                        GL11.glTranslatef(-0.8F, 0.67F, -0.34F);
                        break;
                    }
                } catch (IndexOutOfBoundsException | NullPointerException | ClassCastException e) {
                }
                GL11.glTranslatef(0.0F, 0.775F, 0.0F);
            break;
            case EQUIPPED_FIRST_PERSON:
                GL11.glRotatef(20.0F, 0.0F, 0.9F, 1.0F);
                GL11.glTranslatef(0.15F, 0.7F, 0.05F);
            break;
            case INVENTORY: {
                tileChessDesk.blockMetadata = 2;
                GL11.glTranslatef(0.0F, 0.1F, 0.0F);
                float scale = 1.07F;
                GL11.glScalef(scale, scale, scale);
            }
            break;
            default:
            break;
        }
        ItemChessDesk.getMoves(stack).ifPresent(list->tileChessDesk.readMoves(list));
        Core.instance.deskgame.tileChessDeskRenderer.renderTileEntityAt(tileChessDesk, 0, 0, 0, 0);
    }
}
