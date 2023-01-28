package com.vanym.paniclecraft.plugins.computercraft;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;

import com.vanym.paniclecraft.core.component.painting.IColorizeable;
import com.vanym.paniclecraft.core.component.painting.IPaintingTool;
import com.vanym.paniclecraft.core.component.painting.Picture;
import com.vanym.paniclecraft.core.component.painting.WorldPicturePoint;
import com.vanym.paniclecraft.core.component.painting.WorldPictureProvider;
import com.vanym.paniclecraft.item.ItemPalette;
import com.vanym.paniclecraft.utils.MainUtils;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.util.ForgeDirection;

public class TurtlePaintBrushPeripheral extends PeripheralBase {
    
    protected final ITurtleAccess turtle;
    
    public TurtlePaintBrushPeripheral(ITurtleAccess turtle) {
        this.turtle = turtle;
    }
    
    @Override
    public String getType() {
        return "paintbrush";
    }
    
    protected int getWidth(ForgeDirection dir) throws LuaException, InterruptedException {
        return this.findPicture(dir).getWidth();
    }
    
    @PeripheralMethod(0)
    protected int getWidth() throws LuaException, InterruptedException {
        return this.getWidth(ForgeDirection.UNKNOWN);
    }
    
    @PeripheralMethod(1)
    protected int getWidthUp() throws LuaException, InterruptedException {
        return this.getWidth(ForgeDirection.UP);
    }
    
    @PeripheralMethod(2)
    protected int getWidthDown() throws LuaException, InterruptedException {
        return this.getWidth(ForgeDirection.DOWN);
    }
    
    protected int getHeight(ForgeDirection dir) throws LuaException, InterruptedException {
        return this.findPicture(dir).getHeight();
    }
    
    @PeripheralMethod(3)
    protected int getHeight() throws LuaException, InterruptedException {
        return this.getHeight(ForgeDirection.UNKNOWN);
    }
    
    @PeripheralMethod(4)
    protected int getHeightUp() throws LuaException, InterruptedException {
        return this.getHeight(ForgeDirection.UP);
    }
    
    @PeripheralMethod(5)
    protected int getHeightDown() throws LuaException, InterruptedException {
        return this.getHeight(ForgeDirection.DOWN);
    }
    
    protected boolean useBrush(ForgeDirection dir, int px, int py)
            throws LuaException, InterruptedException {
        ItemStack stack = this.getSelectedStack();
        if (stack == null || stack.stackSize == 0 || !(stack.getItem() instanceof IPaintingTool)) {
            throw new LuaException("cannot find brush");
        }
        Picture picture = this.findPicture(dir);
        if (px < 0 || px >= picture.getWidth()) {
            throw new LuaException("x must be from 0 to width");
        }
        if (py < 0 || py >= picture.getHeight()) {
            throw new LuaException("y must be from 0 to height");
        }
        picture.usePaintingTool(stack, px, py);
        return true;
    }
    
    @PeripheralMethod(6)
    protected boolean useBrush(int px, int py) throws LuaException, InterruptedException {
        return this.useBrush(ForgeDirection.UNKNOWN, px, py);
    }
    
    @PeripheralMethod(7)
    protected boolean useBrushUp(int px, int py) throws LuaException, InterruptedException {
        return this.useBrush(ForgeDirection.UP, px, py);
    }
    
    @PeripheralMethod(8)
    protected boolean useBrushDown(int px, int py) throws LuaException, InterruptedException {
        return this.useBrush(ForgeDirection.DOWN, px, py);
    }
    
    @PeripheralMethod(9)
    protected Object[] getBrushColor() throws LuaException, InterruptedException {
        ItemStack stack = this.getSelectedStack();
        IColorizeable colorizeable = IColorizeable.getColorizeable(stack);
        if (colorizeable == null) {
            throw new LuaException("cannot find brush");
        }
        Color color = MainUtils.getColorFromInt(colorizeable.getColor(stack));
        return new Object[]{color.getRed(), color.getGreen(), color.getBlue()};
    }
    
    @PeripheralMethod(10)
    protected boolean setBrushColor(int red, int green, int blue)
            throws LuaException, InterruptedException {
        if (!MainUtils.inventoryToStream(this.turtle.getInventory())
                      .anyMatch(stack->stack != null && stack.stackSize > 0
                          && stack.getItem() instanceof ItemPalette)) {
            throw new LuaException("cannot find palette");
        }
        ItemStack stack = this.getSelectedStack();
        IColorizeable colorizeable = IColorizeable.getColorizeable(stack);
        if (colorizeable == null) {
            throw new LuaException("cannot find brush");
        }
        if (red < 0 || red >= 256) {
            throw new LuaException("red must be from 0 to 255");
        }
        if (green < 0 || green >= 256) {
            throw new LuaException("green must be from 0 to 255");
        }
        if (blue < 0 || blue >= 256) {
            throw new LuaException("blue must be from 0 to 255");
        }
        colorizeable.setColor(stack, MainUtils.getIntFromRGB(red, green, blue));
        return true;
    }
    
    protected Picture searchPicture(ForgeDirection dir) {
        if (dir == ForgeDirection.UNKNOWN) {
            dir = ForgeDirection.getOrientation(this.turtle.getDirection());
        }
        ChunkCoordinates pos = this.turtle.getPosition();
        pos.posX += dir.offsetX;
        pos.posY += dir.offsetY;
        pos.posZ += dir.offsetZ;
        ForgeDirection pside = dir.getOpposite();
        for (WorldPictureProvider provider : this.getProviders()) {
            WorldPicturePoint point = new WorldPicturePoint(
                    provider,
                    this.turtle.getWorld(),
                    pos.posX,
                    pos.posY,
                    pos.posZ,
                    pside.ordinal());
            Picture picture = point.getOrCreatePicture();
            if (picture != null) {
                return picture;
            }
        }
        return null;
    }
    
    protected Picture findPicture(ForgeDirection dir) throws LuaException {
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
