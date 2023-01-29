package com.vanym.paniclecraft.client.renderer.item;

import org.lwjgl.opengl.GL11;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.client.renderer.model.ModelPortableWorkbench;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

@SideOnly(Side.CLIENT)
public class ItemRendererPortableWorkbench implements IItemRenderer {
    
    protected static final ResourceLocation TEXTURE =
            new ResourceLocation(DEF.MOD_ID, "textures/models/portableWorkbench.png");
    
    ModelPortableWorkbench model = new ModelPortableWorkbench();
    
    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        if (!Core.instance.portableworkbench.renderWorkbenchItem) {
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
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        switch (type) {
            case ENTITY:
                GL11.glScalef(0.5F, 0.5F, 0.5F);
            break;
            case INVENTORY:
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            break;
            case EQUIPPED:
            case EQUIPPED_FIRST_PERSON:
                GL11.glTranslatef(0.0F, -0.5F, -0.9F);
                GL11.glScalef(0.75F, 0.75F, 0.75F);
                GL11.glRotatef(35.0F, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(0.2F, 0.0F, 0.8F);
            break;
            default:
            break;
        }
        this.model.render(0.0625F);
    }
    
}
