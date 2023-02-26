package com.vanym.paniclecraft.client.renderer.item;

import org.lwjgl.opengl.GL11;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.item.ItemAdvSign;
import com.vanym.paniclecraft.tileentity.TileEntityAdvSign;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.IItemRenderer;

@SideOnly(Side.CLIENT)
public class ItemRendererAdvSign implements IItemRenderer {
    
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
        TileEntityAdvSign tileAS = new TileEntityAdvSign();
        tileAS.blockType = Core.instance.advSign.blockAdvSign;
        tileAS.blockMetadata = 1;
        tileAS.setStick(true);
        switch (type) {
            case ENTITY: {
                GL11.glTranslatef(-0.25F, -0.2F, -0.25F);
                float scale = 0.55F;
                GL11.glScalef(scale, scale, scale);
                tileAS.setDirection(270.0D);
            }
            break;
            case EQUIPPED: {
                float scale = 1.25F;
                GL11.glScalef(scale, scale, scale);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-60.0F, 1.0F, 0.0F, -0.2F);
                GL11.glTranslatef(-0.5F, -0.75F, 0.30F);
            }
            break;
            case EQUIPPED_FIRST_PERSON:
                tileAS.setDirection(135.0D);
            break;
            case INVENTORY: {
                float scale = 1.3F;
                GL11.glScalef(scale, scale, scale);
                GL11.glTranslatef(0.0F, -0.2F, 0.0F);
                tileAS.setDirection(315.0D);
            }
            break;
            case FIRST_PERSON_MAP:
            default:
            break;
        }
        if (item.hasTagCompound()) {
            NBTTagCompound tag = item.getTagCompound();
            if (tag.hasKey(ItemAdvSign.TAG_SIGN, 10)) {
                NBTTagCompound signTag = tag.getCompoundTag(ItemAdvSign.TAG_SIGN);
                tileAS.readFromNBT(signTag, true);
            }
        }
        Core.instance.advSign.tileAdvSignRenderer.renderTileEntityAt(tileAS, 0.0D, 0.0D, 0.0D,
                                                                     0.0F, false, false, -1);
    }
    
}
