package com.vanym.paniclecraft.plugins.computercraft;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;

import dan200.computercraft.api.client.TransformedModel;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.TransformationMatrix;
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
        return TurtleUpgradeType.PERIPHERAL;
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
    public TransformedModel getModel(ITurtleAccess turtle, TurtleSide side) {
        float xOffset = side == TurtleSide.LEFT ? -0.40625f : 0.40625f;
        // @formatter:off
        Matrix4f transform = new Matrix4f( new float[] {
            0.0f, 0.0f, -1.0f, 1.0f + xOffset,
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, -1.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 0.0f, 1.0f,
        } );
        // @formatter:on
        return TransformedModel.of(this.getCraftingItem(), new TransformationMatrix(transform));
    }
}
