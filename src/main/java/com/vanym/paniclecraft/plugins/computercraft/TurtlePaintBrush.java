package com.vanym.paniclecraft.plugins.computercraft;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TurtlePaintBrush implements ITurtleUpgrade {
    
    protected static final ResourceLocation ID = new ResourceLocation(DEF.MOD_ID, "paintbrush");
    
    public TurtlePaintBrush() {}
    
    @Override
    public ResourceLocation getUpgradeID() {
        return ID;
    }
    
    @Override
    public String getUnlocalisedAdjective() {
        return "Painter";
    }
    
    @Override
    public TurtleUpgradeType getType() {
        return TurtleUpgradeType.Peripheral;
    }
    
    @Override
    public ItemStack getCraftingItem() {
        return new ItemStack(Core.instance.painting.itemPaintBrush);
    }
    
    @Override
    public IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
        return new TurtlePaintBrushPeripheral(turtle);
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public Pair<IBakedModel, Matrix4f> getModel(ITurtleAccess turtle, TurtleSide side) {
        float xOffset = side == TurtleSide.Left ? -0.40625f : 0.40625f;
        // @formatter:off
        Matrix4f transform = new Matrix4f(
            0.0f, 0.0f, -1.0f, 1.0f + xOffset,
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, -1.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 0.0f, 1.0f
        );
        // @formatter:on
        Minecraft mc = Minecraft.getInstance();
        return Pair.of(mc.getItemRenderer()
                         .getItemModelMesher()
                         .getItemModel(this.getCraftingItem()),
                       transform);
    }
}
