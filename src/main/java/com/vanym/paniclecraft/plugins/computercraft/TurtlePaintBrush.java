package com.vanym.paniclecraft.plugins.computercraft;

import com.vanym.paniclecraft.Core;
import com.vanym.paniclecraft.DEF;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import dan200.computercraft.api.turtle.TurtleVerb;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class TurtlePaintBrush implements ITurtleUpgrade {
    
    protected IIcon iconLeft;
    protected IIcon iconRight;
    
    public TurtlePaintBrush() {}
    
    @Override
    public int getUpgradeID() {
        // See https://computercraft.info/wiki/Turtle_Upgrade_IDs
        return 245;
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
        return Core.instance.painting.itemPaintBrush.getBrush();
    }
    
    @Override
    public IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
        return new TurtlePaintBrushPeripheral(turtle);
    }
    
    @Override
    public TurtleCommandResult useTool(
            ITurtleAccess turtle,
            TurtleSide side,
            TurtleVerb verb,
            int direction) {
        return null;
    }
    
    @Override
    public IIcon getIcon(ITurtleAccess turtle, TurtleSide side) {
        switch (side) {
            default:
            case Left:
                return this.iconLeft;
            case Right:
                return this.iconRight;
        }
    }
    
    @Override
    public void update(ITurtleAccess turtle, TurtleSide side) {}
    
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void textureStitchEvent(net.minecraftforge.client.event.TextureStitchEvent.Pre event) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getMinecraft();
        if (event.map.getTextureType() == mc.getTextureMapBlocks().getTextureType()) {
            this.iconLeft = event.map.registerIcon(DEF.MOD_ID + ":turtle.paintBrush.left");
            this.iconRight = event.map.registerIcon(DEF.MOD_ID + ":turtle.paintBrush.right");
        }
    }
}
