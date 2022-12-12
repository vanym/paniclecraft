package com.vanym.paniclecraft.client.renderer.item;

import org.lwjgl.opengl.GL11;

import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.client.renderer.model.ModelPortableWorkbench;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.ForgeVersion;

@SideOnly(Side.CLIENT)
public class ItemRendererPortableWorkbench implements IItemRenderer {
    
    final ResourceLocation Texture =
            new ResourceLocation(DEF.MOD_ID, "textures/models/portableWorkbench.png");
    
    ModelPortableWorkbench model = new ModelPortableWorkbench();
    
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
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null) {
            TextureManager re = mc.renderEngine;
            if (re != null) {
                re.bindTexture(this.Texture);
            }
        }
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        float f;
        if (type.equals(ItemRenderType.ENTITY)) {
            f = 0.5F;
            GL11.glScalef(f, f, f);
        } else if (type.equals(ItemRenderType.EQUIPPED)
            || (ForgeVersion.getBuildVersion() >= 687 ? type.equals(ItemRenderType.EQUIPPED_FIRST_PERSON)
                                                      : false)) {
            GL11.glTranslatef(0.0F, -0.5F, -0.9F);
            f = 0.75F;
            GL11.glScalef(f, f, f);
            GL11.glRotatef(35.0F, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(0.2F, 0.0F, 0.8F);
        } else if (type.equals(ItemRenderType.INVENTORY)) {
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
        } else if (type.equals(ItemRenderType.FIRST_PERSON_MAP)) {
            
        }
        this.model.render(0.0625F);
    }
    
}
