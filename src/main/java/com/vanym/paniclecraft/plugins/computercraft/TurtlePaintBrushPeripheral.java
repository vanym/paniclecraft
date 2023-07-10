package com.vanym.paniclecraft.plugins.computercraft;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;

import com.vanym.paniclecraft.core.component.painting.IColorizeable;
import com.vanym.paniclecraft.core.component.painting.IPaintingTool;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.core.component.painting.WorldPicturePoint;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;
import com.vanym.paniclecraft.inventory.InventoryUtils;
import com.vanym.paniclecraft.item.ItemPalette;
import com.vanym.paniclecraft.utils.ColorUtils;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class TurtlePaintBrushPeripheral extends PeripheralBase {
    
    protected final ITurtleAccess turtle;
    
    public TurtlePaintBrushPeripheral(ITurtleAccess turtle) {
        this.turtle = turtle;
    }
    
    @Override
    public String getType() {
        return "paintbrush";
    }
    
    protected boolean detectPicture(EnumFacing dir) throws LuaException, InterruptedException {
        return this.searchPicture(dir) != null;
    }
    
    @PeripheralMethod(0)
    protected boolean detectPicture() throws LuaException, InterruptedException {
        return this.detectPicture(null);
    }
    
    @PeripheralMethod(1)
    protected boolean detectPictureUp() throws LuaException, InterruptedException {
        return this.detectPicture(EnumFacing.UP);
    }
    
    @PeripheralMethod(2)
    protected boolean detectPictureDown() throws LuaException, InterruptedException {
        return this.detectPicture(EnumFacing.DOWN);
    }
    
    protected int getWidth(EnumFacing dir) throws LuaException, InterruptedException {
        return this.findPicture(dir).getWidth();
    }
    
    @PeripheralMethod(3)
    protected int getWidth() throws LuaException, InterruptedException {
        return this.getWidth(null);
    }
    
    @PeripheralMethod(4)
    protected int getWidthUp() throws LuaException, InterruptedException {
        return this.getWidth(EnumFacing.UP);
    }
    
    @PeripheralMethod(5)
    protected int getWidthDown() throws LuaException, InterruptedException {
        return this.getWidth(EnumFacing.DOWN);
    }
    
    protected int getHeight(EnumFacing dir) throws LuaException, InterruptedException {
        return this.findPicture(dir).getHeight();
    }
    
    @PeripheralMethod(6)
    protected int getHeight() throws LuaException, InterruptedException {
        return this.getHeight(null);
    }
    
    @PeripheralMethod(7)
    protected int getHeightUp() throws LuaException, InterruptedException {
        return this.getHeight(EnumFacing.UP);
    }
    
    @PeripheralMethod(8)
    protected int getHeightDown() throws LuaException, InterruptedException {
        return this.getHeight(EnumFacing.DOWN);
    }
    
    protected boolean isEditable(EnumFacing dir) throws LuaException, InterruptedException {
        return this.findPicture(dir).isEditable();
    }
    
    @PeripheralMethod(9)
    protected boolean isEditable() throws LuaException, InterruptedException {
        return this.isEditable(null);
    }
    
    @PeripheralMethod(10)
    protected boolean isEditableUp() throws LuaException, InterruptedException {
        return this.isEditable(EnumFacing.UP);
    }
    
    @PeripheralMethod(11)
    protected boolean isEditableDown() throws LuaException, InterruptedException {
        return this.isEditable(EnumFacing.DOWN);
    }
    
    protected boolean useBrush(EnumFacing dir, int px, int py)
            throws LuaException, InterruptedException {
        ItemStack stack = this.getSelectedStack();
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof IPaintingTool)) {
            throw new LuaException("cannot find brush");
        }
        Picture picture = this.findPicture(dir);
        PicturePeripheral.checkPicturePixelCoords(picture, px, py);
        return picture.usePaintingTool(stack, px, py);
    }
    
    @PeripheralMethod(20)
    protected boolean useBrush(int px, int py) throws LuaException, InterruptedException {
        return this.useBrush(null, px, py);
    }
    
    @PeripheralMethod(21)
    protected boolean useBrushUp(int px, int py) throws LuaException, InterruptedException {
        return this.useBrush(EnumFacing.UP, px, py);
    }
    
    @PeripheralMethod(22)
    protected boolean useBrushDown(int px, int py) throws LuaException, InterruptedException {
        return this.useBrush(EnumFacing.DOWN, px, py);
    }
    
    @PeripheralMethod(31)
    protected Object[] getBrushColor() throws LuaException, InterruptedException {
        ItemStack stack = this.getSelectedStack();
        IColorizeable colorizeable = IColorizeable.getColorizeable(stack);
        if (colorizeable == null) {
            throw new LuaException("cannot find brush");
        }
        Color color = new Color(colorizeable.getColor(stack));
        return new Object[]{color.getRed(), color.getGreen(), color.getBlue()};
    }
    
    @PeripheralMethod(32)
    protected boolean setBrushColor(int red, int green, int blue)
            throws LuaException, InterruptedException {
        if (!InventoryUtils.stream(this.turtle.getInventory())
                           .anyMatch(ItemPalette::canBePalette)) {
            throw new LuaException("cannot find palette");
        }
        ItemStack stack = this.getSelectedStack();
        IColorizeable colorizeable = IColorizeable.getColorizeable(stack);
        if (colorizeable == null) {
            throw new LuaException("cannot find brush");
        }
        Color color = PicturePeripheral.getColor(red, green, blue);
        colorizeable.setColor(stack, ColorUtils.getAlphaless(color));
        return true;
    }
    
    protected Picture searchPicture(EnumFacing dir) {
        if (dir == null) {
            dir = this.turtle.getDirection();
        }
        BlockPos pos = this.turtle.getPosition().offset(dir);
        EnumFacing pside = dir.getOpposite();
        for (WorldPictureProvider provider : this.getProviders()) {
            WorldPicturePoint point = new WorldPicturePoint(
                    provider,
                    this.turtle.getWorld(),
                    pos,
                    pside.getIndex());
            Picture picture = point.getOrCreatePicture();
            if (picture != null) {
                return picture;
            }
        }
        return null;
    }
    
    protected Picture findPicture(EnumFacing dir) throws LuaException {
        Picture picture = this.searchPicture(dir);
        if (picture == null) {
            throw new LuaException("cannot find picture");
        }
        return picture;
    }
    
    @Override
    public void attach(IComputerAccess computer) {}
    
    @Override
    public void detach(IComputerAccess computer) {}
    
    @Override
    public boolean equals(IPeripheral other) {
        return super.equals((Object)other);
    }
    
    protected ItemStack getSelectedStack() {
        return this.turtle.getInventory().getStackInSlot(this.turtle.getSelectedSlot());
    }
    
    protected Collection<WorldPictureProvider> getProviders() {
        return Arrays.asList(WorldPictureProvider.ANYTILE);
    }
}
