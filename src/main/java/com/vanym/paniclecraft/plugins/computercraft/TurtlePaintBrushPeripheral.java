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
import net.minecraft.util.Direction;
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
    
    protected boolean detectPicture(Direction dir) throws LuaException, InterruptedException {
        return this.searchPicture(dir) != null;
    }
    
    @PeripheralMethod(value = 0, mainThread = true)
    protected boolean detectPicture() throws LuaException, InterruptedException {
        return this.detectPicture(null);
    }
    
    @PeripheralMethod(value = 1, mainThread = true)
    protected boolean detectPictureUp() throws LuaException, InterruptedException {
        return this.detectPicture(Direction.UP);
    }
    
    @PeripheralMethod(value = 2, mainThread = true)
    protected boolean detectPictureDown() throws LuaException, InterruptedException {
        return this.detectPicture(Direction.DOWN);
    }
    
    protected int getWidth(Direction dir) throws LuaException, InterruptedException {
        return this.findPicture(dir).getWidth();
    }
    
    @PeripheralMethod(value = 3, mainThread = true)
    protected int getWidth() throws LuaException, InterruptedException {
        return this.getWidth(null);
    }
    
    @PeripheralMethod(value = 4, mainThread = true)
    protected int getWidthUp() throws LuaException, InterruptedException {
        return this.getWidth(Direction.UP);
    }
    
    @PeripheralMethod(value = 5, mainThread = true)
    protected int getWidthDown() throws LuaException, InterruptedException {
        return this.getWidth(Direction.DOWN);
    }
    
    protected int getHeight(Direction dir) throws LuaException, InterruptedException {
        return this.findPicture(dir).getHeight();
    }
    
    @PeripheralMethod(value = 6, mainThread = true)
    protected int getHeight() throws LuaException, InterruptedException {
        return this.getHeight(null);
    }
    
    @PeripheralMethod(value = 7, mainThread = true)
    protected int getHeightUp() throws LuaException, InterruptedException {
        return this.getHeight(Direction.UP);
    }
    
    @PeripheralMethod(value = 8, mainThread = true)
    protected int getHeightDown() throws LuaException, InterruptedException {
        return this.getHeight(Direction.DOWN);
    }
    
    protected boolean isEditable(Direction dir) throws LuaException, InterruptedException {
        return this.findPicture(dir).isEditable();
    }
    
    @PeripheralMethod(value = 9, mainThread = true)
    protected boolean isEditable() throws LuaException, InterruptedException {
        return this.isEditable(null);
    }
    
    @PeripheralMethod(value = 10, mainThread = true)
    protected boolean isEditableUp() throws LuaException, InterruptedException {
        return this.isEditable(Direction.UP);
    }
    
    @PeripheralMethod(value = 11, mainThread = true)
    protected boolean isEditableDown() throws LuaException, InterruptedException {
        return this.isEditable(Direction.DOWN);
    }
    
    protected boolean useBrush(Direction dir, int px, int py)
            throws LuaException, InterruptedException {
        ItemStack stack = this.getSelectedStack();
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof IPaintingTool)) {
            throw new LuaException("cannot find brush");
        }
        Picture picture = this.findPicture(dir);
        PicturePeripheral.checkPicturePixelCoords(picture, px, py);
        return picture.usePaintingTool(stack, px, py);
    }
    
    @PeripheralMethod(value = 20, mainThread = true)
    protected boolean useBrush(int px, int py) throws LuaException, InterruptedException {
        return this.useBrush(null, px, py);
    }
    
    @PeripheralMethod(value = 21, mainThread = true)
    protected boolean useBrushUp(int px, int py) throws LuaException, InterruptedException {
        return this.useBrush(Direction.UP, px, py);
    }
    
    @PeripheralMethod(value = 22, mainThread = true)
    protected boolean useBrushDown(int px, int py) throws LuaException, InterruptedException {
        return this.useBrush(Direction.DOWN, px, py);
    }
    
    @PeripheralMethod(value = 31, mainThread = true)
    protected Object[] getBrushColor() throws LuaException, InterruptedException {
        ItemStack stack = this.getSelectedStack();
        IColorizeable colorizeable = IColorizeable.getColorizeable(stack);
        if (colorizeable == null) {
            throw new LuaException("cannot find brush");
        }
        Color color = new Color(colorizeable.getColor(stack));
        return new Object[]{color.getRed(), color.getGreen(), color.getBlue()};
    }
    
    @PeripheralMethod(value = 32, mainThread = true)
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
    
    protected Picture searchPicture(Direction dir) {
        if (dir == null) {
            dir = this.turtle.getDirection();
        }
        BlockPos pos = this.turtle.getPosition().offset(dir);
        Direction pside = dir.getOpposite();
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
    
    protected Picture findPicture(Direction dir) throws LuaException {
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
