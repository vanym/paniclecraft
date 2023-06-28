package com.vanym.paniclecraft.client.renderer.item;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.tileentity.TileEntityCannon;

import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class ItemRendererCannon extends ItemStackTileEntityRenderer {
    
    protected double height = 0.0D;
    protected boolean oneshot = false;
    
    @Override
    public void renderByItem(ItemStack stack) {
        TileEntityCannon tileCannon = new TileEntityCannon();
        tileCannon.setHeight(this.height);
        Core.instance.cannon.tileCannonRenderer.render(tileCannon, 0.0F, 0.0F, 0.0F, 1.0F, -1);
        if (this.oneshot) {
            this.height = 0.0D;
            this.oneshot = false;
        }
    }
    
    protected void setHeight(double pitch) {
        this.height = Math.max(0.0D, Math.min(90.0D, MathHelper.wrapDegrees(pitch)));
    }
    
    @SubscribeEvent
    public void preEntityRender(RenderLivingEvent.Pre<LivingEntity, ?> event) {
        this.setHeight(event.getEntity().rotationPitch);
    }
    
    @SubscribeEvent
    public void postEntityRender(RenderLivingEvent.Post<LivingEntity, ?> event) {
        this.height = 0.0D;
    }
    
    @SubscribeEvent
    public void handRender(RenderSpecificHandEvent event) {
        if (event.getItemStack().getItem() == Core.instance.cannon.itemCannon) {
            this.setHeight(event.getInterpolatedPitch());
            this.oneshot = true;
        }
    }
    
    public static ItemRendererCannon createRegistered() {
        ItemRendererCannon renderer = new ItemRendererCannon();
        MinecraftForge.EVENT_BUS.register(renderer);
        return renderer;
    }
}
