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
    
    protected boolean detectPicture(ForgeDirection dir) throws LuaException, InterruptedException {
        return this.searchPicture(dir) != null;
    }
    
    @PeripheralMethod(0)
    protected boolean detectPicture() throws LuaException, InterruptedException {
        return this.detectPicture(ForgeDirection.UNKNOWN);
    }
    
    @PeripheralMethod(1)
    protected boolean detectPictureUp() throws LuaException, InterruptedException {
        return this.detectPicture(ForgeDirection.UP);
    }
    
    @PeripheralMethod(2)
    protected boolean detectPictureDown() throws LuaException, InterruptedException {
        return this.detectPicture(ForgeDirection.DOWN);
    }
    
    protected int getWidth(ForgeDirection dir) throws LuaException, InterruptedException {
        return this.findPicture(dir).getWidth();
    }
    
    @PeripheralMethod(3)
    protected int getWidth() throws LuaException, InterruptedException {
        return this.getWidth(ForgeDirection.UNKNOWN);
    }
    
    @PeripheralMethod(4)
    protected int getWidthUp() throws LuaException, InterruptedException {
        return this.getWidth(ForgeDirection.UP);
    }
    
    @PeripheralMethod(5)
    protected int getWidthDown() throws LuaException, InterruptedException {
        return this.getWidth(ForgeDirection.DOWN);
    }
    
    protected int getHeight(ForgeDirection dir) throws LuaException, InterruptedException {
        return this.findPicture(dir).getHeight();
    }
    
    @PeripheralMethod(6)
    protected int getHeight() throws LuaException, InterruptedException {
        return this.getHeight(ForgeDirection.UNKNOWN);
    }
    
    @PeripheralMethod(7)
    protected int getHeightUp() throws LuaException, InterruptedException {
        return this.getHeight(ForgeDirection.UP);
    }
    
    @PeripheralMethod(8)
    protected int getHeightDown() throws LuaException, InterruptedException {
        return this.getHeight(ForgeDirection.DOWN);
    }
    
    protected boolean isEditable(ForgeDirection dir) throws LuaException, InterruptedException {
        return this.findPicture(dir).isEditable();
    }
    
    @PeripheralMethod(9)
    protected boolean isEditable() throws LuaException, InterruptedException {
        return this.isEditable(ForgeDirection.UNKNOWN);
    }
    
    @PeripheralMethod(10)
    protected boolean isEditableUp() throws LuaException, InterruptedException {
        return this.isEditable(ForgeDirection.UP);
    }
    
    @PeripheralMethod(11)
    protected boolean isEditableDown() throws LuaException, InterruptedException {
        return this.isEditable(ForgeDirection.DOWN);
    }
    
    protected boolean useBrush(ForgeDirection dir, int px, int py)
            throws LuaException, InterruptedException {
        ItemStack stack = this.getSelectedStack();
        if (stack == null || stack.stackSize == 0 || !(stack.getItem() instanceof IPaintingTool)) {
            throw new LuaException("cannot find brush");
        }
        Picture picture = this.findPicture(dir);
        PicturePeripheral.checkPicturePixelCoords(picture, px, py);
        return picture.usePaintingTool(stack, px, py);
    }
    
    @PeripheralMethod(20)
    protected boolean useBrush(int px, int py) throws LuaException, InterruptedException {
        return this.useBrush(ForgeDirection.UNKNOWN, px, py);
    }
    
    @PeripheralMethod(21)
    protected boolean useBrushUp(int px, int py) throws LuaException, InterruptedException {
        return this.useBrush(ForgeDirection.UP, px, py);
    }
    
    @PeripheralMethod(22)
    protected boolean useBrushDown(int px, int py) throws LuaException, InterruptedException {
        return this.useBrush(ForgeDirection.DOWN, px, py);
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
