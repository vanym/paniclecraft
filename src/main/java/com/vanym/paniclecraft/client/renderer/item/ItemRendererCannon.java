package com.vanym.paniclecraft.client.renderer.item;

import org.lwjgl.opengl.GL11;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.tileentity.TileEntityCannon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.IItemRenderer;

@SideOnly(Side.CLIENT)
public class ItemRendererCannon implements IItemRenderer {
    
    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        if (!Core.instance.cannon.renderCannonItem) {
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
        TileEntityCannon tileCannon = new TileEntityCannon();
        switch (type) {
            case ENTITY: {
                GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                tileCannon.setHeight(stack.stackSize);
            }
            break;
            case EQUIPPED:
            case EQUIPPED_FIRST_PERSON: {
                GL11.glTranslatef(0.0F, 0.5F, 0.0F);
                tileCannon.setDirection(135.0D);
                try {
                    EntityLivingBase entity = (EntityLivingBase)data[1];
                    if (stack == entity.getEquipmentInSlot(4)) {
                        tileCannon.setDirection(270.0D);
                    }
                    double pitch = MathHelper.wrapAngleTo180_double(entity.rotationPitch);
                    double height = Math.max(0.0D, Math.min(90.0D, pitch));
                    tileCannon.setHeight(height);
                } catch (IndexOutOfBoundsException | NullPointerException | ClassCastException e) {
                }
            }
            break;
            case INVENTORY: {
                float scale = 1.1F;
                GL11.glScalef(scale, scale, scale);
                tileCannon.setHeight(stack.stackSize);
            }
            break;
            default:
            break;
        }
        Core.instance.cannon.tileCannonRenderer.renderTileEntityAt(tileCannon, 0, 0, 0, 0);
    }
    
}
