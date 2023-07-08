package com.vanym.paniclecraft.client.renderer.item;

import com.mojang.blaze3d.platform.GlStateManager;
import com.vanym.paniclecraft.DEF;
import com.vanym.paniclecraft.client.renderer.model.ModelPortableWorkbench;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemRendererPortableWorkbench extends ItemStackTileEntityRenderer {
    
    protected static final ResourceLocation TEXTURE =
            new ResourceLocation(DEF.MOD_ID, "textures/models/portable_workbench.png");
    
    ModelPortableWorkbench model = new ModelPortableWorkbench();
    
    @Override
    public void renderByItem(ItemStack stack) {
        Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE);
        GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.translatef(0.5F, -0.0625F, -0.5F);
        this.model.render(0.0625F);
    }
}
